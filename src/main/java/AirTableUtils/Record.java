package AirTableUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Record{
    private final String id;
    private final JsonObject fields;
    private final String IdFieldVal;

    Record(JsonObject record) {
        this.id = record.get("id").getAsString();
        this.fields = record.get("fields").getAsJsonObject();
        if (this.fields.has("Id"))
            this.IdFieldVal = this.fields.get("Id").getAsString();
        else
            this.IdFieldVal = null;
    }
    protected String getId() {
        return this.id;
    }
    protected boolean equals(JsonObject fields, List<Field> fieldsList) {
        for (Field field : fieldsList) {
            if (fields.has(field.getName())) {
                String newVal = fields.get(field.getName()).toString();
                if (newVal.equals("false") || newVal.equals("null") || newVal.equals("[]"))
                    continue;
                if (!this.fields.has(field.getName()))
                {
                    return false;
                }
                String oldVal = this.fields.get(field.getName()).toString();
                if (field.getType().contains("date"))
                    if(oldVal.replaceAll(".000Z", "Z").equals(newVal))
                        continue;
                if (!newVal.equals(oldVal))
                {
                    return false;
                }
            }
        }
        return true;
    }
    protected String getValOfId() {
        return this.IdFieldVal;
    }
    protected JsonObject getFields() {
        return this.fields;
    }
    // API Methods
    protected static String listRecords(String tableId, String baseId, String token) {
        //curl "https://api.airtable.com/v0/{baseId}/{tableIdOrName}" \
        //-H "Authorization: Bearer YOUR_TOKEN"
        String url = "https://api.airtable.com/v0/" + baseId + "/" + tableId;

        try(CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Bearer " + token);
            get.setHeader("Content-Type", "application/json");

            ClassicHttpResponse response = client.execute(get);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    protected static String updateRecord(JsonObject fields, String recordId, String tableId, String baseId, String Token){
        String url = "https://api.airtable.com/v0/" + baseId + "/" + tableId + "/" + recordId;

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPatch patch = new HttpPatch(url);
            patch.setHeader("Authorization", "Bearer " + Token);
            patch.setHeader("Content-Type", "application/json; charset=utf-8");

            JsonObject fullBody = new JsonObject();
            fullBody.add("fields", fields);
            patch.setEntity(new StringEntity(fullBody.toString(), StandardCharsets.UTF_8));

            ClassicHttpResponse response = client.execute(patch);

            if (response.getCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            } else {
                return null;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
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
            post.setEntity(new StringEntity(fullBody.toString(), StandardCharsets.UTF_8));

            ClassicHttpResponse response = client.execute(post);

            if (response.getCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            } else {
                System.out.println("Error creating record: " + response.getCode());
                System.out.println("fullBody: " + fullBody);
                return null;
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error creating record: " + e.getMessage());
            return null;
        }
    }
    protected static boolean dropRecord(String recordId, String tableId, String baseId, String Token){
        String url = "https://api.airtable.com/v0/" + baseId + "/" + tableId + "/" + recordId;

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpDelete delete = new HttpDelete(url);
            delete.setHeader("Authorization", "Bearer " + Token);

            ClassicHttpResponse response = client.execute(delete);

            if (response.getCode() == 200) {
                System.out.println("Record " + recordId + " deleted");
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
