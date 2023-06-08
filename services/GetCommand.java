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
                    client.send(new TdApi.GetChat(getChatId(commands[1])), defaultHandler);
                    break;
                }
                case "me": {
                    client.send(new TdApi.GetMe(), defaultHandler);
                    break;
                }
                case "sm": {
                    String[] args = commands[1].split(" ", 2);
                    SendMessage.sendMessage(getChatId(args[0]), args[1]);
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
                    client.send(new TdApi.GetUser((int)getChatId(commands[1])), defaultHandler);
                    break;
                case "add": {
                    String[] args = commands[1].split(" ", 3);
                    client.send(new TdApi.AddChatMember(getChatId(args[0]), (int) getChatId(args[1]), 13), defaultHandler);
                    break;
                }
                case "getmem": {
                    String[] args = commands[1].split(" ", 2);

                    client.send(new TdApi.GetChat(getChatId(args[0])), new Client.ResultHandler() {
                        @Override
                        public void onResult(TdApi.Object object) {
                            if (object instanceof TdApi.Chat){
                                TdApi.Chat chat = (TdApi.Chat) object;
                                if (chat.type instanceof TdApi.ChatTypeSupergroup){
                                    long supergroupId = ((TdApi.ChatTypeSupergroup) chat.type).supergroupId;
                                    client.send(new TdApi.GetSupergroupMembers(supergroupId, null, 0, 10), defaultHandler);
                                } else { System.out.println("This chat is not a super group");}
                            } else {System.out.println("Handle error");}
                        }
                    }, null);
                    break;
                }
                case "link": {
                    String[] args = commands[1].split(" ",  2);
                    client.send(new TdApi.GetChatInviteLinks(getChatId(args[0]),getChatId(args[1]), false, 0, null, 10 ), defaultHandler);
                    break;
                }
                case "pm":{
                    String[] args = commands[1].split(" ", 2);
                    client.send(new TdApi.CreatePrivateChat(getChatId(args[0]), true), defaultHandler);
                    SendMessage.sendMessage(getChatId(args[0]), args[1]);
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
