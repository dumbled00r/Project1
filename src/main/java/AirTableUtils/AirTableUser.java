package AirTableUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class AirTableUser extends AirTable{
    public AirTableUser(){
        super();
        String response = Table.listTables(baseId, personal_access_token);
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        JsonArray tables = jsonObject.get("tables").getAsJsonArray();
        for (var ele : tables){
            JsonObject table = ele.getAsJsonObject();
            if (table.has("name") && table.get("name").isJsonPrimitive() && table.get("name").getAsString().equals("Users Data")){
                userData = new Table(table, baseId, personal_access_token);
                break;
            }
        }
    }
    public void pushUserData(JsonObject jsonObject){
        List<JsonObject> list = new ArrayList<>();
        list.add(jsonObject);
        userData.pullAllRecord(list, baseId, personal_access_token);
    }
}
