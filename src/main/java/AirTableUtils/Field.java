package AirTableUtils;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Field extends AirTable {
    private final String id;
    private final String name;
    private final String type;

    protected Field(JsonObject field) {
        this.id = field.get("id").getAsString();
        this.name = field.get("name").getAsString();
        this.type = field.get("type").getAsString();
    }

    protected String getId() {
        return this.id;
    }

    protected String getName() {
        return this.name;
    }

    protected String getType() {
        return this.type;
    }

    // API Methods
    protected static String createField(JsonObject field, String tableId, String baseId, String token) {
        try {
            URL url = new URL("https://api.airtable.com/v0/meta/bases/" + baseId + "/tables/" + tableId + "/fields");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String input = field.toString();
            conn.getOutputStream().write(input.getBytes());

            if (conn.getResponseCode() != 200) {
                System.out.println("Error " + conn.getResponseCode());
                return conn.getResponseMessage();
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

    protected static String updateField(JsonObject field, String fieldId, String tableId, String baseId, String token) {
        try {
            URL url = new URL("https://api.airtable.com/v0/meta/bases/" + baseId + "/tables/" + tableId + "/fields/" + fieldId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String input = field.toString();
            conn.getOutputStream().write(input.getBytes());

            if (conn.getResponseCode() != 200) {
                System.out.println("Error updating field: " + conn.getResponseCode());
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

    protected static void getField(String personal_access_token, String baseId, String tableId) {
        try {
            URL url = new URL("https://api.airtable.com/v0/" + baseId + "/" + tableId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + personal_access_token);
            conn.setRequestProperty("Content-Type", "application/json");

            if (conn.getResponseCode() != 200) {
                System.out.println("Error " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}