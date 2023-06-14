package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
public class GetMember extends Base {
    private static int limit = 200;
    private static int offset = 0;
    private static int numOfMembers;
    /**
    Get the members' userid of a group chat
     */
    public static void getMember(String[] args)
    {
        List<Long> chatMemberIds = new ArrayList<>();
        client.send(new TdApi.GetChat(ConvertToLong.toLong(args[0])), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                Object lock = new Object();
                if (object instanceof TdApi.Chat) {
                    TdApi.Chat chat = (TdApi.Chat) object;

                    if (chat.type instanceof TdApi.ChatTypeSupergroup) {
                        if (((TdApi.ChatTypeSupergroup) chat.type).isChannel) {
                            System.out.println("\nThis chat group is a channel, cannot get members");
                        }
                        long supergroupId = ((TdApi.ChatTypeSupergroup) chat.type).supergroupId;
                        client.send(new TdApi.GetSupergroupFullInfo(supergroupId), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object instanceof TdApi.SupergroupFullInfo){
                                    TdApi.SupergroupFullInfo supergroupFullInfo = (TdApi.SupergroupFullInfo) object;
                                    numOfMembers = supergroupFullInfo.memberCount;
                                    getSupergroupMembersRecursive(0, chatMemberIds, numOfMembers, supergroupId);
                                }
                            }
                        });
                    } else if (chat.type instanceof TdApi.ChatTypeBasicGroup){
                        // Upgrade to Super group to get member
                        TdApi.ChatTypeBasicGroup basicGroup = (TdApi.ChatTypeBasicGroup) chat.type;
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
                                    GetUser.getMassUser(chatMemberIds);
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
                                GetUser.getUser(Long.toString(userID));
                            }
                        }
                    }
                }
                if (offset < numOfMembers) {
                    int nextOffset = offset + Math.min(200, numOfMembers - offset);
                    getSupergroupMembersRecursive(nextOffset, chatMemberIds, numOfMembers, supergroupId);
                }
            }
        });
    }
}

