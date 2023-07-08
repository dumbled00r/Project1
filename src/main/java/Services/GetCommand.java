package Services;

import AirTableUtils.SyncToAirTable;
import Utils.*;
import org.drinkless.tdlib.TdApi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class GetCommand extends Base {
    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("", new EmptyCommand());
        commands.put("help", new HelpCommand());
        commands.put("gcs", new GetMainChatListCommand());
        commands.put("gc", new GetChatCommand());
        commands.put("me", new GetMeCommand());
        commands.put("sm", new SendMessageCommand());
        commands.put("gu", new GetUserCommand());
        commands.put("add", new AddMemberCommand());
        commands.put("pm", new SendPrivateMessageCommand());
        commands.put("getmem", new GetMemberCommand());
        commands.put("sync", new SyncToAirTableCommand());
        commands.put("lo", new LogoutCommand());
        commands.put("q", new QuitCommand());
    }
    private static final ReentrantLock lock = new ReentrantLock();


    protected static void getCommand() throws InterruptedException {
        String command = PromptString.promptString(commandsLine);
        String[] commandParts = command.split(" ", 2);
        Command cmd = commands.get(commandParts[0].toLowerCase());
        if (cmd != null) {
            cmd.execute(commandParts.length > 1 ? commandParts[1] : "");
        }
        else {
            System.err.println("Unsupported command: " + command);
        }
    }

    private static abstract class Command {
        public abstract void execute(String args) throws InterruptedException;
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
            System.out.println("gcs - Get Chat Lists ");
            System.out.println("gc <ChatId> - Get Chat Information");
            System.out.println("me - Get My Information");
            System.out.println("sm <ChatId> <Message> - Send Message To An Existing Chat");
            System.out.println("add <ChatId> <UserId> - Add User To An Existing Chat");
            System.out.println("pm <UserId> - Send Private Message To User ");
            System.out.println("getmem <ChatId> - Get Members Of A Chat Group");
            System.out.println("upload - Upload latest query to Airtable");
            System.out.println("lo - Logout");
            System.out.println("q - Quit");
        }
    }

    private static class GetMainChatListCommand extends Command {
        @Override
        public void execute(String args) {
            int limit = 50;
            if (!args.isEmpty()) {
                limit = ToInt.toInt(args);
            }
            GetMainChatList.getMainChatList(limit);
        }
    }

    private static class GetChatCommand extends Command {
        @Override
        public void execute(String args) throws InterruptedException {
//            String[] chatArgs = args.split(" ", 2);
//            GetChat.getChat(Long.parseLong(chatArgs[0]));
            GetMainChatList.loadChatIds();
            Thread.sleep(3000);
            GetChat.getMassChat();
        }
    }

    private static class GetMeCommand extends Command {
        @Override
        public void execute(String args) {
            GetMe.printMyInfo();
        }
    }

    private static class SendMessageCommand extends Command {
        @Override
        public void execute(String args) {
            String[] sendArgs = args.split(" ", 2);
            SendMessage.sendMessage(ConvertToLong.toLong(sendArgs[0]), sendArgs[1]);
        }
    }

    private static class GetUserCommand extends Command {
        @Override
        public void execute(String args) {
            GetUser.getUser(Long.parseLong(args), 123L);
        }
    }

    private static class AddMemberCommand extends Command {
        @Override
        public void execute(String args) {
            String[] addArgs = args.split(" ", 2);
            Long longChatId = ConvertToLong.toLong(addArgs[0]);
            AddMember.addSingleUser(longChatId, ConvertToLong.toLong(addArgs[1]));
        }
    }

    private static class SendPrivateMessageCommand extends Command {
        @Override
        public void execute(String args) {
            String[] pmArgs = args.split(" ", 2);
            client.send(new TdApi.CreatePrivateChat(ConvertToLong.toLong(pmArgs[0]), true), null);
            SendMessage.sendMessage(ConvertToLong.toLong(pmArgs[0]), pmArgs[1]);
        }
    }

    private static class GetMemberCommand extends Command {
        @Override
        public void execute(String args) throws InterruptedException {

            String[] getMemArgs = args.split(" ", 2);
            authorizationLock.lock();
            try {
                GetMember.getMember(Long.parseLong(getMemArgs[0]));
                while (!haveAuthorization) {
                    gotResult.await();
                }
            } finally {
                authorizationLock.unlock();
            }

        }
    }

    private static class SyncToAirTableCommand extends Command {
        @Override
        public void execute(String args) throws InterruptedException {
//            SyncToAirTable.syncToAirTable();
        }
    }

    private static class LogoutCommand extends Command {
        @Override
        public void execute(String args) {
            haveAuthorization = false;
            client.send(new TdApi.LogOut(), defaultHandler);
        }
    }

    private static class QuitCommand extends Command {
        @Override
        public void execute(String args) {
            needQuit = true;
            haveAuthorization = false;
            client.send(new TdApi.Close(), defaultHandler);
        }
    }
}