package Services;

import Models.GroupChat;
import Utils.Base;
import Utils.Print;
import com.google.gson.JsonObject;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static Services.GetMainChatList.chatIds;

public class GetChat extends Base {
    /**
     * Get Information for Multiple Chats
     */

    public static CompletableFuture<List<GroupChat>> getMassChat() throws ExecutionException, InterruptedException {
        List<CompletableFuture<GroupChat>> futures = new ArrayList<>();
        for (Long chatId : chatIds){
            futures.add(getChat(chatId)
                    .thenApplyAsync(data -> {
                        if (data != null){
                            return data;
                        } else {
                            return null;
                        }
                    })
                    .exceptionally(throwable -> {
                        System.err.println("Failed to get chat: " + throwable.getMessage());
                        return null;
                    }));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .thenApply(v -> {
                    List<GroupChat> results = new ArrayList<>();
                    for (CompletableFuture<GroupChat> future : futures) {
                        GroupChat data = future.join();
                        if (data != null) {
                            results.add(data);
                        }
                    }
                    if (!results.isEmpty()) {
                        System.out.println("\n+-----------------+-----------------+--------------------------------+--------------------------------+--------------------------------+-----------------+");
                        System.out.println("|        ID       |      Type       |             Title              |           Description          |          Invite Link           |    Members      |");
                        System.out.println("+-----------------+-----------------+--------------------------------+--------------------------------+--------------------------------+-----------------+");
                        for (GroupChat result : results) {
                            long id = result.getId();
                            String type = result.getType();
                            String title = result.getTitle();
                            String description = result.getDescription();
                            if (description.length() > 27) {
                                description = description.substring(0, 24) + "...";
                            }
                            String inviteLink = result.getInviteLink();
                            int memberCount = result.getMembersCount();

                            // Restretch the table to fit the result
                            System.out.printf("| %-15d | %-15s | %-30s | %-30s | %-30s | %-15d |\n", id, type, title, description, inviteLink, memberCount);
                            if (title.length() > 30 || description.length() > 30 || inviteLink.length() > 30) {
                                System.out.println("+-----------------+-----------------+--------------------------------+--------------------------------+--------------------------------+-----------------+");
                            }
                        }
                        System.out.println("+-----------------+-----------------+--------------------------------+--------------------------------+--------------------------------+-----------------+");
                    } else {
                        System.out.println("No chat information available");
                    }
                    System.out.println("You can use the command getmem + <ChatID> to get members of a group \nOr use help for more commands");
                    Print.print("");
                    return results;
                });
    }

    /**
     * Get Chat's Information
     */

    public static CompletableFuture<GroupChat> getChat(Long chatId) throws ExecutionException, InterruptedException {
        CompletableFuture<GroupChat> future = new CompletableFuture<>();
        Long myId = GetMe.getMe().get().get("Id").getAsLong();
        client.send(new TdApi.GetChat(chatId), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) throws ExecutionException, InterruptedException {
                if (object.getConstructor() == TdApi.Chat.CONSTRUCTOR){
                    TdApi.Chat chat = (TdApi.Chat) object;
                    if (chat.type instanceof TdApi.ChatTypeSupergroup){
                        client.send(new TdApi.GetChatAdministrators(chat.id), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) throws ExecutionException, InterruptedException {
                                if (object instanceof TdApi.ChatAdministrators) {
                                    TdApi.ChatAdministrator[] chatAdmins = ((TdApi.ChatAdministrators) object).administrators;
                                    boolean isAdmin = false;
                                    for (TdApi.ChatAdministrator admin : chatAdmins) {
                                        if (myId == admin.userId) {
                                            isAdmin = true;
                                            break;
                                        }
                                    }
                                    if (!isAdmin) {
                                        System.out.println("You are not an administrator in this chat.");
                                        future.complete(null);
                                        return;
                                    }
                                }
                            }
                        });
                        client.send(new TdApi.GetSupergroupFullInfo(((TdApi.ChatTypeSupergroup) chat.type).supergroupId), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object.getConstructor() == TdApi.SupergroupFullInfo.CONSTRUCTOR){
                                    TdApi.SupergroupFullInfo supergroupFullInfo = (TdApi.SupergroupFullInfo) object;
                                    String description = supergroupFullInfo.description;
                                    String inviteLink = (supergroupFullInfo.inviteLink == null) ? "" : supergroupFullInfo.inviteLink.inviteLink;
                                    GroupChat groupChat = new GroupChat(chat.id, chat.type.getClass().getSimpleName().substring(8), chat.title, supergroupFullInfo.memberCount, description, inviteLink);
                                    future.complete(groupChat);
                                } else {
                                    String errorMessage = "Failed to get supergroup full info: " + object;
                                    System.err.println(errorMessage);
                                    future.completeExceptionally(new RuntimeException(errorMessage));
                                }
                            }
                        });
                    } else if (chat.type instanceof TdApi.ChatTypeBasicGroup){
                        client.send(new TdApi.GetChatAdministrators(chat.id), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) throws ExecutionException, InterruptedException {
                                if (object instanceof TdApi.ChatAdministrators) {
                                    TdApi.ChatAdministrator[] chatAdmins = ((TdApi.ChatAdministrators) object).administrators;
                                    boolean isAdmin = false;
                                    for (TdApi.ChatAdministrator admin : chatAdmins) {
                                        if (myId == admin.userId) {
                                            isAdmin = true;
                                            break;
                                        }
                                    }
                                    if (!isAdmin) {
                                        System.out.println("You are not an administrator in this chat.");
                                        future.complete(null);
                                        return;
                                    }
                                }
                            }
                        });
                        client.send(new TdApi.GetBasicGroupFullInfo(((TdApi.ChatTypeBasicGroup) chat.type).basicGroupId), new Client.ResultHandler() {
                            @Override
                            public void onResult(TdApi.Object object) {
                                if (object.getConstructor() == TdApi.BasicGroupFullInfo.CONSTRUCTOR){
                                    TdApi.BasicGroupFullInfo basicGroupFullInfo = (TdApi.BasicGroupFullInfo) object;
                                    String description = basicGroupFullInfo.description;
                                    String inviteLink = basicGroupFullInfo.inviteLink.inviteLink;
                                    GroupChat groupChat = new GroupChat(chat.id, chat.type.getClass().getSimpleName().substring(8), chat.title, basicGroupFullInfo.members.length, description, inviteLink);
                                    future.complete(groupChat);
                                } else {
                                    String errorMessage = "Failed to get basic group full info: " + object;
                                    System.err.println(errorMessage);
                                    future.completeExceptionally(new RuntimeException(errorMessage));
                                }
                            }
                        });

                    } else {
                        future.complete(null);
                    }
                } else {
                    String errorMessage = "Failed to get chat: " + object;
                    System.err.println(errorMessage);
                    future.completeExceptionally(new RuntimeException(errorMessage));
                }
            }
        }, null);
        return future;
    }
}