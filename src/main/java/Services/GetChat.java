package Services;

import Utils.Base;
import Utils.ConvertToLong;
import com.google.gson.JsonObject;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.Objects;

public class GetChat extends Base {
    public static JsonObject chatJson = new JsonObject();
    /**
     Get Chat's Information
     */
    public static void getChat(String[] args){
        client.send(new TdApi.GetChat(ConvertToLong.toLong(args[0])), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                if (object instanceof TdApi.Chat chat){
                    if (chat.type instanceof TdApi.ChatTypeSupergroup supergroup){
                        client.send(new TdApi.GetSupergroupFullInfo(supergroup.supergroupId), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object instanceof TdApi.SupergroupFullInfo supergroupFullInfo){
                                    chatJson.addProperty("Id", supergroup.supergroupId);
                                    chatJson.addProperty("type", chat.type.getClass().getSimpleName().substring(8));
                                    chatJson.addProperty("title", chat.title);
                                    chatJson.addProperty("description", supergroupFullInfo.description);
                                    chatJson.addProperty("members count", supergroupFullInfo.memberCount);
                                    String inviteLink = (supergroupFullInfo.inviteLink == null) ? "" : supergroupFullInfo.inviteLink.inviteLink;
                                    chatJson.addProperty("invite link", inviteLink);
                                    System.out.println(chatJson);
                                }
                            }
                        });
                    } else if (chat.type instanceof TdApi.ChatTypeBasicGroup basicGroup){
                        client.send(new TdApi.GetBasicGroupFullInfo(basicGroup.basicGroupId), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object instanceof TdApi.BasicGroupFullInfo basicGroupFullInfo){
                                    chatJson.addProperty("Id", basicGroup.basicGroupId);
                                    chatJson.addProperty("type" ,chat.type.getClass().getSimpleName().substring(8));
                                    chatJson.addProperty("title", chat.title);
                                    chatJson.addProperty("description", basicGroupFullInfo.description);
                                    chatJson.addProperty("members count", basicGroupFullInfo.members.length);
                                    chatJson.addProperty("invite link", basicGroupFullInfo.inviteLink.inviteLink);
                                    System.out.println(chatJson);
                                }
                            }
                        }, null);
                    }
                }
            }
        }, null);
    }
}
