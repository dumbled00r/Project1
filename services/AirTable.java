package services;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.io.OutputStream;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AirTable {

    public static void main(String[] args) {
        try {
            // Set up the API endpoint
            String personal_access_token = "patqFFRZ6C8V4JGmm.f1b736a6cf3eb9f39d872e2ea68feeeba73b895aba1b4e255daa8b9851d2a24d";
            String baseId = "appo8OuYkDxTabJyB";
            String tableName = "tblnOrjxWkdR7mot2";
            String endpoint = "https://api.airtable.com/v0/" + baseId + "/" + tableName;

            // Declare the request body variable
            JSONObject requestBody = new JSONObject();

            // Get the path to the data file
            Path filePath = Paths.get("data.csv").toAbsolutePath();
            // Construct the request body
            BufferedReader csvReader = new BufferedReader(new FileReader("D:/Study/2022-2/Project1/services/data.csv"));
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");

                JSONObject fields = new JSONObject();
                fields.put("Email", data[0]);
                fields.put("Password", data[1]);
                fields.put("ID", data[2]);

                JSONObject record = new JSONObject();
                record.put("fields", fields);

                requestBody.put("records", new JSONObject[]{record});

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
                requestBody = new JSONObject();
            }
            csvReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}