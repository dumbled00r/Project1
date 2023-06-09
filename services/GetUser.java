package services;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

public class GetUser extends Base {
    public static void getUser(String[] args) {
        TdApi.GetUser getUser = new TdApi.GetUser(ConvertToLong.toLong(args[0]));
        client.send(getUser, object -> {
            if (object.getConstructor() == TdApi.User.CONSTRUCTOR) {
                TdApi.User user = (TdApi.User) object;
                long id = user.id;
                String firstName = user.firstName;
                String lastName = user.lastName;
                String languageCode = user.languageCode;
                String userName = user.usernames.activeUsernames[0];
                System.out.println("User ID: " + id);
                System.out.println("Username: " + userName);
                System.out.println("First Name: " + firstName);
                System.out.println("Last Name: " + lastName);
                System.out.println("Language Code: " + languageCode);
            } else {
                System.out.println("Failed to get user: " + object);
            }
        });
    }
}
