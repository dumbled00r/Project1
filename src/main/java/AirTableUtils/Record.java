package AirTableUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Record extends AirTable{
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
        String urlStr = "https://api.airtable.com/v0/" + baseId + "/" + tableId;

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");

            if (conn.getResponseCode() != 200) {
                System.out.println("Error " + conn.getResponseCode());
                System.out.println("Broh");
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                sb.append(output);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    protected static String updateRecord(JsonObject fields, String recordId, String tableId, String baseId, String Token){
        String urlStr = "https://api.airtable.com/v0/" + baseId + "/" + tableId + "/" + recordId;

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("Authorization", "Bearer " + Token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JsonObject fullBody = new JsonObject();
            fullBody.add("fields", fields);

            byte[] input = fullBody.toString().getBytes(StandardCharsets.UTF_8);
            conn.getOutputStream().write(input);

            if (conn.getResponseCode() != 200) {
                System.out.println("Error updating record: " + conn.getResponseCode());
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    protected static String createRecord(JsonObject fields, String tableId, String baseId, String Token){
        String urlStr = "https://api.airtable.com/v0/" + baseId + "/" + tableId;

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + Token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JsonObject body = new JsonObject();
            body.add("fields", fields);

            JsonArray records = new JsonArray();
            records.add(body);

            JsonObject fullBody = new JsonObject();
            fullBody.add("records", records);

            byte[] input = fullBody.toString().getBytes(StandardCharsets.UTF_8);
            conn.getOutputStream().write(input);

            if (conn.getResponseCode() != 200) {
                System.out.println("Error creating record: " + conn.getResponseCode());
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    protected static boolean dropRecord(String recordId, String tableId, String baseId, String Token){
        // curl -X DELETE "https://api.airtable.com/v0/{baseId}/{tableIdOrName}/{recordId}" \
        //-H "Authorization: Bearer YOUR_TOKEN"
        String urlStr = "https://api.airtable.com/v0/" + baseId + "/" + tableId + "/" + recordId;

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + Token);

            if (conn.getResponseCode() != 200) {
                System.out.println("Error deleting record: " + conn.getResponseCode());
                return false;
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}