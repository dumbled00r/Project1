package Services;

import Utils.Base;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.drinkless.tdlib.TdApi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GetMessagesHistory extends Base {
    public static CompletableFuture<List<TdApi.Message>> getMessages(Long chatId) {
        final int messageLimit = 100;
        final AtomicReference<Integer> offset = new AtomicReference<>(0);

        CompletableFuture<List<TdApi.Message>> future = new CompletableFuture<>();

        AtomicBoolean errorOccurred = new AtomicBoolean(false);

        List<TdApi.Message> messagesList = new ArrayList<>();

        CompletableFuture<Void> getNextBatchFuture = CompletableFuture.completedFuture(null);

        do {
            TdApi.GetChatHistory getChatHistory = new TdApi.GetChatHistory(chatId, offset.get(), 0, messageLimit, false);

            CompletableFuture<Void> requestFuture = new CompletableFuture<>();

            client.send(getChatHistory, object -> {
                if (object instanceof TdApi.Messages) {
                    TdApi.Messages messages = (TdApi.Messages) object;
                    // handle messages here
                    for (int i = 0; i < messages.messages.length; i++) {
                        TdApi.Message message = messages.messages[i];
                        messagesList.add(message);
                    }

                    offset.updateAndGet(oldOffset -> oldOffset + messages.messages.length);
                } else {
                    System.err.println("Error getting chat history: " + object);
                    errorOccurred.set(true);
                    future.completeExceptionally(new RuntimeException("Error getting chat history"));
                }

                requestFuture.complete(null);
            });

            getNextBatchFuture = getNextBatchFuture.thenCompose(ignore -> requestFuture);

        } while (offset.get() >= 0 && offset.get() % messageLimit == 0 && !errorOccurred.get());

        getNextBatchFuture.whenComplete((result, error) -> {
            if (error != null) {
                System.err.println("Error getting chat history: " + error.getMessage());
                errorOccurred.set(true);
                future.completeExceptionally(new RuntimeException("Error getting chat history"));
            } else {
                future.complete(messagesList);
            }
        });
        return future;
    }

    public static void printMessages(Long chatId) throws ExecutionException, InterruptedException, IOException {
        List<TdApi.Message> messagesList = getMessages(chatId).get();
        FileWriter fileWriter = new FileWriter("messages.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for (TdApi.Message message : messagesList) {
            if (message.senderId instanceof TdApi.MessageSenderUser) {
                String senderId = String.valueOf(((TdApi.MessageSenderUser) message.senderId).userId);
            }
            if (message.content instanceof TdApi.MessageText) {
                TdApi.FormattedText formattedText = ((TdApi.MessageText) message.content).text;
                String text = formattedText.text;
                System.out.println("Message text: " + text);
            }
            String text = message.content.toString();
            bufferedWriter.write(text);
            bufferedWriter.newLine();
        }

        bufferedWriter.close();
        fileWriter.close();
    }
}