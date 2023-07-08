package Utils;

import org.drinkless.tdlib.TdApi;
public class SetChatPosition extends Base {
    private static final Object lock = new Object();

    public static void setChatPositions(TdApi.Chat chat, TdApi.ChatPosition[] positions) {
        synchronized (lock) {
             {
                for (TdApi.ChatPosition position : chat.positions) {
                    if (position.list.getConstructor() == TdApi.ChatListMain.CONSTRUCTOR) {
                        boolean isRemoved = mainChatList.remove(new ChatOrder.OrderedChat(chat.id, position));
                        assert isRemoved;
                    }
                }
                chat.positions = positions;
                for (TdApi.ChatPosition position : chat.positions) {
                    if (position.list.getConstructor() == TdApi.ChatListMain.CONSTRUCTOR) {
                        boolean isAdded = mainChatList.add(new ChatOrder.OrderedChat(chat.id, position));
                        assert isAdded;
                    }
                }
            }
        }
    }
}
