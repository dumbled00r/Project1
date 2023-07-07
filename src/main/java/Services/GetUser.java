package Services;

import AirTableUtils.AirTable;
import Utils.Base;
import org.drinkless.tdlib.TdApi;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GetUser extends Base {

    /**
     * Get single user
     */
    public static CompletableFuture<JsonObject> getUser(Long args, long chatId) {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        TdApi.GetUser getUser = new TdApi.GetUser(args);
        client.send(getUser, object -> {
            JsonObject jsonResults = new JsonObject();
            if (object.getConstructor() == TdApi.User.CONSTRUCTOR) {
                TdApi.User user = (TdApi.User) object;
                long id = user.id;
                String firstName = user.firstName;
                String lastName = user.lastName;
                String userName = user.usernames != null ? user.usernames.activeUsernames[0] : "";
                jsonResults.addProperty("Id", id);
                jsonResults.addProperty("Username", userName);
                jsonResults.addProperty("First Name", firstName);
                jsonResults.addProperty("Last Name", lastName);
                jsonResults.addProperty("Chat Id", chatId);
                future.complete(jsonResults);
            } else {
                String errorMessage = "Failed to get user: " + object;
                System.err.println(errorMessage);
                future.completeExceptionally(new RuntimeException(errorMessage));
            }
        });
        return future;
    }

    /**
     * Get multiple users
     */
    public static CompletableFuture<List<JsonObject>> getMassUser(List<Long> userIds, Long chatId) {
        List<CompletableFuture<JsonObject>> futures = new ArrayList<>();
        for (Long userId : userIds) {
            futures.add(getUser(userId, chatId)
                    .thenApplyAsync(data -> {
                        if (data != null && data.has("Id") && data.has("Username") && data.has("First Name") && data.has("Last Name") && data.has("Chat Id")) {
                            return data;
                        } else {
                            return null;
                        }
                    })
                    .exceptionally(throwable -> {
                        System.err.println("Failed to get user: " + throwable.getMessage());
                        return null;
                    }));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .thenApply(v -> {
                    List<JsonObject> results = new ArrayList<>();
                    for (CompletableFuture<JsonObject> future : futures) {
                        JsonObject data = future.join();
                        if (data != null) {
                            results.add(data);
                        }
                    }
                    if (!results.isEmpty()) {
                        System.out.println("User information:");
                        System.out.println("+-----------------+-----------------+--------------------------------+--------------------------------+-----------------+");
                        System.out.println("|        ID       |     Username    |           First Name           |         Last Name              |      Chat ID    |");
                        System.out.println("+-----------------+-----------------+--------------------------------+--------------------------------+-----------------+");
                        for (JsonObject result : results) {
                            long id = result.get("Id").getAsLong();
                            String username = result.get("Username").getAsString();
                            String firstName = result.get("First Name").getAsString();
                            String lastName = result.get("Last Name").getAsString();
                            long chatIdValue = result.get("Chat Id").getAsLong();
                            System.out.printf("| %-15d | %-15s | %-30s | %-30s | %-15d |\n", id, username != "" ? username : "N/A", firstName, lastName, chatIdValue);
                        }
                        System.out.println("+-----------------+-----------------+--------------------------------+--------------------------------+-----------------+");
                    } else {
                        System.out.println("No user information available");
                    }
                    return results;
                });
    }
}