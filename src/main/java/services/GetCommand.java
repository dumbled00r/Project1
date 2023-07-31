package services;

import airtableutils.SyncToAirTable;
import models.GroupChat;
import models.User;
import org.drinkless.tdlib.Client;
import utils.*;
import org.drinkless.tdlib.TdApi;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
public class GetCommand extends Base {
    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("", new EmptyCommand());
        commands.put("help", new HelpCommand());
        commands.put("getallchat", new GetChatCommand());
        commands.put("getme", new GetMeCommand());
        commands.put("sendmsg", new SendMessageCommand());
        commands.put("addmember", new AddMemberCommand());
        commands.put("getmember", new GetMemberCommand());
        commands.put("kick", new KickMemberCommand());
        commands.put("getmessage", new GetMessage());
        commands.put("createbasic", new CreateBasicGroup());
        commands.put("createsuper", new CreateSuperGroup());
        commands.put("sync", new SyncToAirTableCommand());
        commands.put("logout", new LogoutCommand());
        commands.put("quit", new QuitCommand());
    }

    public static CompletableFuture<Void> getCommand() {
        return PromptString.promptStringAsync(commandsLine).thenApply(command -> {
            String[] commandParts = command.split(" ", 2);
            Command cmd = commands.get(commandParts[0].toLowerCase());
            if (cmd != null) {
                try {
                    cmd.execute(commandParts.length > 1 ? commandParts[1] : "");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    System.err.println("Error executing command: " + e.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                System.err.println("Unsupported command: " + command);
                Print.print("");
            }
            return null;
        });
    }

    private abstract static class Command {
        public abstract void execute(String args) throws InterruptedException, ExecutionException, IOException;
    }

    private static class EmptyCommand extends Command {
        @Override
        public void execute(String args) {
            // do nothing
        }
    }

    private static class HelpCommand extends Command {
        @Override
        public void execute(String args) {
            System.out.println("\033[0;96m  Command     |     Arguments      |             Description              \033[0m");
            System.out.println("\033[0;96mgetallchat    |                    |  Get All Administrated Chat Information\033[m");
            System.out.println("\033[0;96mgetme         |                    |  Get My Information\033[0m");
            System.out.println("\033[0;96msendmsg       | <ChatId> <Message> |  Send Message To An Existing Chat\033[0m");
            System.out.println("\033[0;96maddmember     | <ChatId> <UserId>  |  Add User To An Existing Chat\033[0m");
            System.out.println("\033[0;96mgetmember     | <ChatId>           |  Get Members Of A Chat Group\033[0m");
            System.out.println("\033[0;96mkick          | <ChatId> <UserId>  |  Kick User Out Of An Existing Chat\033[0m");
            System.out.println("\033[0;96mgetmessage    | <ChatId>           |  Get Messages History Of An Existing Chat\033[0m");
            System.out.println("\033[0;96mcreatebasic   | <GroupTitle>       |  Create Basic Group Chat\033[0m");
            System.out.println("\033[0;96mcreatesuper   | <GroupTitle>       |  Create Super Group Chat\033[0m");
            System.out.println("\033[0;96msync          |                    |  Sync To AirTable\033[0m");
            System.out.println("\033[0;96mlogout        |                    |  Logout\033[0m");
            System.out.println("\033[0;96mhelp          |                    |  List Of Commands\033[0m");
            System.out.println("\033[0;96mquit          |                    |  Quit\033[0m");
        }
    }

    private static class GetChatCommand extends Command {
        @Override
        public void execute(String args) throws InterruptedException, ExecutionException {
            String[] getArgs = args.split(" ", 1);
            if (!getArgs[0].equals("")) System.out.println("\nMaybe you mean getting all chats:");
            CompletableFuture<Void> chatIdsFuture = GetMainChatList.loadChatIdsAsync();
            chatIdsFuture.thenComposeAsync((Void v) -> {
                CompletableFuture<List<GroupChat>> massChatFuture = null;
                try {
                    massChatFuture = GetChat.getMassChat();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                return massChatFuture.thenAcceptAsync(results -> {
                    try {
                        GetChat.printChatInfo();
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                });
            }).join();
        }
    }

    private static class GetMeCommand extends Command {
        @Override
        public void execute(String args) throws InterruptedException, ExecutionException {
            String[] getArgs = args.split(" ", 1);
            if (!getArgs[0].equals("")) System.out.println("\nMaybe you mean getting your user information:");
            GetMe.printMyInfo();
        }
    }

    private static class SendMessageCommand extends Command {
        public void execute(String args) throws InterruptedException, ExecutionException {
            String[] sendArgs = args.split(" ", 2);
            if (sendArgs.length == 1) {
                System.err.println("Message cannot be empty");
                Print.print("");
                return;
            }
            CompletableFuture<Void> future = SendMessage.sendMessage(ConvertToLong.toLong(sendArgs[0]), sendArgs[1]);
            future.thenRun(() -> {
            });
            future.get();
        }
    }

    private static class AddMemberCommand extends Command {
        @Override
        public void execute(String args) throws InterruptedException, ExecutionException {
            String[] addArgs = args.split(" ", 2);
            Long longChatId = ConvertToLong.toLong(addArgs[0]);
            Long longUserId = ConvertToLong.toLong(addArgs[1]);
            CompletableFuture<Void> future = AddMember.addMember(longChatId, longUserId);;
            future.thenRun(() -> {
            });
            future.get();
        }
    }

    private static class KickMemberCommand extends Command {
        @Override
        public void execute(String args) throws InterruptedException, ExecutionException, IOException {
            String[] kickArgs = args.split(" ", 2);
            Long longChatId = ConvertToLong.toLong(kickArgs[0]);
            Long longUserId = ConvertToLong.toLong(kickArgs[1]);
            CompletableFuture<Void> future = KickMember.kickMember(longChatId, longUserId);
            future.thenRun(() -> {
            });
            future.get();
        }
    }
    private static class GetMemberCommand extends Command {
        @Override
        public void execute(String args) throws InterruptedException, ExecutionException {
            long chatId = ConvertToLong.toLong(args);
            List<User> res = GetMember.getMember(chatId).get();
            GetUser.printUserInfo(res);
        }
    }

    private static class GetMessage extends Command {
        @Override
        public void execute(String args) throws InterruptedException, ExecutionException, IOException {
            String[] getmsgArgs = args.split(" ", 1);
            Long chatId = ConvertToLong.toLong(getmsgArgs[0]);
            GetMessagesHistory.printMessages(chatId);
        }
    }

    private static class CreateBasicGroup extends Command {
        @Override
        public void execute(String args) throws InterruptedException, ExecutionException, IOException {
            String[] getGroupTitle = args.split(" ", 1);
            CompletableFuture<GroupChat> future = CreateGroup.createBasicGroup(getGroupTitle[0]);
            CompletableFuture<Void> printFuture = future.thenAccept((GroupChat groupChat) -> {
            }).thenRun(() -> {
            });
            printFuture.get();
        }
    }
    private static class CreateSuperGroup extends Command {
        @Override
        public void execute(String args) throws InterruptedException, ExecutionException, IOException {
            String[] getGroupTitle = args.split(" ", 1);
            CompletableFuture<GroupChat> future = CreateGroup.createSuperGroup(getGroupTitle[0]);
            CompletableFuture<Void> printFuture = future.thenAccept((GroupChat groupChat) -> {
            }).thenRun(() -> {
            });
            printFuture.get();
        }
    }


    private static class SyncToAirTableCommand extends Command {
        @Override
        public void execute(String args) throws InterruptedException, ExecutionException {
            String[] getArgs = args.split(" ", 1);
            if (!getArgs[0].equals("")) System.out.println("\nMaybe you mean synchronize your data to AirTable:");
            try {
                SyncToAirTable.syncToAirTable();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                FileLogger.write("Error executing SyncToAirTableCommand: " + e.getMessage());
            }
        }
    }

    private static class LogoutCommand extends Command {
        @Override
        public void execute(String args) throws InterruptedException, ExecutionException {
            String[] getArgs = args.split(" ", 1);
            if (!getArgs[0].equals("")) System.out.println("\nMaybe you mean logging out:");
            CompletableFuture<Void> logoutFuture = new CompletableFuture<>();
            client.send(new TdApi.LogOut(), new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.Object object) throws IOException, InterruptedException, ExecutionException {
                    logoutFuture.complete(null);
                }
            });
            logoutFuture.thenAccept((Void v) -> System.out.println("Logged out, please wait for new session to be created"));
        }
    }

    private static class QuitCommand extends Command {
        @Override
        public void execute(String args) throws InterruptedException, ExecutionException {
            String[] getArgs = args.split(" ", 1);
            if (!getArgs[0].equals("")) System.out.println("\nMaybe you mean quitting the application:");
            System.out.println("Goodbye!");
            System.exit(0);
        }
    }
}