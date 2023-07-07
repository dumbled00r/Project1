package Services;

import Utils.Base;
import com.google.gson.JsonObject;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GetMember extends Base {
    private static int numOfMembers;

    private static List<JsonObject> lstObjResults = new ArrayList<>();

    /**
     * Get the members' userid of a group chat
     */
    public static CompletableFuture<List<JsonObject>> getMember(Long chatId) {
        chatMemberIds.clear();
        lstObjResults.clear();
        CompletableFuture<List<JsonObject>> future = new CompletableFuture<>();
        client.send(new TdApi.GetChat(chatId), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                if (object instanceof TdApi.Chat chat) {
                    if (chat.type instanceof TdApi.ChatTypeSupergroup) {
                        if (((TdApi.ChatTypeSupergroup) chat.type).isChannel) {
                            System.out.println("\nThis chat group is a channel, please provide a chat group");
                            future.complete(lstObjResults);
                            return;
                        }
                        long supergroupId = ((TdApi.ChatTypeSupergroup) chat.type).supergroupId;
                        client.send(new TdApi.GetSupergroupFullInfo(supergroupId), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object instanceof TdApi.SupergroupFullInfo supergroupFullInfo) {
                                    if (supergroupFullInfo.canGetMembers) {
                                        numOfMembers = supergroupFullInfo.memberCount;
                                        getSupergroupMembers(chatId, supergroupId)
                                                .thenAccept(result -> future.complete(lstObjResults));
                                    } else {
                                        System.out.println("Group does not allow us to get members");
                                        future.complete(lstObjResults);
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
                                    GetUser.getMassUser(chatMemberIds, chatId).thenAccept(result -> {
                                        lstObjResults.addAll(result);
                                        future.complete(lstObjResults);
                                    });
                                }
                            }
                        });
                    } else {
                        System.out.println("Not a group chat");
                        future.complete(lstObjResults);
                    }
                } else {
                    System.out.println("Handle error");
                    future.complete(lstObjResults);
                }
            }
        }, null);
        return future;
    }

    private static CompletableFuture<Void> getSupergroupMembers(long chatId, long supergroupId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
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
                        GetUser.getMassUser(memberIds, chatId).thenAccept(result -> {
                            lstObjResults.addAll(result);
                            future.complete(null);
                        });
                    }
                }
            });
            offset += Math.min(200, numOfMembers - offset);
        }
        return future;
    }
}