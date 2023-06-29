package AirTableUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;


public class AirTable {
    static String personal_access_token = "patv0ej5dUApUGh1C.be1f25f23a0817bf20853e2a55693f96210c9272a7133836866c36d916c3bece";
    static String baseId = "apphDXbOWUoH9LMZp";
    Table Data;

//    public AirTable(){
//        String response = Table.listTables(baseId, personal_access_token);
//        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
//        JsonArray tables = jsonObject.get("tables").getAsJsonArray();
//        for (var ele : tables){
//            JsonObject table = ele.getAsJsonObject();
//            if (table.has("name") && table.get("name").isJsonPrimitive() && table.get("name").getAsString().equals("Users Data")){
//                Table userData = new Table(table, baseId, personal_access_token);
//                break;
//            }
//        }
//    }
//
//    public void pushUserData(JsonObject jsonObject){
//        List<JsonObject> list = new ArrayList<>();
//        list.add(jsonObject);
//        userData.pullAllRecord(list, baseId, personal_access_token);
//    }
//
//    public static void main(String[] args){
//
//        AirTable airTable = new AirTable();
//        JsonObject a = new JsonObject();
//        a.addProperty("Id", 1231);
//        a.addProperty("First Name", "sfdgdf");
//        a.addProperty("Last Name", "sfdgdf");
//        a.addProperty("Username", "sfdgddasdsadasf");
//        a.addProperty("Chat Title", "asdas");
//        airTable.pushUserData(a);
//    }
}