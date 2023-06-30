package Services;

import Utils.Base;
import Utils.ChatOrder;
import Utils.Print;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetMainChatList extends Base {
    public static long[] chatIds;
    public static void getMainChatList(final int limit) {
        synchronized (mainChatList) {
            if (!haveFullMainChatList && limit > mainChatList.size()) {
                // send LoadChats request if there are some unknown chats and have not enough known chats
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
                                    System.err.println("Receive an error for LoadChats:" + newLine + object);
                                }
                                break;
                            case TdApi.Ok.CONSTRUCTOR:
                                // chats had already been received through updates, let's retry request
                                getMainChatList(limit);
                                break;
                            default:
                                System.err.println("Receive wrong response from TDLib:" + newLine + object);
                        }
                    }
                });
                return;
            }
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

    public static void loadChatIds() {
        // Create a ChatList object
        TdApi.ChatList chatList = new TdApi.ChatListMain();

        // Send a request to get all the chats
        client.send(new TdApi.GetChats(chatList, 50), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                if (object instanceof TdApi.Chats) {
                    // Get the list of chat IDs and print them to the console
                    chatIds =((TdApi.Chats) object).chatIds;
                }
            }
        });
    }
}
