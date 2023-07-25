package AirTableUtils;

import Models.GroupChat;
import Models.User;
import Services.GetChat;
import Services.GetMainChatList;
import Services.GetMember;
import Utils.FileLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SyncToAirTable {
    private static Set<JsonObject> jsonUserRes = new HashSet<>();
    private static Set<JsonObject> jsonGroupRes = new HashSet<>();

    public static void syncToAirTable() throws InterruptedException, ExecutionException, IOException {
        GetMainChatList.loadChatIdsAsync().join(); // Wait for the CompletableFuture to complete

        List<Long> groupIds = new ArrayList<>();
        List<GroupChat> groupRes = GetChat.getMassChat().get();
        for (GroupChat groupResult : groupRes) {
            try {
                JsonObject groupJson = groupResult.toJsonObject();
                jsonGroupRes.add(groupJson);
            } catch (NullPointerException e) {
                FileLogger.write(groupResult.getId() + " is not a group chat");
            }
        }

        for (JsonObject jsonObject : jsonGroupRes) {
            Long groupId = jsonObject.get("Id").getAsLong();
            groupIds.add(groupId);
        }

        int numChats = groupIds.size();
        int processedChats = 0;

        for (long groupId : groupIds) {
            List<User> res = GetMember.getMember(groupId).join();
            Thread.sleep(5000);
            for (User user : res) {
                if (!user.toJsonObject().isJsonNull() && !user.toJsonObject().isEmpty()) {
                    JsonObject userJson = user.toJsonObject();
                    jsonUserRes.add(userJson);
                }
            }
            processedChats++;
            updateProgressBar(processedChats, numChats);
        }

        System.out.println("\nGroup Data processing completed!\n");

        Map<String, JsonObject> idToJsonObject = new HashMap<>();
        int numUsers = jsonUserRes.size();
        int processedUsers = 0;

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

            processedUsers++;
            updateProgressBar(processedUsers, numUsers);
        }
        System.out.println("\nUsers Data processing completed!");

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
        System.out.println("\nUploading...");
        try {
            AirTableGroup airTableGroup = new AirTableGroup();
            for (JsonObject jsonObject : jsonGroupRes) {
                airTableGroup.pushGroupData(jsonObject);
            }
            AirTableUser airTableUser = new AirTableUser();
            for (JsonObject jsonObject : mergedObjects) {
                airTableUser.pushUserData(jsonObject);
            }
            System.out.println("Update AirTable successfully");
        } catch (Exception e) {
            System.err.println("Failed to update AirTable, check logs for more information");
            FileLogger.write(e.getMessage());
        }
    }

    private static void updateProgressBar(int current, int total) {
        int progress = (int) ((double) current / total * 100);

        StringBuilder progressBar = new StringBuilder("[");
        int completeBars = progress / 2;
        int remainingBars = 50 - completeBars;

        progressBar.append("=".repeat(completeBars));
        progressBar.append(" ".repeat(remainingBars));
        progressBar.append(String.format("] %d%%", progress));

        System.out.print("\rProcessing data to upload: " + progressBar.toString());
        System.out.flush();
    }
}
