package services;

import com.google.gson.stream.JsonToken;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

public class GetCommand extends Base {
    protected static void getCommand() {
        String command = PromptString.promptString(commandsLine);
        String[] commands = command.split(" ", 3);
        try {
            switch (commands[0]) {
                case "help":{
                    System.out.println("gcs - Get Chat Lists ");
                    System.out.println("gc <ChatId> - Get Chat Information");
                    System.out.println("me - Get My Information");
                    System.out.println("sm <ChatId> <Message> - Send Message To An Existing Chat");
                    System.out.println("gu <UserId> - Get User Information");
                    System.out.println("add <ChatId> <UserId> - Add User To An Existing Chat");
                    System.out.println("pm <UserId> - Send Private Message To User ");
                    System.out.println("getmem <ChatId> - Get Members Of A Chat Group");
                    System.out.println("lo - Logout");
                    System.out.println("q - Quit");
                    break;
                }
                case "gcs": {
                    int limit = 50;
                    if (commands.length > 1) {
                        limit = ToInt.toInt(commands[1]);
                    }
                    GetMainChatList.getMainChatList(limit);
                    break;
                }
                case "gc": {
                    String[] args = commands[1].split(" ", 2);
                    GetChat.getChat(args);
                    break;
                }
                case "me": {
                    client.send(new TdApi.GetMe(), defaultHandler);
                    break;
                }
                case "sm": {
                    String[] args = commands[1].split(" ", 3);
                    SendMessage.sendMessage(ConvertToLong.toLong(args[0]), args[1]);
                    break;
                }
                case "gu": {
                    String[] args = commands[1].split(" ", 1);
                    GetUser.getUser(args[0]);
                    break;
                }
                case "add": {
                    String sChatId = commands[1];
                    String sUserId = commands[2];
                    AddMember.addSingleUser(ConvertToLong.toLong(sChatId), ConvertToLong.toLong(sUserId));
                    break;
                }
                case "pm":{
                    String[] args = commands[1].split(" ", 2);
                    client.send(new TdApi.CreatePrivateChat(ConvertToLong.toLong(args[0]), true), defaultHandler);
                    SendMessage.sendMessage(ConvertToLong.toLong(args[0]), args[1]);
                    break;
                }
                case "getmem":{
                    String[] args = commands[1].split(" ", 2);
                    GetMember.getMember(args);
                    break;
                }
                case "lo":
                    haveAuthorization = false;
                    client.send(new TdApi.LogOut(), defaultHandler);
                    break;
                case "q":
                    needQuit = true;
                    haveAuthorization = false;
                    client.send(new TdApi.Close(), defaultHandler);
                    break;
                default: {
                    System.err.println("Unsupported command: " + command);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Print.print("Not enough arguments");
        }
    }
}
