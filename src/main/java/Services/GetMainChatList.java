package Services;

import Utils.Base;
import Utils.ChatOrder;
import Utils.Print;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GetMainChatList extends Base {
    public static long[] chatIds;
    public static CompletableFuture<Void> getMainChatListAsync(final int limit) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (haveFullMainChatList || limit <= mainChatList.size()) {
            // If we already have the full main chat list, or if the limit is less than or equal to the size of the main chat list,
            // we can return the list immediately.
            getMainChatList(limit);
            future.complete(null);
        } else {
            // Otherwise, we need to send a LoadChats request to TDLib to get more chats.
            client.send(new TdApi.LoadChats(new TdApi.ChatListMain(), limit - mainChatList.size()), new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.Object object) {
                    switch (object.getConstructor()) {
                        case TdApi.Error.CONSTRUCTOR:
                            if (((TdApi.Error) object).code == 404) {
                                synchronized (mainChatList) {
                                    haveFullMainChatList = true;
                                }
                            } else {
                                System.err.println("\nReceive an error for LoadChats:" + ((TdApi.Error) object).message);
                            }
                            future.completeExceptionally(new RuntimeException("Error loading chats"));
                            break;
                        case TdApi.Ok.CONSTRUCTOR:
                            // Chats have already been received through updates, so retry the request.
                            getMainChatListAsync(limit).thenAccept((Void v) -> future.complete(null));
                            break;
                        default:
                            System.err.println("\nReceive wrong response from TDLib:" + ((TdApi.Error) object).message);
                            future.completeExceptionally(new RuntimeException("Error loading chats"));
                    }
                }
            });
        }
        return future;
    }

    public static void getMainChatList(int limit) {
        synchronized (mainChatList) {
            int numberOfChats = limit;
            Iterator<ChatOrder.OrderedChat> iter = mainChatList.iterator();
            System.out.println();
            if (limit >= mainChatList.size()){
                numberOfChats = mainChatList.size();
            }
            System.out.println("First " + numberOfChats + " chat(s) out of " + mainChatList.size() + " known chat(s):");
            for (int i = 0; i < limit && i < mainChatList.size(); i++) {
                long chatId = iter.next().chatId;
                TdApi.Chat chat = chats.get(chatId);
                synchronized (chat) {
                    System.out.println(chatId + ": " + chat.title);
                }
            }
            Print.print("");
        }
    }

    public static CompletableFuture<Void> loadChatIdsAsync() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        // Create a ChatList object
        TdApi.ChatList chatList = new TdApi.ChatListMain();
        // Send a request to get all the chats
        client.send(new TdApi.GetChats(chatList, 50), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                if (object instanceof TdApi.Chats) {
                    // Get the list of chat IDs and print them to the console
                    chatIds = ((TdApi.Chats) object).chatIds;
                    future.complete(null);
                } else {
                    future.completeExceptionally(new RuntimeException("Error loading chat IDs: " + ((TdApi.Error) object).message));
                }
            }
        });
        return future;
    }
}