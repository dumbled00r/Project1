package AirTableUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class AirTable {

    Gson gson = new Gson();
    String jsonString = new String(Files.readAllBytes(Paths.get("airtablecfg.json")));
    JsonObject json = gson.fromJson(jsonString, JsonObject.class);
    String personal_access_token = json.get("token").getAsString();
    String baseId = json.get("tableId").getAsString();
    Table groupData;
    Table userData;

    public AirTable() throws IOException {
    }

    public void pushUserData(JsonObject jsonObject) {
        List<JsonObject> list = new ArrayList<>();
        list.add(jsonObject);
        userData.checkAllRecords(list, baseId, personal_access_token);
    }
}