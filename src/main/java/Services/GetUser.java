package Services;

import AirTableUtils.AirTable;
import Models.User;
import Utils.Base;
import Utils.Print;
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
    public static CompletableFuture<User> getUser(Long args, long chatId) {
        CompletableFuture<User> future = new CompletableFuture<>();
        TdApi.GetUser getUser = new TdApi.GetUser(args);
        client.send(getUser, object -> {
            if (object.getConstructor() == TdApi.User.CONSTRUCTOR) {
                TdApi.User tdUser = (TdApi.User) object;
                long id = tdUser.id;
                String firstName = tdUser.firstName;
                String lastName = tdUser.lastName;
                String username = tdUser.usernames != null ? tdUser.usernames.activeUsernames[0] : "";
                String type = "Regular"; // default to Regular
                if (tdUser.type.getConstructor() == TdApi.UserTypeBot.CONSTRUCTOR) {
                    type = "Bot"; // update to Bot if user is a bot
                }
                User user = new User(id, username, firstName, lastName, chatId, type); // pass in type
                future.complete(user);
            } else {
                String errorMessage = "Failed to get user: " + object;
                System.err.println(errorMessage);
                future.completeExceptionally(new RuntimeException(errorMessage));
            }
        });
        return future;
    }

    /**
     * Get multiple users of a group then print out!
     */
    public static CompletableFuture<List<User>> getMassUser(List<Long> userIds, Long chatId) {
        List<CompletableFuture<User>> futures = new ArrayList<>();
        for (Long userId : userIds) {
            futures.add(getUser(userId, chatId)
                    .exceptionally(throwable -> {
                        System.err.println("Failed to get user: " + throwable.getMessage());
                        return null;
                    }));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .thenApply(v -> {
                    List<User> results = new ArrayList<>();
                    for (CompletableFuture<User> future : futures) {
                        User user = future.join();
                        if (user != null) {
                            results.add(user);
                        }
                    }
                    if (!results.isEmpty()) {
                        System.out.println("\nUser information:");
                        System.out.println("+-----------------+-----------------+--------------------------------+--------------------------------+-----------------+------------+");
                        System.out.println("|        ID       |     Username    |           First Name           |         Last Name              |     Chat ID     |  User Type |");
                        System.out.println("+-----------------+-----------------+--------------------------------+--------------------------------+-----------------+------------+");
                        for (User user : results) {
                            long id = user.getId();
                            String username = user.getUsername();
                            String firstName = user.getFirstName();
                            String lastName = user.getLastName();
                            long chatIdValue = user.getChatId();
                            String type = user.getType(); // new field
                            System.out.printf("| %-15d | %-15s | %-30s | %-30s | %-15d | %-10s |\n", id, username != "" ? username : "", firstName, lastName, chatIdValue, type);
                        }
                        System.out.println("+-----------------+-----------------+--------------------------------+--------------------------------+-----------------+------------+");
                    } else {
                        System.out.println("No user information available");
                    }
                    Print.print("");
                    return results;
                });
    }
}