package AirTableUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.Charset;

public class SyncCSV extends AirTable {
    public static void main(String[] args) {
        try {
            // Set up the request URL and headers
            String url = "https://api.airtable.com/v0/appnC7konF9VsPdbU/tblCcizv2ytExED7f/sync/EmvIBhx1";
            String personalAccessToken = personal_access_token;
            String csvFilePath = "./users.csv";

            // Read the CSV data from the file
            BufferedReader reader = new BufferedReader(new FileReader(csvFilePath, StandardCharsets.UTF_8));
            String line;
            List<Map<String, Object>> records = new ArrayList<>();
            // Add headers to the uploaded data
            Map<String, Object> header = new HashMap<>();
            header.put("User Id", "User Id");
            header.put("Username", "Username");
            header.put("First Name", "First Name");
            header.put("Last Name", "Last Name");
            header.put("Chat Id", "Chat Id");
            records.add(header);

            // Add records
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(";");
                Map<String, Object> record = new HashMap<>();
                record.put("User Id", fields[0]);
                record.put("Username", fields[1]);
                record.put("First Name", fields[2]);
                record.put("Last Name", fields[3]);
                record.put("Chat Id", fields[4]);
                records.add(record);
                System.out.println(record);
            }
            reader.close();

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + personalAccessToken);
            connection.setRequestProperty("Content-Type", "text/csv; charset=UTF-8");
            connection.setDoOutput(true);

            // Write the CSV data to the request body
            OutputStreamWriter outputStream = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            for (Map<String, Object> record : records) {
                outputStream.write(record.get("User Id") + ",");
                outputStream.write(record.get("Username") + ",");
                outputStream.write(record.get("First Name") + ",");
                outputStream.write(record.get("Last Name") + ",");
                outputStream.write(record.get("Chat Id") + "\n");
            }
            outputStream.flush();
            outputStream.close();

            // Read the response from the API
            int responseCode = connection.getResponseCode();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = responseReader.readLine()) != null) {
                response.append(responseLine);
            }
            responseReader.close();

            // Print the response to the console
            System.out.println("Response code: " + responseCode);
            System.out.println("Response body: " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
