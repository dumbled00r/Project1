package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.ArrayList;
import java.util.List;

public class GetMember extends Base {
    public static void getMember(String[] args)
    {
        client.send(new TdApi.GetChat(ConvertToLong.toLong(args[0])), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                if (object instanceof TdApi.Chat) {
                    TdApi.Chat chat = (TdApi.Chat) object;
                    if (((TdApi.ChatTypeSupergroup) chat.type).isChannel) {
                        System.out.println("This chat group is a channel, cannot get members");
                    }
                    if (chat.type instanceof TdApi.ChatTypeSupergroup) {
                        long supergroupId = ((TdApi.ChatTypeSupergroup) chat.type).supergroupId;
                        client.send(new TdApi.GetSupergroupMembers(supergroupId, null, 0, 200), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object instanceof TdApi.ChatMembers){
                                    TdApi.ChatMember[] chatMembers = ((TdApi.ChatMembers) object ).members;
                                    List<Long> chatMemberIds = new ArrayList<>();
                                    for (TdApi.ChatMember member : chatMembers){
                                        if (member.memberId instanceof TdApi.MessageSenderUser){
                                            chatMemberIds.add(((TdApi.MessageSenderUser) member.memberId).userId);
                                        }
                                    }
                                    System.out.println(chatMemberIds);
                                }
                            }
                        });
                    } else if (chat.type instanceof TdApi.ChatTypeBasicGroup){
                        // Upgrade to Super group to get member
                        client.send(new TdApi.UpgradeBasicGroupChatToSupergroupChat(chat.id), defaultHandler);
                        long supergroupId = ((TdApi.ChatTypeSupergroup) chat.type).supergroupId;
                        client.send(new TdApi.GetSupergroupMembers(supergroupId, null, 0, 200), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object instanceof TdApi.ChatMembers) {
                                    TdApi.ChatMember[] chatMembers = ((TdApi.ChatMembers) object).members;
                                    List<Long> chatMemberIds = new ArrayList<>();
                                    for (TdApi.ChatMember member : chatMembers) {
                                        if (member.memberId instanceof TdApi.MessageSenderUser) {
                                            chatMemberIds.add(((TdApi.MessageSenderUser) member.memberId).userId);
                                        }
                                    }
                                    System.out.println(chatMemberIds);
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
}
