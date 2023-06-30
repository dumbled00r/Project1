package AirTableUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class AirTableGroup extends AirTable{
    public AirTableGroup(){
        super();
        String response = Table.listTables(baseId, personal_access_token);
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        JsonArray tables = jsonObject.get("tables").getAsJsonArray();
        for (var ele : tables){
            JsonObject table = ele.getAsJsonObject();
            if (table.has("name") && table.get("name").isJsonPrimitive() && table.get("name").getAsString().equals("Group Data")){
                groupData = new Table(table, baseId, personal_access_token);
                break;
            }
        }
    }
    public void pushGroupData(JsonObject jsonObject){
        List<JsonObject> list = new ArrayList<>();
        list.add(jsonObject);
        groupData.pullAllRecord(list, baseId, personal_access_token);
    }
}
