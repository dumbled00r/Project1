package Services;

import AirTableUtils.AirTable;
import AirTableUtils.AirTableGroup;
import AirTableUtils.AirTableUser;
import Utils.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.drinkless.tdlib.TdApi;
import java.util.List;

import static Services.GetMainChatList.chatIds;


public class GetCommand extends Base {
    protected static void getCommand() {
        String command = PromptString.promptString(commandsLine);
        String[] commands = command.split(" ", 2);
        try {
            switch (commands[0].toLowerCase()) {
                case "":{
                    break;
                }
                case "help":{
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
                    GetChat.getChat(Long.parseLong(args[0]));
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
                case "gu": {
                    GetUser.getUser(Long.parseLong(commands[1]), "ABC");
                    break;
                }
                case "add": {
                    String sChatId = commands[1];
                    String sUserId = commands[2];
                    Long longChatId = ConvertToLong.toLong(sChatId);
                    AddMember.addSingleUser(longChatId, ConvertToLong.toLong(sUserId));
                    break;
                }
                case "pm":{
                    String[] args = commands[1].split(" ", 2);
                    client.send(new TdApi.CreatePrivateChat(ConvertToLong.toLong(args[0]), true), null);
                    SendMessage.sendMessage(ConvertToLong.toLong(args[0]), args[1]);
                    break;
                }
                case "getmem":{
                    String[] args = commands[1].split(" ", 2);
                    GetMember.getMember(Long.parseLong(args[0]));
                    break;
                }
                case "sync":{
                    GetMainChatList.loadChatIds();
                    Thread.sleep(3000);
                    for (long chatId : chatIds) {
//                        JsonObject groupRes = GetChat.getChat(chatId);

//                        if (!groupRes.isJsonNull()) {
//                            jsonGroupRes.add(groupRes);
                    }
                    Thread.sleep(2000);
                    for (JsonObject obj : jsonUserRes) {
                        System.out.println(obj);
                    }
//                    AirTableGroup airTableGroup = new AirTableGroup();
//                    for (JsonObject jsonObject : jsonGroupRes) {
//                        airTableGroup.pushGroupData(jsonObject);
//                    }
//                    AirTableUser airTableUser = new AirTableUser();
//                    for (JsonObject jsonObject : jsonUserRes) {
//                        airTableUser.pushUserData(jsonObject);
//                    }
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
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}