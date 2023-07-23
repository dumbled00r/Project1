import AirTableUtils.SyncToAirTable;
import Services.GetCommand;
import Utils.Base;
import Utils.Handler;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.io.IOError;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Synchronize extends Base {
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        // set log message handler to handle only fatal errors (0) and plain log messages (-1)
        Client.setLogMessageHandler(0, new Handler.LogMessageHandler());

        // disable TDLib log and redirect fatal errors and plain log messages to a file
        Client.execute(new TdApi.SetLogVerbosityLevel(0));
        if (Client.execute(new TdApi.SetLogStream(new TdApi.LogStreamFile("tdlib.log", 1 << 27, false))) instanceof TdApi.Error) {
            throw new IOError(new IOException("Write access to the current directory is required"));
        }
        String blue = "\033[34m";
        String reset = "\033[0m";

        System.out.println(blue + "  ______     __   ______            __    " + reset);
        System.out.println(blue + " /_  __/__  / /__/_  __/___  ____  / /____" + reset);
        System.out.println(blue + "  / / / _ \\/ / _ \\/ / / __ \\/ __ \\/ / ___/" + reset);
        System.out.println(blue + " / / /  __/ /  __/ / / /_/ / /_/ / (__  ) " + reset);
        System.out.println(blue + "/_/  \\___/_/\\___/_/  \\____/\\____/_/____/  " + reset);


        // create client
        client = Client.create(new Handler.UpdateHandler(), null, null);
//         main loop
        while (!needQuit) {
            // await authorization
            authorizationLock.lock();
            try {
                while (!haveAuthorization) {
                    gotAuthorization.await();
                    System.out.println("\nTrying to login, if this message persists, please double-check your telegram " +
                            "credentials");
                }
            } finally {
                authorizationLock.unlock();
                System.out.println("\nSuccessfully logged in!");
            }
            SyncToAirTable.syncToAirTable();
            canQuit = true;
            needQuit = true;
        }

        while (!canQuit) {
            Thread.sleep(20);
        }
    }
}