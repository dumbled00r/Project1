package AirTableUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class AirTableUser extends AirTable{
    Table group;
    public AirTableUser(){
        super();
        String response1 = Table.getListTables(baseId, personal_access_token);
        JsonObject jsonObject1 = new Gson().fromJson(response1, JsonObject.class);
        JsonArray tables1 = jsonObject1.get("tables").getAsJsonArray();
        for (var ele : tables1){
            JsonObject table = ele.getAsJsonObject();
            if (table.has("name") && table.get("name").isJsonPrimitive() && table.get("name").getAsString().equals("Group Data")){
                group = new Table(table, baseId, personal_access_token);
                break;
            }
        }
        String response = Table.getListTables(baseId, personal_access_token);
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
    @Override
    public void pushUserData(JsonObject jsonObject){
        String chatsId = jsonObject.get("Chat Ids").getAsString();
        String[] listChatId = chatsId.split(";");
        JsonArray jsonArray = new JsonArray();
        for (String id: listChatId){
            long longId = Long.parseLong(id);
            Record x = group.getRecord(longId);
            if (x != null){
                jsonArray.add(x.getId());
            }
        }
        jsonObject.remove("Chat Ids");
        jsonObject.add("Groups", jsonArray);

        List<JsonObject> list = new ArrayList<>();
        list.add(jsonObject);

        userData.checkAllRecords(list, baseId, personal_access_token);
    }
}
