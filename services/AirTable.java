
import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;

public class AirTable {

    public static void main(String[] args) {
        try {
            // Set up the API endpoint
            String personal_access_token = "patXuz9EWHUzVSy2w.7ae493d8e7a9c2ec703b82da846049a1cba40a14ae07f03cd6097794af4b10cf";
            String baseId = "appsNLcg89KC3abTv";
            String tableName = "tblUVrIqGxmaD3zU0";
            String endpoint = "https://api.airtable.com/v0/" + baseId + "/" + tableName;

            // Set up the request headers
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + personal_access_token);
            connection.setRequestProperty("Content-Type", "application/json");

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}