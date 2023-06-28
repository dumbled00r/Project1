package AirTableUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;

public class abc {
    protected static String createRecord(JsonObject fields, String tableId, String baseId, String Token){
        String url = "https://api.airtable.com/v0/" + baseId + "/" + tableId;

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            JsonObject body = new JsonObject();
            body.add("fields", fields);

            JsonArray records = new JsonArray();
            records.add(body);

            JsonObject fullBody = new JsonObject();
            fullBody.add("records", records);

            HttpPost post = new HttpPost(url);
            post.setHeader("Authorization", "Bearer " + Token);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(fullBody.toString()));

            ClassicHttpResponse response = client.execute(post);

            if (response.getCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            } else {
                System.out.println("Error creating record: " + response.getCode());
                System.out.println(EntityUtils.toString(response.getEntity()));
                return null;
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error creating record: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args){
//        "tblexw8RrU1S7drHh", "appfpkYiYDZtMWJhA", "patDHXbaPvYn30swA.1e8a7fabfa00ccb9e2687143b1b79f46bd864fe86d10256f1ca44a4125046e45"
        String tableId = "tblCPMFRc2Qu6aN7s";
        String base = "apphDXbOWUoH9LMZp";
        String token = "patv0ej5dUApUGh1C.be1f25f23a0817bf20853e2a55693f96210c9272a7133836866c36d916c3bece";

        JsonObject a = new JsonObject();
        a.addProperty("User Id", "12312");
        a.addProperty("First Name", "sfdgdf");
        a.addProperty("Last Name", "sfdgdf");
        a.addProperty("Username", "sfdgdf");
        a.addProperty("Chat Id", 123412);

        String response = createRecord(a, tableId, base, token);
        System.out.println(response);
    }
}