package Services;

import Utils.Base;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
public class SendMessage extends Base {
    public static void sendMessage(long chatId, String message) {
        if (message.equals("")) {
            System.err.println("Message must not be empty");
        }
        TdApi.InputMessageContent messageContent = new TdApi.InputMessageText(new TdApi.FormattedText(message, null), false, true);
        client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, messageContent), object -> {
            if (object instanceof TdApi.Message) {
                // The message was sent successfully
                System.err.println("Message sent successfully");
            } else if (object instanceof TdApi.Error) {
                // Failed to send the message
                System.err.println("Failed to send message: " + ((TdApi.Error) object).message);
            } else {
                // Unexpected result object
                System.err.println("Unexpected result object: " + ((TdApi.Error) object).message);
            }
        });
    }
}
