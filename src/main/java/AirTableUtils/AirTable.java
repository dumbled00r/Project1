package AirTableUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class AirTable {
    static String personal_access_token = "patSKeitTJVS6GY5Q.5479b0e63dbc534cb60aec48ba97951541b30e16ba7c73bac25e34d07af637f9";
    static String baseId = "appV34Ec7l8VWjbr3";
    Table userData;
    public AirTable(){
        String response = Table.listTables(baseId, personal_access_token);
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        JsonArray tables = jsonObject.get("tables").getAsJsonArray();
        for (var ele : tables){
            JsonObject table = ele.getAsJsonObject();
            if (table.get("name").getAsString().equals("Users Data")){
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

    public static void main(String[] args){
        AirTable airTable = new AirTable();
        JsonObject a = new JsonObject();
        a.addProperty("Id", "xjkhhfdjsk");
        a.addProperty("First Name", "sfdgdf");
        a.addProperty("Last Name", "sfdgdf");
        a.addProperty("Username", "sfdgdf");
        a.addProperty("Chat Id", 12312);
        airTable.pushUserData(a);
    }

}