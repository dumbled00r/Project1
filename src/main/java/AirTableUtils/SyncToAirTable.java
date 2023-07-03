package AirTableUtils;

import Services.GetChat;
import Services.GetMainChatList;
import Services.GetMember;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

import static Services.GetMainChatList.chatIds;

public class SyncToAirTable {
    static Gson gson = new GsonBuilder().create();
    private static Set<JsonObject> jsonUserRes = new HashSet<>();
    private static Set<JsonObject> jsonGroupRes = new HashSet<>();
    public static void syncToAirTable() throws InterruptedException {
        GetMainChatList.loadChatIds();
        Thread.sleep(3000);
        for (long chatId : chatIds) {
            JsonObject groupRes = GetChat.getChat(chatId);
            List<JsonObject> res = GetMember.getMember(chatId);
            Thread.sleep(1000);
            for (JsonObject jsonObject : res) {
                if (!jsonObject.isJsonNull() && !jsonObject.isEmpty()) {
                    jsonUserRes.add(jsonObject);
                }
            }
            if (!groupRes.isJsonNull()) {
                jsonGroupRes.add(groupRes);
            }
        }
        for (JsonObject jsonObject : jsonUserRes) {
            System.out.println(jsonObject);
        }
        Map<String, JsonObject> idToJsonObject = new HashMap<>();

        for (JsonObject jsonObject : jsonUserRes) {
            String id = jsonObject.get("Id").getAsString();

            if (idToJsonObject.containsKey(id)) {
                JsonObject existingJsonObject = idToJsonObject.get(id);
                JsonArray chatIds = existingJsonObject.getAsJsonArray("Chat Ids");
                chatIds.add(jsonObject.get("Chat Id").getAsInt());
            } else {
                JsonObject newJsonObject = new JsonObject();
                newJsonObject.addProperty("Id", Long.parseLong(id));
                newJsonObject.addProperty("Username", jsonObject.get("Username").isJsonNull() ? "" : jsonObject.get("Username").getAsString());
                newJsonObject.addProperty("First Name", jsonObject.get("First Name").isJsonNull() ? "" : jsonObject.get("First Name").getAsString());
                newJsonObject.addProperty("Last Name", jsonObject.get("Last Name").isJsonNull() ? "" : jsonObject.get("Last Name").getAsString());
                JsonArray chatIds = new JsonArray();
                chatIds.add(jsonObject.get("Chat Id").getAsInt());
                newJsonObject.add("Chat Ids", chatIds);
                idToJsonObject.put(id, newJsonObject);
            }
        }

        List<JsonObject> mergedObjects = new ArrayList<>(idToJsonObject.values());

        for (JsonObject jsonObject : mergedObjects) {
            JsonArray chatIds = jsonObject.getAsJsonArray("Chat Ids");

            StringBuilder chatIdsString = new StringBuilder();
            for (int i = 0; i < chatIds.size(); i++) {
                if (i > 0) {
                    chatIdsString.append(";");
                }
                chatIdsString.append(chatIds.get(i).getAsString());
            }

            jsonObject.addProperty("Chat Ids", chatIdsString.toString());
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String mergedJson = gson.toJson(mergedObjects);

        System.out.println(mergedJson);

        AirTableGroup airTableGroup = new AirTableGroup();
        for (JsonObject jsonObject : jsonGroupRes) {
            airTableGroup.pushGroupData(jsonObject);
        }
        AirTableUser airTableUser = new AirTableUser();
        for (JsonObject jsonObject : mergedObjects) {
            airTableUser.pushUserData(jsonObject);
        }
    }
}
