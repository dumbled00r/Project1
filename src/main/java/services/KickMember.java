package services;

import utils.Base;
import utils.FileLogger;
import org.drinkless.tdlib.TdApi;

import java.util.concurrent.CompletableFuture;

public class KickMember extends Base {
    public static CompletableFuture<Void> kickMember(Long chatId, Long userId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        client.send(new TdApi.BanChatMember(chatId, new TdApi.MessageSenderChat(userId), 0, false), object -> {
            if (object instanceof TdApi.Error) {
                System.err.println("Failed to kick member: " + ((TdApi.Error) object).message);
                FileLogger.write("Failed to kick member: " + ((TdApi.Error) object).message);
            }
            else {
                System.out.println("\033[0;92mMember kicked successfully\033[0m");
            }
            future.complete(null);
        });
        return future;
    }
}
