package Services;

import Utils.Base;
import Utils.ConvertToLong;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.ArrayList;
import java.util.List;

import static Services.GetChat.chatJson;

public class GetMember extends Base {
    private static int numOfMembers;

    private static String chatTitle;

    /**
     * Get the members' userid of a group chat
     */
    public static void getMember(String[] args) {
        chatMemberIds.clear();
        long chatId = ConvertToLong.toLong(args[0]);
        client.send(new TdApi.GetChat(chatId), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                if (object instanceof TdApi.Chat chat) {
                    chatTitle = chat.title;
                    if (chat.type instanceof TdApi.ChatTypeSupergroup) {
                        if (((TdApi.ChatTypeSupergroup) chat.type).isChannel) {
                            System.out.println("\nThis chat group is a channel, please provide a chat group");
                            return;
                        }
                        long supergroupId = ((TdApi.ChatTypeSupergroup) chat.type).supergroupId;
                        client.send(new TdApi.GetSupergroupFullInfo(supergroupId), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object instanceof TdApi.SupergroupFullInfo supergroupFullInfo) {
                                    if (supergroupFullInfo.canGetMembers) {
                                        numOfMembers = supergroupFullInfo.memberCount;
                                        getSupergroupMembers(chatId, supergroupId);
                                    } else {
                                        System.out.println("Group does not allow us to get members");
                                    }
                                }
                            }
                        });
                    } else if (chat.type instanceof TdApi.ChatTypeBasicGroup basicGroup) {
                        client.send(new TdApi.GetBasicGroupFullInfo(basicGroup.basicGroupId), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object instanceof TdApi.BasicGroupFullInfo) {

                                    TdApi.ChatMember[] chatMembers = ((TdApi.BasicGroupFullInfo) object).members;
                                    for (TdApi.ChatMember member : chatMembers) {
                                        if (member.memberId instanceof TdApi.MessageSenderUser) {
                                            chatMemberIds.add(((TdApi.MessageSenderUser) member.memberId).userId);
                                        }
                                    }
                                    GetUser.getMassUser(chatMemberIds, chatTitle);
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

    private static void getSupergroupMembers(long chatId, long supergroupId) {
        int offset = 0;
        int limit = 200;
        List<Long> memberIds = new ArrayList<>();
        while (offset < numOfMembers) {
            client.send(new TdApi.GetSupergroupMembers(supergroupId, null, offset, limit), new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.Object object) {
                    if (object instanceof TdApi.ChatMembers) {
                        TdApi.ChatMember[] chatMembers = ((TdApi.ChatMembers) object).members;
                        for (TdApi.ChatMember member : chatMembers) {
                            if (member.memberId instanceof TdApi.MessageSenderUser) {
                                Long userId = ((TdApi.MessageSenderUser) member.memberId).userId;
                                if (!memberIds.contains(userId)) {
                                    memberIds.add(userId);
                                }
                            }
                        }
                    }
                    // if we have received all members, call the getMassUser method
                    if (memberIds.size() == numOfMembers) {
                        GetUser.getMassUser(memberIds, chatTitle);
                    }
                }
            });
            offset += Math.min(200, numOfMembers - offset);
        }
    }
}