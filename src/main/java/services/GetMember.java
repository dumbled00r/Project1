package services;

import utils.Base;
import models.User;
import utils.FileLogger;
import utils.Print;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GetMember extends Base {
    private static int numOfMembers;

    private static List<User> lstUsers = new ArrayList<>();

    /**
     * Get the members' userid of a group chat
     */
    public static CompletableFuture<List<User>> getMember(Long chatId) {
        chatMemberIds.clear();
        lstUsers.clear();
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        client.send(new TdApi.GetChat(chatId), object -> {
            if (object instanceof TdApi.Chat chat) {
                if (chat.type instanceof TdApi.ChatTypeSupergroup) {
                    if (((TdApi.ChatTypeSupergroup) chat.type).isChannel) {
                        FileLogger.write("This chat group is a channel, please provide a chat group");
                        future.complete(lstUsers);
                        return;
                    }
                    long supergroupId = ((TdApi.ChatTypeSupergroup) chat.type).supergroupId;
                    client.send(new TdApi.GetSupergroupFullInfo(supergroupId), object12 -> {
                        if (object12 instanceof TdApi.SupergroupFullInfo supergroupFullInfo) {
                            if (supergroupFullInfo.canGetMembers) {
                                numOfMembers = supergroupFullInfo.memberCount;
                                getSupergroupMembers(chatId, supergroupId)
                                        .thenAccept(result -> future.complete(lstUsers));
                            } else {
                                FileLogger.write("Group " + chatId + " does not allow us to get members");
                                future.complete(lstUsers);
                            }
                        }
                    });
                } else if (chat.type instanceof TdApi.ChatTypeBasicGroup basicGroup) {
                    client.send(new TdApi.GetBasicGroupFullInfo(basicGroup.basicGroupId), object1 -> {
                        if (object1 instanceof TdApi.BasicGroupFullInfo) {
                            TdApi.ChatMember[] chatMembers = ((TdApi.BasicGroupFullInfo) object1).members;
                            for (TdApi.ChatMember member : chatMembers) {
                                if (member.memberId instanceof TdApi.MessageSenderUser) {
                                    chatMemberIds.add(((TdApi.MessageSenderUser) member.memberId).userId);
                                }
                            }
                            GetUser.getMassUser(chatMemberIds, chatId).thenAccept(result -> {
                                lstUsers.addAll(result);
                                future.complete(lstUsers);
                            });
                        }
                    });
                } else if (chat.type instanceof TdApi.ChatTypePrivate) {
                    FileLogger.write(chat.id + " is not a chat group");
                    Print.print("");
                    future.complete(lstUsers);
                }
            } else {
                FileLogger.write("Invalid Chat ID: " + chatId + ((TdApi.Error) object).message);
                Print.print("");
                future.complete(lstUsers);
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
            client.send(new TdApi.GetSupergroupMembers(supergroupId, null, offset, limit), object -> {
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
                } else {
                    FileLogger.write("Failed to get member: " + ((TdApi.Error) object).message);
                }
                // if we have received all members, call the getMassUser method
                if (memberIds.size() == numOfMembers) {
                    GetUser.getMassUser(memberIds, chatId).thenAccept(result -> {
                        lstUsers.addAll(result);
                        future.complete(null);
                    });
                }
            });
            offset += Math.min(200, numOfMembers - offset);
        }
        return future;
    }
}