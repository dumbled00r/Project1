package AirTableUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class AirTable {
    static String personal_access_token = "patSKeitTJVS6GY5Q.5479b0e63dbc534cb60aec48ba97951541b30e16ba7c73bac25e34d07af637f9";
    static String baseId = "appV34Ec7l8VWjbr3";
    static String tableId = "tblBZ0VAEApVRsjxK";
    static String userendpoint = "https://api.airtable.com/v0/" + baseId + "/" + tableId;
    protected static Gson gson = new GsonBuilder().setPrettyPrinting().create();

}