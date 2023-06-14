package services;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

public class GetMember extends Base {
    public static void getMember(String[] args)
    {
        client.send(new TdApi.GetChat(ConvertToLong.toLong(args[0])), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                if (object instanceof TdApi.Chat) {
                    TdApi.Chat chat = (TdApi.Chat) object;
                    if (chat.type instanceof TdApi.ChatTypeSupergroup) {
                        long supergroupId = ((TdApi.ChatTypeSupergroup) chat.type).supergroupId;
                        client.send(new TdApi.GetSupergroupMembers(supergroupId, null, 0, 10), defaultHandler);
                    } else if (chat.type instanceof TdApi.ChatTypeBasicGroup){
                        // Upgrade to Super group to get member
                        client.send(new TdApi.UpgradeBasicGroupChatToSupergroupChat(chat.id), defaultHandler);
                        long supergroupId = ((TdApi.ChatTypeSupergroup) chat.type).supergroupId;
                        client.send(new TdApi.GetSupergroupMembers(supergroupId, null, 0, 10), defaultHandler);
                    } else {
                        System.out.println("Unexpected error");
                    }
                } else {
                    System.out.println("Handle error");
                }
            }
        }, null);
    }
}
