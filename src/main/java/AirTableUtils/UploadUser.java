//package AirTableUtils;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//
//import java.util.*;
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.ProtocolException;
//import java.net.URL;
//
//public class UploadUser extends AirTable{
//    public static void uploadUser(JsonObject userData) throws IOException {
//        String url = "https://api.airtable.com/v0/" + baseId + "/" + tableId;
//        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
//        con.setRequestMethod("POST");
//        con.setRequestProperty("Authorization", "Bearer " + personal_access_token);
//        con.setRequestProperty("Content-Type", "application/json");
//
//        // Set the request body to the JSON data
//        con.setDoOutput(true);
//        DataOutputStream out = new DataOutputStream(con.getOutputStream());
//        out.writeBytes(userData.toString());
//        out.flush();
//        out.close();
//
//        // Check for successful response
//        int responseCode = con.getResponseCode();
//        if (responseCode != HttpURLConnection.HTTP_OK) {
//            System.out.println(userData);
//            System.out.println(con.getResponseMessage());
//            System.out.println("Error: Could not create record in table: " + tableId);
//        }
//
//        // Print response data
//        Scanner scanner = new Scanner(con.getInputStream());
//        StringBuilder response = new StringBuilder();
//        while (scanner.hasNextLine()) {
//            response.append(scanner.nextLine());
//        }
//        scanner.close();
//        System.out.println("Added record to table: " + tableId);
//    }
//}
