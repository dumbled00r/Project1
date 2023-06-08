package services;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

public class GetCommand extends authorize {
    protected static void getCommand() {
        String command = promptString(commandsLine);
        String[] commands = command.split(" ", 2);
        try {
            switch (commands[0]) {
                case "gcs": {
                    int limit = 50;
                    if (commands.length > 1) {
                        limit = toInt(commands[1]);
                    }
                    getMainChatList(limit);
                    break;
                }
                case "gc": {
                    client.send(new TdApi.GetChat(ConvertToLong.toLong(commands[1])), defaultHandler);
                    break;
                }
                case "me": {
                    client.send(new TdApi.GetMe(), defaultHandler);
                    break;
                }
                case "sm": {
                    String[] args = commands[1].split(" ", 2);
                    SendMessage.sendMessage(ConvertToLong.toLong(args[0]), args[1]);
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
                case "u":
                    client.send(new TdApi.GetUser((int) ConvertToLong.toLong(commands[1])), defaultHandler);
                    break;
                case "add": {
                    String[] args = commands[1].split(" ", 3);
                    client.send(new TdApi.AddChatMember(ConvertToLong.toLong(args[0]), (int) ConvertToLong.toLong(args[1]), 13), defaultHandler);
                    break;
                }
                case "getmem": {
                    String[] args = commands[1].split(" ", 2);
                    GetMember.getMember(args);

                    break;
                }
                case "link": {
                    String[] args = commands[1].split(" ",  2);
                    client.send(new TdApi.GetChatInviteLinks(ConvertToLong.toLong(args[0]), ConvertToLong.toLong(args[1]), false, 0, null, 10 ), defaultHandler);
                    break;
                }
                case "pm":{
                    String[] args = commands[1].split(" ", 2);
                    client.send(new TdApi.CreatePrivateChat(ConvertToLong.toLong(args[0]), true), defaultHandler);
                    SendMessage.sendMessage(ConvertToLong.toLong(args[0]), args[1]);
                    break;
                }
                default: {
                    System.err.println("Unsupported command: " + command);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            print("Not enough arguments");
        }
    }
}
