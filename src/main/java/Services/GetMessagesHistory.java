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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
                    } else if (object instanceof TdApi.Error) {
                        future.completeExceptionally(new RuntimeException("Failed to get messages: " + ((TdApi.Error) object).message));
                    }
                }
            });
        }

        return future;
    }

    public static void printMessages(Long chatId) {
        String fileName = "messages_" + chatId + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            getMessages(chatId).thenAcceptAsync(messages -> {
                try {
                    writer.write("Total messages: " + messages.size() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (TdApi.Message message : messages) {
                    try {
                        writer.write(message.content + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS, scheduler)).join();
            System.out.println("Message retrieved successfully, saved in file: "+ fileName);
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }
}