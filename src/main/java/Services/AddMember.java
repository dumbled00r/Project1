package Services;

import Utils.Base;
import Utils.FileLogger;
import Utils.Print;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class AddMember extends Base {
    /**
     * Add a single (in contact) user to a group chat
     * @param chatId
     * @param userId
     *
     */
    public static void addMember(Long chatId, Long userId){
        client.send(new TdApi.AddChatMember(chatId, userId, 0), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) throws IOException, InterruptedException, ExecutionException {
                if (object instanceof TdApi.Error) {
                    System.err.println("\nFailed to add member: " + ((TdApi.Error) object).message);
                    FileLogger.write("\nFailed to add member: " + ((TdApi.Error) object).message);
                    Print.print("");
                }
                else {
                    System.out.println("Added successfully");
                    Print.print("");
                }
            }
        });
    }
}
