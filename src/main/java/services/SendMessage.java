package services;

import utils.Base;
import utils.Print;
import org.drinkless.tdlib.TdApi;

import java.util.concurrent.CompletableFuture;

public class SendMessage extends Base {
    public static CompletableFuture<Void> sendMessage(long chatId, String message) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (message.equals("")) {
            System.err.println("Message must not be empty");
            future.complete(null);
        } else {
            TdApi.InputMessageContent messageContent = new TdApi.InputMessageText(new TdApi.FormattedText(message, null), false, true);
            client.send(new TdApi.SendMessage(chatId, 0, 0, null, null, messageContent), object -> {
                if (object instanceof TdApi.Message) {
                    // The message was sent successfully
                    System.out.println("Message sent successfully");
                } else if (object instanceof TdApi.Error) {
                    // Failed to send the message
                    System.err.println("Failed to send message: " + ((TdApi.Error) object).message);
                } else {
                    // Unexpected result object
                    System.err.println("Unexpected result object: " + ((TdApi.Error) object).message);
                }
                future.complete(null);
            });
        }
        return future;
    }
}