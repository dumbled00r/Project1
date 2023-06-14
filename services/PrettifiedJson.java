package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class PrettifiedJson extends Base{
    public static JsonElement convertToPrettifiedJson(Object object) {
        // Convert result obj to JSON string
        String jsonString = gson.toJson(object);

        // Serialize the JSON string into a JsonElement obj
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonString);
        return jsonElement;
    }
}

