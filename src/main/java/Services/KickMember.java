package Services;

import Utils.Base;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import static Utils.ConvertToLong.toLong;

public class KickMember extends Base {
    public static void kickMember(Long chatId, Long userId) {
        client.send(new TdApi.BanChatMember(chatId, new TdApi.MessageSenderChat(userId), 0, false), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object)
            {
                if (object.getConstructor() == TdApi.Error.CONSTRUCTOR) System.err.println(((TdApi.Error) object).message);
                else System.out.println("Member kicked successfully");
            }
        });
    }
}
