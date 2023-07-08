package AirTableUtils;

import Models.GroupChat;
import Models.User;
import Services.GetChat;
import Services.GetMainChatList;
import Services.GetMember;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static Services.GetMainChatList.chatIds;

public class SyncToAirTable {
    private static Set<JsonObject> jsonUserRes = new HashSet<>();
    private static Set<JsonObject> jsonGroupRes = new HashSet<>();

    public static void syncToAirTable() throws InterruptedException, ExecutionException {
        GetMainChatList.loadChatIdsAsync().join(); // Wait for the CompletableFuture to complete

        int numChats = chatIds.length;
        int processedChats = 0;

        System.out.print("Processing chats: [");
        for (long chatId : chatIds) {
            CompletableFuture<GroupChat> groupRes = GetChat.getChat(chatId);
            List<User> res = GetMember.getMember(chatId).join();
            Thread.sleep(1000);
            for (User user : res) {
                if (!user.toJsonObject().isJsonNull() && !user.toJsonObject().isEmpty()) {
                    JsonObject userJson = user.toJsonObject();
                    jsonUserRes.add(userJson);
                }
            }
            try {
                JsonObject groupJson = groupRes.get().toJsonObject();
                jsonGroupRes.add(groupJson);
            } catch (NullPointerException e) {
                if (Thread.currentThread().getStackTrace()[2].getClassName().equals(SyncToAirTable.class.getName())) {
                    System.out.println("Not a group chat");
                }
            }

            // Update the progress bar
            processedChats++;
            updateProgressBar(processedChats, numChats);
        }
        System.out.println("]");

        if (Thread.currentThread().getStackTrace()[2].getClassName().equals(SyncToAirTable.class.getName())) {
            System.out.println(jsonGroupRes);
        }
        Map<String, JsonObject> idToJsonObject = new HashMap<>();

        int numUsers = jsonUserRes.size();
        int processedUsers = 0;

        System.out.print("Processing users: [");
        for (JsonObject jsonObject : jsonUserRes) {
            String id = jsonObject.get("Id").getAsString();

            if (idToJsonObject.containsKey(id)) {
                // Merge the current JSON object with an existing one
                JsonObject existingJsonObject = idToJsonObject.get(id);
                JsonArray chatIds = existingJsonObject.getAsJsonArray("Chat Ids");
                chatIds.add(jsonObject.get("Chat Id").getAsLong());
            } else {
                // Create a new JSON object
                JsonObject newJsonObject = new JsonObject();
                newJsonObject.addProperty("Id", Long.parseLong(id));
                newJsonObject.addProperty("Username", jsonObject.get("Username").isJsonNull() ? "" : jsonObject.get("Username").getAsString());
                newJsonObject.addProperty("First Name", jsonObject.get("First Name").isJsonNull() ? "" : jsonObject.get("First Name").getAsString());
                newJsonObject.addProperty("Last Name", jsonObject.get("Last Name").isJsonNull() ? "" : jsonObject.get("Last Name").getAsString());
                newJsonObject.addProperty("Type", jsonObject.get("Type").isJsonNull() ? "" : jsonObject.get("Type").getAsString());
                JsonArray chatIds = new JsonArray();
                chatIds.add(jsonObject.get("Chat Id").getAsLong());
                newJsonObject.add("Chat Ids", chatIds);
                idToJsonObject.put(id, newJsonObject);
            }

            // Update the progress bar
            processedUsers++;
            updateProgressBar(processedUsers, numUsers);
        }
        System.out.println("]");

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

        AirTableGroup airTableGroup = new AirTableGroup();
        for (JsonObject jsonObject : jsonGroupRes) {
            airTableGroup.pushGroupData(jsonObject);
        }
        AirTableUser airTableUser = new AirTableUser();
        for (JsonObject jsonObject : mergedObjects) {
            airTableUser.pushUserData(jsonObject);
        }
    }

    private static void updateProgressBar(int current, int total) {
        int progress = (int) ((double) current / total * 100);

        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(progress / 2));
        sb.append(" ".repeat(50 - progress / 2));
        sb.append(String.format("] %d%%", progress));

        System.out.print("\r" + sb.toString());
        System.out.flush();
    }
}