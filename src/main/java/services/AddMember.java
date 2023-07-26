package services;

import utils.Base;
import utils.FileLogger;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AddMember extends Base {
    /**
     * Add a single (in contact) user to a group chat
     * @param chatId
     * @param userId
     * @return CompletableFuture<Void>
     */
    public static CompletableFuture<Void> addMember(Long chatId, Long userId){
        CompletableFuture<Void> future = new CompletableFuture<>();
        client.send(new TdApi.AddChatMember(chatId, userId, 0), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) throws IOException, InterruptedException, ExecutionException {
                if (object instanceof TdApi.Error) {
                    System.err.println("\nFailed to add member: " + ((TdApi.Error) object).message);
                    FileLogger.write("\nFailed to add member: " + ((TdApi.Error) object).message);
                }
                else {
                    System.out.println("Added successfully");
                }
                future.complete(null);
            }
        });
        return future;
    }
}