package services;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.Arrays;
import java.util.List;

public class GetUser extends Base {
    /**
     *
     * Get single user
     */
    public static void getUser(String args) {
        TdApi.GetUser getUser = new TdApi.GetUser(ConvertToLong.toLong(args));
        client.send(getUser, object -> {
            if (object.getConstructor() == TdApi.User.CONSTRUCTOR) {
                TdApi.User user = (TdApi.User) object;
                long id = user.id;
                String firstName = user.firstName;
                String lastName = user.lastName;
                String userName = user.usernames.activeUsernames[0];
                System.out.println("\nUser ID: " + id);
                System.out.println("Username: " + userName);
                System.out.println("First Name: " + firstName);
                System.out.println("Last Name: " + lastName + "\n");
            } else {
                System.out.println("Failed to get user: " + object);
            }
        });
    }
    public static void getMassUser(List<Long> userIds){
        for (Long userId : userIds){
            getUser(Long.toString(userId));
        }
    }
}
