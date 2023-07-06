package Services;

import AirTableUtils.AirTable;
import Utils.Base;
import Utils.ConvertToLong;
import org.drinkless.tdlib.TdApi;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class GetUser extends Base {
    private static List<JsonObject> lstJsonResults = new ArrayList<>();

    /**
     *
     * Get single user
     */
    public static JsonObject getUser(Long args, long chatId) {
        JsonObject jsonResults = new JsonObject();
        TdApi.GetUser getUser = new TdApi.GetUser(args);
        client.send(getUser, object -> {
            if (object.getConstructor() == TdApi.User.CONSTRUCTOR) {
                TdApi.User user = (TdApi.User) object;
                long id = user.id;
                String firstName = user.firstName;
                String lastName = user.lastName;
                String userName;
                if (user.usernames != null){
                    userName = user.usernames.activeUsernames[0];
                } else {
                    userName = null;
                }
                jsonResults.addProperty("Id", id);
                jsonResults.addProperty("Username", userName);
                jsonResults.addProperty("First Name", firstName);
                jsonResults.addProperty("Last Name", lastName);
                jsonResults.addProperty("Chat Id", chatId);
            } else {
                System.out.println("Failed to get user: " + object);
            }
        });
        return jsonResults;
    }

    /**
     * This is only for saving data usage
     * @param userIds
     * @param chatId
     */
    private static final ReentrantLock lock = new ReentrantLock();

    public static List<JsonObject> getMassUser(List<Long> userIds, Long chatId) throws InterruptedException {
        for (Long userId : userIds) {
            JsonObject data = getUser(userId, chatId);
            if (!data.isJsonNull()) {
                lstJsonResults.add(data);
            }
        }
        if (!lstJsonResults.isEmpty()) {
            System.out.println("User information:");
            System.out.println("+------+----------------+---------------+---------------+-----------------+");
            System.out.println("|  ID  |    Username    |   First Name  |   Last Name   |     Chat ID     |");
            System.out.println("+------+----------------+---------------+---------------+-----------------+");
            for (JsonObject result : lstJsonResults) {
                long id = result.get("Id").getAsLong();
                String username = result.get("Username").getAsString();
                String firstName = result.get("First Name").getAsString();
                String lastName = result.get("Last Name").getAsString();
                long chatIdValue = result.get("Chat Id").getAsLong();
                System.out.printf("| %-4d | %-14s | %-13s | %-13s | %-15d |\n", id, username, firstName, lastName, chatIdValue);
            }
            System.out.println("+------+----------------+---------------+---------------+-----------------+");
        } else {
            System.out.println("No user information available");
        }
        return lstJsonResults;
    }
}