package utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;


public class Authorize extends Base{
    public static void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) throws ExecutionException, InterruptedException, IOException {
        if (authorizationState != null) {
            Base.authorizationState = authorizationState;
        }
        switch (Base.authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                Gson gson = new Gson();
                Path filePath = Paths.get("td.json");
                if (!Files.exists(filePath)) {
                    throw new FileNotFoundException("File not found: " + filePath);
                }
                String jsonString = new String(Files.readAllBytes(filePath));
                JsonObject json = gson.fromJson(jsonString, JsonObject.class);
                TdApi.SetTdlibParameters request = new TdApi.SetTdlibParameters();
                request.databaseDirectory = "tdlib";
                request.useMessageDatabase = true;
                request.useSecretChats = true;
                request.apiId = json.get("apiId").getAsInt();
                request.apiHash = json.get("apiHash").getAsString();
                request.systemLanguageCode = "en";
                request.deviceModel = "Desktop";
                request.applicationVersion = "1.0";
                request.enableStorageOptimizer = true;

                client.send(request, new Handler.AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
                System.out.println();
                String phoneNumber = String.valueOf(PromptString.promptStringAsync("Please enter phone number: ").get());
                client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null), new Handler.AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR: {
                String link = ((TdApi.AuthorizationStateWaitOtherDeviceConfirmation) Base.authorizationState).link;
                System.out.println("Please confirm this login link on another device: " + link);
                break;
            }
            case TdApi.AuthorizationStateWaitEmailAddress.CONSTRUCTOR: {
                String emailAddress = String.valueOf(PromptString.promptStringAsync("Please enter email address: ").get());
                client.send(new TdApi.SetAuthenticationEmailAddress(emailAddress), new Handler.AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitEmailCode.CONSTRUCTOR: {
                String code = String.valueOf(PromptString.promptStringAsync("Please enter email authentication code: ").get());
                client.send(new TdApi.CheckAuthenticationEmailCode(new TdApi.EmailAddressAuthenticationCode(code)), new Handler.AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR: {
                String code = String.valueOf(PromptString.promptStringAsync("Please enter authentication code: ").get());
                client.send(new TdApi.CheckAuthenticationCode(code), new Handler.AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR: {
                String firstName = String.valueOf(PromptString.promptStringAsync("Please enter your first name: ").get());
                String lastName = String.valueOf(PromptString.promptStringAsync("Please enter your last name: "));
                client.send(new TdApi.RegisterUser(firstName, lastName), new Handler.AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR: {
                String password = String.valueOf(PromptString.promptStringAsync("Please enter password: ").get());
                client.send(new TdApi.CheckAuthenticationPassword(password), new Handler.AuthorizationRequestHandler());
                break;
            }
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                haveAuthorization = true;
                authorizationLock.lock();
                try {
                    gotAuthorization.signal();
                } finally {
                    authorizationLock.unlock();
                }
                break;
            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                haveAuthorization = false;
                break;
            case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
                haveAuthorization = false;
                break;
            case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
                if (!needQuit) {
                    client = Client.create(new Handler.UpdateHandler(), null, null); // recreate client after previous has closed
                } else {
                    canQuit = true;
                }
                break;
            default:
                System.err.println("Unsupported authorization state:" + newLine + Base.authorizationState);
        }
    }
    protected static void onFatalError(String errorMessage) {
        final class ThrowError implements Runnable {
            private final String errorMessage;
            private final AtomicLong errorThrowTime;
            private ThrowError(String errorMessage, AtomicLong errorThrowTime) {
                this.errorMessage = errorMessage;
                this.errorThrowTime = errorThrowTime;
            }
            @Override
            public void run() {
                if (isDatabaseBrokenError(errorMessage) || isDiskFullError(errorMessage) || isDiskError(errorMessage)) {
                    processExternalError();
                    return;
                }

                errorThrowTime.set(System.currentTimeMillis());
                throw new ClientError("TDLib fatal error: " + errorMessage);
            }
            private void processExternalError() {
                errorThrowTime.set(System.currentTimeMillis());
                throw new ExternalClientError("Fatal error: " + errorMessage);
            }
            final class ClientError extends Error {
                private ClientError(String message) {
                    super(message);
                }
            }
            final class ExternalClientError extends Error {
                public ExternalClientError(String message) {
                    super(message);
                }
            }
            private boolean isDatabaseBrokenError(String message) {
                return message.contains("Wrong key or database is corrupted") ||
                        message.contains("SQL logic error or missing database") ||
                        message.contains("database disk image is malformed") ||
                        message.contains("file is encrypted or is not a database") ||
                        message.contains("unsupported file format") ||
                        message.contains("Database was corrupted and deleted during execution and can't be recreated");
            }
            private boolean isDiskFullError(String message) {
                return message.contains("PosixError : No space left on device") ||
                        message.contains("database or disk is full");
            }
            private boolean isDiskError(String message) {
                return message.contains("I/O error") || message.contains("Structure needs cleaning");
            }
        }
        final AtomicLong errorThrowTime = new AtomicLong(Long.MAX_VALUE);
        new Thread(new ThrowError(errorMessage, errorThrowTime), "TDLib fatal error thread").start();
        // wait at least 10 seconds after the error is thrown
        while (errorThrowTime.get() >= System.currentTimeMillis() - 10000) {
            try {
                Thread.sleep(1000 /* milliseconds */);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
