package AirTableUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;


public class AirTable {
    static String personal_access_token = "patv0ej5dUApUGh1C.be1f25f23a0817bf20853e2a55693f96210c9272a7133836866c36d916c3bece";
    static String baseId = "apphDXbOWUoH9LMZp";
    Table groupData;
    Table userData;

    public void pushUserData(JsonObject jsonObject) {
        List<JsonObject> list = new ArrayList<>();
        list.add(jsonObject);
        userData.pullAllRecord(list, baseId, personal_access_token);
    }
}