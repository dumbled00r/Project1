package AirTableUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AirTable {

    protected final Path filePath;
    protected final String personal_access_token;
    protected final String baseId;
    Table groupData;
    Table userData;

    {
        filePath = Paths.get("airtablecfg.json");
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        Gson gson = new Gson();
        String jsonString = new String(Files.readAllBytes(filePath));
        JsonObject json = gson.fromJson(jsonString, JsonObject.class);
        personal_access_token = json.get("token").getAsString();
        baseId = json.get("tableId").getAsString();
    }

    public AirTable() throws IOException {
    }

    public void pushUserData(JsonObject jsonObject) {
        List<JsonObject> list = new ArrayList<>();
        list.add(jsonObject);
        userData.checkAllRecords(list, baseId, personal_access_token);
    }
}