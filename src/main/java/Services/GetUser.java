package Services;

import AirTableUtils.AirTable;
import Utils.Base;
import Utils.ConvertToLong;
import org.drinkless.tdlib.TdApi;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

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
    public static List<JsonObject> getMassUser(List<Long> userIds, Long chatId) throws InterruptedException {
        for (Long userId : userIds){
            JsonObject data = getUser(userId, chatId);
            if (!data.isJsonNull()) {
                lstJsonResults.add(data);
            }
        }
        return lstJsonResults;
    }
}