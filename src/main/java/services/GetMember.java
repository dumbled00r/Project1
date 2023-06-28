package Services;

import Utils.Base;
import Utils.ConvertToLong;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.List;
public class GetMember extends Base {
    private static int numOfMembers;
    /**
     Get the members' userid of a group chat
     */
    public static void getMember(String[] args)
    {
        chatMemberIds.clear();
        client.send(new TdApi.GetChat(ConvertToLong.toLong(args[0])), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                Object lock = new Object();
                if (object instanceof TdApi.Chat chat) {
                    if (chat.type instanceof TdApi.ChatTypeSupergroup) {
                        if (((TdApi.ChatTypeSupergroup) chat.type).isChannel) {
                            System.out.println("\nThis chat group is a channel, please provide a chat group");
                        }
                        long supergroupId = ((TdApi.ChatTypeSupergroup) chat.type).supergroupId;
                        client.send(new TdApi.GetSupergroupFullInfo(supergroupId), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object instanceof TdApi.SupergroupFullInfo supergroupFullInfo){
                                    numOfMembers = supergroupFullInfo.memberCount;
                                    getSupergroupMembersRecursive(0, chatMemberIds, numOfMembers, supergroupId);
                                }
                            }
                        });
                    } else if (chat.type instanceof TdApi.ChatTypeBasicGroup basicGroup){
                        // Upgrade to Super group to get member
                        client.send(new TdApi.GetBasicGroupFullInfo(basicGroup.basicGroupId), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object instanceof TdApi.BasicGroupFullInfo){

                                    TdApi.ChatMember[] chatMembers = ((TdApi.BasicGroupFullInfo) object ).members;
                                    for (TdApi.ChatMember member : chatMembers) {
                                        if (member.memberId instanceof TdApi.MessageSenderUser) {
                                            chatMemberIds.add(((TdApi.MessageSenderUser) member.memberId).userId);
                                        }
                                    }
                                    GetUser.getMassUser(chatMemberIds, basicGroup.basicGroupId);
                                }
                            }
                        });
                    } else {
                        System.out.println("Not a group chat");
                    }
                } else {
                    System.out.println("Handle error");
                }
            }
        }, null);
    }
    private static void getSupergroupMembersRecursive(int offset, List<Long> chatMemberIds, int numOfMembers, long supergroupId) {
        client.send(new TdApi.GetSupergroupMembers(supergroupId, null, offset, 200), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                if (object instanceof TdApi.ChatMembers){
                    TdApi.ChatMember[] chatMembers = ((TdApi.ChatMembers) object ).members;

                    for (TdApi.ChatMember member : chatMembers){
                        if (member.memberId instanceof TdApi.MessageSenderUser){
                            Long userID = ((TdApi.MessageSenderUser) member.memberId).userId;
                            if (!chatMemberIds.contains(userID)){
                                chatMemberIds.add(userID);
                            }
                        }
                    }
                }
                if (offset < numOfMembers) {
                    int nextOffset = offset + Math.min(200, numOfMembers - offset);
                    getSupergroupMembersRecursive(nextOffset, chatMemberIds, numOfMembers, supergroupId);
                }
                else {
                    GetUser.getMassUser(chatMemberIds, supergroupId);
                }
            }
        });
    }
}