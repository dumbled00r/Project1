package Services;

import Models.GroupChat;
import Utils.Base;
import Utils.FileLogger;
import Utils.Print;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletableFuture;

public class CreateGroup extends Base {
    public static CompletableFuture<GroupChat> createBasicGroup(String groupTitle) {
        CompletableFuture<GroupChat> future = new CompletableFuture<>();
        client.send(new TdApi.CreateNewBasicGroupChat(null, groupTitle, 0), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) throws IOException, InterruptedException, ExecutionException {
                if (object instanceof TdApi.Error) {
                    System.err.println("Encounter error: " + ((TdApi.Error) object).message);
                    FileLogger.write("Encounter error: " + ((TdApi.Error) object).message);
                    Print.print("");
                    future.completeExceptionally(new RuntimeException("Group creation failed: " + ((TdApi.Error) object).message));
                }
                else {
                    TdApi.Chat chat = (TdApi.Chat) object;
                    GroupChat groupChat = new GroupChat(chat.id, "BasicGroup", chat.title, 0, "", "");
                    System.out.println("Basic Group has successfully be created");
                    Print.print("");
                    future.complete(groupChat);
                }
            }
        });
        return future;
    }
    public static CompletableFuture<GroupChat> createSuperGroup(String groupTitle) {
        CompletableFuture<GroupChat> future = new CompletableFuture<>();
        client.send(new TdApi.CreateNewSupergroupChat(groupTitle, false, false, "", null, 0, false), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) throws IOException, InterruptedException, ExecutionException {
                if (object instanceof TdApi.Error) {
                    System.err.println("Encounter error: " + ((TdApi.Error) object).message);
                    FileLogger.write("Encounter error: " + ((TdApi.Error) object).message);
                    Print.print("");
                    future.completeExceptionally(new RuntimeException("Group creation failed: " + ((TdApi.Error) object).message));
                }
                else {
                    TdApi.Chat chat = (TdApi.Chat) object;
                    GroupChat groupChat = new GroupChat(chat.id, "SuperGroup", chat.title, 0, "", "");
                    System.out.println("Super Group has successfully be created");
                    Print.print("");
                    future.complete(groupChat);
                }
            }
        });
        return future;
    }
}
