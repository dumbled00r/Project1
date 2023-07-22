package Services;

import Utils.Base;
import Utils.FileLogger;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CreateGroup extends Base {
    public static void createBasicGroup(String groupTitle) {
        client.send(new TdApi.CreateNewBasicGroupChat(null, groupTitle, 0), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) throws IOException, InterruptedException, ExecutionException {
                if (object instanceof TdApi.Error) {
                    System.err.println(((TdApi.Error) object).message);
                    FileLogger.write(((TdApi.Error) object).message);
                }
                else {
                    System.out.println("Basic Group has successfully be created");
                }
            }
        });
    }
    public static void createSuperGroup(String groupTitle) {
        client.send(new TdApi.CreateNewSupergroupChat(groupTitle, false, false, "", null, 0, false), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) throws IOException, InterruptedException, ExecutionException {
                if (object instanceof TdApi.Error) {
                    System.err.println(((TdApi.Error) object).message);
                    FileLogger.write(((TdApi.Error) object).message);
                }
                else {
                    System.out.println("Super Group has successfully be created");
                }
            }
        });
    }
}
