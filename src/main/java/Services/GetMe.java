package Services;

import Utils.Base;
import Utils.Print;
import com.google.gson.JsonObject;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class GetMe extends Base {

    public static CompletableFuture<JsonObject> getMe() {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();

        // Create a TdApi.GetMe object to send to the client
        TdApi.GetMe getMe = new TdApi.GetMe();

        // Send the TdApi.GetMe object to the client
        client.send(getMe, object -> {
            // Handle the result of the TdApi.GetMe request
            if (object.getConstructor() == TdApi.User.CONSTRUCTOR) {
                // If the result is a TdApi.User object, create a JsonObject and add the user's information to it
                TdApi.User user = (TdApi.User) object;
                JsonObject userJson = new JsonObject();
                userJson.addProperty("Id", user.id);
                userJson.addProperty("First Name", user.firstName);
                userJson.addProperty("Last Name", user.lastName);
                String userName = user.usernames != null ? user.usernames.activeUsernames[0] : "";
                userJson.addProperty("User Name", userName);
                userJson.addProperty("Phone Number", user.phoneNumber);
                future.complete(userJson);
            } else {
                // If the result is not a TdApi.User object, complete the future exceptionally with an error message
                String errorMessage = "Failed to get user: " + ((TdApi.Error) object).message;
                System.err.println(errorMessage);
                future.completeExceptionally(new RuntimeException(errorMessage));
            }
        }, null);
        return future;
    }
    public static void printMyInfo() {
        try {
            // Call the getMe method to get the user information
            JsonObject userJson = GetMe.getMe().get();

            // Print out the user information in a tabular form
            String tableFormat = "%-15s %-20s%n";
            System.out.println("\nYour Information:");
            System.out.println("-------------------------------------------------");
            System.out.printf(tableFormat, "Id:", userJson.get("Id").getAsInt());
            System.out.printf(tableFormat, "First Name:", userJson.get("First Name").getAsString());
            System.out.printf(tableFormat, "Last Name:", userJson.get("Last Name").getAsString());
            System.out.printf(tableFormat, "User Name:", userJson.get("User Name").getAsString());
            System.out.printf(tableFormat, "Phone Number: ", "+" + userJson.get("Phone Number").getAsString() );
            System.out.println("-------------------------------------------------");
            Print.print("");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
