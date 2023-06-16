package AirTableUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AirTable {

    public static void main(String[] args) {
        try {
            // Set up the API endpoint
            String personal_access_token = "patSKeitTJVS6GY5Q.5479b0e63dbc534cb60aec48ba97951541b30e16ba7c73bac25e34d07af637f9";
            String baseId = "appV34Ec7l8VWjbr3";
            String tableName = "tblBZ0VAEApVRsjxK";
            String endpoint = "https://api.airtable.com/v0/" + baseId + "/" + tableName;

            // Declare the request body variable
            JsonObject requestBody = new JsonObject();

            // Construct the request body
            BufferedReader csvReader = new BufferedReader(new FileReader("./users.csv"));
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");

                JsonObject fields = new JsonObject();
                fields.addProperty("User Id", data[0]);
                String[][] fieldMappings = {{"Username", "1"}, {"First Name", "2"}, {"Last Name", "3"}};
                for (String[] mapping : fieldMappings) {
                    int index = Integer.parseInt(mapping[1]);
                    String value = "";
                    try {
                        value = data[index];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        // do nothing
                    }
                    fields.addProperty(mapping[0], value);
                }
                JsonObject record = new JsonObject();
                record.add("fields", fields);

                requestBody.add("records", new Gson().toJsonTree(new JsonObject[]{record}));

                // Set up the request headers
                URL url = new URL(endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + personal_access_token);
                connection.setRequestProperty("Content-Type", "application/json");

                // Set up the request body
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBody.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                // Get the response data
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Print the response data
                System.out.println(response.toString());

                // Clear the request body for the next iteration
                requestBody = new JsonObject();
            }
            csvReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}