package Services;

import Utils.Base;
import Utils.ConvertToLong;
import org.drinkless.tdlib.TdApi;
import com.google.gson.*;

import java.util.List;



public class GetUser extends Base {
    /**
     *
     * Get single user
     */
    public static void getUser(String args, Long chatId) {
        TdApi.GetUser getUser = new TdApi.GetUser(ConvertToLong.toLong(args));
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
                JsonObject json = new JsonObject();
                json.addProperty("User Id", id);
                json.addProperty("Username", userName);
                json.addProperty("First Name", firstName);
                json.addProperty("Last Name", lastName);
                json.addProperty("Chat Id", chatId);
                System.out.println(json);
//                AirTable airTable = new AirTable();
//                airTable.pushUserData(json);
            } else {
                System.out.println("Failed to get user: " + object);
            }
        });
    }

    /**
     * This is only for saving data usage
     * @param userIds
     * @param chatId
     */
    public static void getMassUser(List<Long> userIds, Long chatId){
        for (Long userId : userIds){
            getUser(Long.toString(userId), chatId);
        }
    }
}
