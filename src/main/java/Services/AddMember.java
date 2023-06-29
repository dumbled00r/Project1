package Services;

import Utils.Base;
import org.drinkless.tdlib.TdApi;

import java.util.List;

public class AddMember extends Base {
    /**
     * Add a single user to a group chat
     * @param chatId
     * @param userId
     *
     */
    public static void addSingleUser(Long chatId, Long userId){
        client.send(new TdApi.AddChatMember(chatId, userId, 9999), defaultHandler);
    }

    /**
     * Add multiple users to a group chat
     * @param chatId
     * @param userId
     */
    public static void addMassUser(Long chatId, List<Long> userId){
        long[] aUserId = userId.stream().mapToLong(Long::longValue).toArray();
        client.send(new TdApi.AddChatMembers(chatId, aUserId), defaultHandler);
    }
}
