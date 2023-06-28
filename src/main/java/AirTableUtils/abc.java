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
        String tableId = "tblBZ0VAEApVRsjxK";
        String base = "appV34Ec7l8VWjbr3";
        String token = "patSKeitTJVS6GY5Q.5479b0e63dbc534cb60aec48ba97951541b30e16ba7c73bac25e34d07af637f9";

        JsonObject a = new JsonObject();
        a.addProperty("Id", "xjkhhfd213jsk");
        a.addProperty("First Name", "sfdgdf");
        a.addProperty("Last Name", "sfdgdf");
        a.addProperty("Username", "sfdgdf");
        a.addProperty("Chat Id", 123412);

        String response = createRecord(a, tableId, base, token);
        System.out.println(response);
    }
}