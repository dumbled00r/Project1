package services;

import utils.Base;
import utils.FileLogger;
import utils.Print;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

public class KickMember extends Base {
    public static void kickMember(Long chatId, Long userId) {
        client.send(new TdApi.BanChatMember(chatId, new TdApi.MessageSenderChat(userId), 0, false), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object)
            {
                if (object instanceof TdApi.Error) {
                    System.err.println("Failed to kick member: " + ((TdApi.Error) object).message);
                    FileLogger.write("Failed to kick member: " + ((TdApi.Error) object).message);
                    Print.print("");
                }
                else {
                    System.out.println("Member kicked successfully");
                    Print.print("");
                }
            }
        });
    }
}
