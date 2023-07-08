package Services;

import Utils.Base;
import Utils.Print;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GetMessagesHistory extends Base {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static CompletableFuture<List<TdApi.Message>> getMessages(Long chatId) {
        CompletableFuture<List<TdApi.Message>> future = new CompletableFuture<>();

        int limit = 100;
        final int[] offset = {0};
        final boolean[] reachedEnd = {false};
        Set<Long> receivedMessageIds = new HashSet<>();
        List<TdApi.Message> messagesList = new ArrayList<>();

        while(!reachedEnd[0]) {
            TdApi.GetChatHistory getChatHistory = new TdApi.GetChatHistory(chatId, 0, offset[0], limit, false);
            client.send(getChatHistory, new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.Object object) {
                    if (object instanceof TdApi.Messages) {
                        TdApi.Messages messages = (TdApi.Messages) object;
                        for (TdApi.Message message : messages.messages) {
                            if (!receivedMessageIds.contains(message.id)) {
                                messagesList.add(message);
                                receivedMessageIds.add(message.id);
                            }
                        }
                        if (messages.messages.length < limit) {
                            reachedEnd[0] = true;
                            future.complete(messagesList);
                        } else {
                            offset[0] += limit;
                        }
                    } else {
                        future.completeExceptionally(new RuntimeException("Failed to get messages"));
                    }
                }
            });
        }

        return future;
    }

    public static void printMessages(Long chatId) {
        getMessages(chatId).thenAcceptAsync(messages -> {
            System.out.println("Total messages: " + messages.size());
            for (TdApi.Message message : messages) {
                System.out.println(message.content);
            }
            Print.print("");
        }, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS, scheduler));
    }

}