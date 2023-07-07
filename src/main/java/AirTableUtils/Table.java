package AirTableUtils;

import com.google.gson.*;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Table{
    private int numChanges;
    private final String id;
    private final String name;
    private final List<Field> fields = new ArrayList<>();
    private final List<Record> records = new ArrayList<>();

    // Constructors
    protected Table(JsonObject table, String baseId, String token) {
        this.id = table.get("id").getAsString();
        this.name = table.get("name").getAsString();
        table.get("fields").getAsJsonArray().forEach(field -> this.fields.add(new Field(field.getAsJsonObject())));
        // Get Records
        syncRecord(baseId, token);
    }

    protected void syncRecord(String baseId, String token) {
//        records.clear();
        String records = Record.listRecords(id, baseId, token);
        if (records == null) {
            System.out.println("Error: Could not get records for table: " + name);
        } else {
            JsonObject recordsJson = new Gson().fromJson(records, JsonObject.class);
            JsonArray listRecords = recordsJson.get("records").getAsJsonArray();
            listRecords.forEach(record -> this.records.add(new Record(record.getAsJsonObject())));
        }
    }

    // Getters
    protected String getName() {
        return this.name;
    }
    protected String getId() {
        return this.id;
    }
    protected int getNumChanges() {
        return numChanges;
    }
    protected int getNumRecords() {
        return records.size();
    }

    // Handle Fields
    protected Field getField(String name) {
        for (Field field : this.fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    // Handle Records
    private boolean updateRecord(JsonObject fields, Record record, String baseId, String token) {
        String recordUpdate = Record.updateRecord(fields, record.getId(), id, baseId, token);
        if (recordUpdate == null) {
            System.out.println("Error: Could not update record: " + record.getValOfId() + " in table: " + name);
            return false;
        }
        JsonObject recordJson = JsonParser.parseString(recordUpdate).getAsJsonObject();
        records.remove(record);
        records.add(new Record(recordJson));
        System.out.println("Updated record: " + record.getValOfId() + " in table: " + name);
        return true;
    }
    private boolean addRecord(JsonObject fields, String baseId, String token) {
        String recordCreate = Record.createRecord(fields, id, baseId, token);
        if (recordCreate == null) {
            System.out.println("Error: Could not create record: " + fields.get("Id").getAsString() + " in table: " + name + "has id: " + id + " baseId: " + baseId);
            return false;
        }
        JsonObject recordJson = new Gson().fromJson(recordCreate, JsonObject.class);
        JsonArray listRecords = recordJson.get("records").getAsJsonArray();

        records.clear();
        listRecords.forEach(record -> this.records.add(new Record(record.getAsJsonObject())));

        System.out.println("Created record: " + fields.get("Id").getAsString() + " in table: " + name);
        return true;
    }
    protected Record getRecord(long idFieldVal) {
        for (Record record : this.records) {
            if (record.getValOfId() != 0) {
                if (record.getValOfId() == (idFieldVal)) {
                    return record;
                }
            }
        }
        return null;
    }
    private boolean pullRecord(JsonObject fields, String baseId, String token) {
        Record oldRecord = null;
        try {
            oldRecord = getRecord(fields.get("Id").getAsLong());
            if (oldRecord == null) {
                if (addRecord(fields, baseId, token)){
                    System.out.println("Add record: " + fields.get("Id").getAsString() + " in table: " + name);
                    numChanges++;
                    return true;
                }
                return false;
            }
            if (oldRecord.equals(fields, this.fields)) {
                return true;
            }
            if (updateRecord(fields, oldRecord, baseId, token)) {
                System.out.println("Update record: " + fields.get("Id").getAsString() + " in table: " + name);
                numChanges++;
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    protected void pullAllRecord(List<JsonObject> fields, String baseId, String token) {
        numChanges = 0;
        for (JsonObject field : fields) {
            if (!pullRecord(field, baseId, token)) {
                try {
                    System.out.println("Error: Could not pull record: " + field.get("Id").getAsString() + " in table: " + name);
                } catch (NullPointerException e) {}
                return;
            }
        }
        System.out.println("Pulled all records in table: " + name);
    }
    protected void dropRecord(List<JsonObject> fields, String baseId, String token) {
        List<Record> dropList = new ArrayList<>();
        for (Record record : this.records) {
            boolean isExist = false;
            for (JsonObject field : fields) {
                if (record.getValOfId() == (field.get("Id").getAsLong())) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                if (Record.dropRecord(record.getId(), id, baseId, token)) {
                    System.out.println("Deleted record: " + record.getValOfId() + " in table: " + name);
                    dropList.add(record);
                } else {
                    System.out.println("Error: Could not delete record: " + record.getValOfId() + " in table: " + name);
                }
            }
        }
        this.records.removeAll(dropList);
    }

    // API Methods
    protected static String listTables(String baseId, String token) {
        String url = "https://api.airtable.com/v0/meta/bases/" + baseId + "/tables";
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Bearer " + token);
            ClassicHttpResponse response = client.execute(get);
            if (response.getCode() != 200) {
                System.out.println("Error: Could not list tables");
                return null;
            }
            System.out.println("Connected to the tables");
            return EntityUtils.toString(response.getEntity());
        } catch (IOException | ParseException e) {
            System.out.println("Error: Could not list tables due to exception: " + e.getMessage());
            return null;
        }
    }
}