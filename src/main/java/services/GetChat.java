package services;

import models.GroupChat;
import utils.Base;
import utils.FileLogger;
import utils.Print;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static services.GetMainChatList.chatIds;
import static utils.TruncateString.truncateStringIfNeeded;

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
                        FileLogger.write("Failed to get chat: " + chatId + " " + throwable.getMessage());
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
                    return results;
                });
    }

    /**
     * Get Chat's Information
     */

    public static CompletableFuture<GroupChat> getChat(Long chatId) throws ExecutionException, InterruptedException {
        CompletableFuture<GroupChat> future = new CompletableFuture<>();
        Long myId = GetMe.getMe().get().get("Id").getAsLong();

        // Send the GetChat request
        client.send(new TdApi.GetChat(chatId), new Client.ResultHandler() {
            boolean isAdmin = false;

            @Override
            public void onResult(TdApi.Object object) {
                try {
                    if (object.getConstructor() == TdApi.Chat.CONSTRUCTOR) {
                        TdApi.Chat chat = (TdApi.Chat) object;
                        if (chat.type instanceof TdApi.ChatTypeSupergroup) {
                            // Send the GetChatAdministrators request
                            client.send(new TdApi.GetChatAdministrators(chat.id), object1 -> {
                                if (object1 instanceof TdApi.ChatAdministrators) {
                                    TdApi.ChatAdministrator[] chatAdmins = ((TdApi.ChatAdministrators) object1).administrators;
                                    for (TdApi.ChatAdministrator admin : chatAdmins) {
                                        if (myId == admin.userId) {
                                            isAdmin = true;
                                            break;
                                        }
                                    }
                                    if (!isAdmin) {
                                        FileLogger.write("You are not an administrator of chat: " + chat.id);
                                        future.complete(null);
                                    } else {
                                        // Send the GetSupergroupFullInfo request only if the user is an administrator
                                        client.send(new TdApi.GetSupergroupFullInfo(((TdApi.ChatTypeSupergroup) chat.type).supergroupId), object11 -> {
                                            if (object11.getConstructor() == TdApi.SupergroupFullInfo.CONSTRUCTOR) {
                                                TdApi.SupergroupFullInfo supergroupFullInfo = (TdApi.SupergroupFullInfo) object11;
                                                String description = supergroupFullInfo.description;
                                                String inviteLink = (supergroupFullInfo.inviteLink == null) ? "" : supergroupFullInfo.inviteLink.inviteLink;
                                                GroupChat groupChat = new GroupChat(chat.id, chat.type.getClass().getSimpleName().substring(8), chat.title, supergroupFullInfo.memberCount, description, inviteLink);
                                                future.complete(groupChat);
                                            } else {
                                                String errorMessage = "Failed to get supergroup full info: " + ((TdApi.Error) object11).message;
                                                FileLogger.write(errorMessage);
                                                future.completeExceptionally(new RuntimeException(errorMessage));
                                            }
                                        });
                                    }
                                }
                            });
                        } else if (chat.type instanceof TdApi.ChatTypeBasicGroup) {
                            // Send the GetChatAdministrators request
                            client.send(new TdApi.GetChatAdministrators(chat.id), object12 -> {
                                if (object12 instanceof TdApi.ChatAdministrators) {
                                    TdApi.ChatAdministrator[] chatAdmins = ((TdApi.ChatAdministrators) object12).administrators;
                                    for (TdApi.ChatAdministrator admin : chatAdmins) {
                                        if (myId == admin.userId) {
                                            isAdmin = true;
                                            break;
                                        }
                                    }
                                    if (!isAdmin) {
                                        FileLogger.write("You are not an administrator of chat: " + chat.id);
                                        future.complete(null);
                                    } else {
                                        // Send the GetBasicGroupFullInfo request only if the user is an administrator
                                        client.send(new TdApi.GetBasicGroupFullInfo(((TdApi.ChatTypeBasicGroup) chat.type).basicGroupId), object121 -> {
                                            if (object121.getConstructor() == TdApi.BasicGroupFullInfo.CONSTRUCTOR) {
                                                TdApi.BasicGroupFullInfo basicGroupFullInfo = (TdApi.BasicGroupFullInfo) object121;
                                                String description = basicGroupFullInfo.description;
                                                String inviteLink = basicGroupFullInfo.inviteLink.inviteLink;
                                                GroupChat groupChat = new GroupChat(chat.id, chat.type.getClass().getSimpleName().substring(8), chat.title, basicGroupFullInfo.members.length, description, inviteLink);
                                                future.complete(groupChat);
                                            } else {
                                                String errorMessage = "Failed to get basic group full info: " + ((TdApi.Error) object121).message;
                                                FileLogger.write(errorMessage);
                                                future.completeExceptionally(new RuntimeException(errorMessage));
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            future.complete(null);
                        }
                    } else {
                        String errorMessage = "Failed to get chat: " + chatId + " " + ((TdApi.Error) object).message;
                        FileLogger.write(errorMessage);
                        future.completeExceptionally(new RuntimeException(errorMessage));
                    }
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        });

        return future;
    }


    public static void printChatInfo() throws ExecutionException, InterruptedException {
        List<GroupChat> results = getMassChat().get();
        if (!results.isEmpty()) {
            System.out.println("\033[34m+-----------------+-----------------+--------------------------------+--------------------------------+--------------------------------+-----------------+");
            System.out.println("\033[34m|        ID       |      Type       |             Title              |           Description          |          Invite Link           |    Members      |");
            System.out.println("\033[34m+-----------------+-----------------+--------------------------------+--------------------------------+--------------------------------+-----------------+");
            for (GroupChat result : results) {
                long id = result.getId();
                String type = result.getType();
                String title = truncateStringIfNeeded(result.getTitle());
                String description = truncateStringIfNeeded(result.getDescription());
                String inviteLink = truncateStringIfNeeded(result.getInviteLink());
                int memberCount = result.getMembersCount();

                // Restretch the table to fit the result
                System.out.printf("\033[34m| %-15d | %-15s | %-30s | %-30s | %-30s | %-15d |\n", id, type, title, description, inviteLink, memberCount);
            }
            System.out.println("\033[34m+-----------------+-----------------+--------------------------------+--------------------------------+--------------------------------+-----------------+\033[0m");
        } else {
            System.out.println("No chat information available");
        }
        System.out.println("You can use the command getmember + <ChatID> to get members of a group \nOr use help for more commands");
        Print.print("");
    }
}