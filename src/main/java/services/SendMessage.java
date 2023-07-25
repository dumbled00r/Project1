package services;

import utils.Base;
import utils.Print;
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
                System.out.println("\nMessage sent successfully");
                Print.print("");
            } else if (object instanceof TdApi.Error) {
                // Failed to send the message
                System.err.println("Failed to send message: " + ((TdApi.Error) object).message);
                Print.print("");
            } else {
                // Unexpected result object
                System.err.println("Unexpected result object: " + ((TdApi.Error) object).message);
                Print.print("");
            }
        });
    }
}
