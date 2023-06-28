package Services;

import Utils.Base;
import Utils.ConvertToLong;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

public class GetChat extends Base {
    /**
     Get Chat's Information
     */
    public static void getChat(String[] args){
        client.send(new TdApi.GetChat(ConvertToLong.toLong(args[0])), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                if (object instanceof TdApi.Chat){
                    TdApi.Chat chat = (TdApi.Chat) object;
                    if (chat.type instanceof TdApi.ChatTypeSupergroup){
                        TdApi.ChatTypeSupergroup supergroup = (TdApi.ChatTypeSupergroup) chat.type;
                        client.send(new TdApi.GetSupergroupFullInfo(supergroup.supergroupId), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object instanceof TdApi.SupergroupFullInfo){
                                    TdApi.SupergroupFullInfo supergroupFullInfo = (TdApi.SupergroupFullInfo) object;
                                    System.out.println("\nID: -" + supergroup.supergroupId);
                                    System.out.println("Type: " + chat.type.getClass().getSimpleName().substring(8));
                                    System.out.println("Title: " + chat.title);
                                    System.out.println("Description:\n" + supergroupFullInfo.description);
                                    System.out.println("Members count: " + supergroupFullInfo.memberCount);
                                    System.out.println("Invite link: " + supergroupFullInfo.inviteLink.inviteLink);
                                }
                            }
                        });
                    } else if (chat.type instanceof TdApi.ChatTypeBasicGroup){
                        TdApi.ChatTypeBasicGroup basicGroup = (TdApi.ChatTypeBasicGroup) chat.type;
                        client.send(new TdApi.GetBasicGroupFullInfo(basicGroup.basicGroupId), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object instanceof TdApi.BasicGroupFullInfo){
                                    TdApi.BasicGroupFullInfo basicGroupFullInfo = (TdApi.BasicGroupFullInfo) object;
                                    System.out.println("\nID: -" + basicGroup.basicGroupId);
                                    System.out.println("Type: " + chat.type.getClass().getSimpleName().substring(8));
                                    System.out.println("Title: " + chat.title);
                                    System.out.println("Description: \n" + basicGroupFullInfo.description);
                                    System.out.println("Members count: " + basicGroupFullInfo.members.length);
                                    System.out.println("Invite link: " + basicGroupFullInfo.inviteLink.inviteLink);
                                }
                            }
                        }, null);
                    }
                }
            }
        }, null);
    }
}
