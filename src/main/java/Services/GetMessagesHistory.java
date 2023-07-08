package Services;

import Utils.Base;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class GetMessagesHistory extends Base {
    public static CompletableFuture<List<TdApi.Message>> getMessages(Long chatId) {
        CompletableFuture<List<TdApi.Message>> future = new CompletableFuture<>();

        TdApi.GetChatHistory getChatHistory = new TdApi.GetChatHistory(chatId, 0, 0, 100, false);
        client.send(getChatHistory, new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                if (object instanceof TdApi.Messages) {
                    TdApi.Messages messages = (TdApi.Messages) object;
                    List<TdApi.Message> messageList = Arrays.asList(messages.messages);
                    future.complete(messageList);
                } else {
                    future.completeExceptionally(new RuntimeException("Failed to get messages"));
                }
            }
        });

        return future.thenCompose(messages -> {
            int offset = (int) messages.get(messages.size() - 1).id; // get the ID of the last message
            TdApi.GetChatHistory getChatHistory2 = new TdApi.GetChatHistory(chatId, offset, 0, 100, false);
            CompletableFuture<List<TdApi.Message>> future2 = new CompletableFuture<>();
            client.send(getChatHistory2, new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.Object object) {
                    if (object instanceof TdApi.Messages) {
                        TdApi.Messages messages = (TdApi.Messages) object;
                        List<TdApi.Message> messageList = Arrays.asList(messages.messages);
                        future2.complete(messageList);
                    } else {
                        future2.completeExceptionally(new RuntimeException("Failed to get messages"));
                    }
                }
            });
            return future2;
        });
    }
    public static void printMessages(Long chatId) throws ExecutionException, InterruptedException, IOException {
    }
}