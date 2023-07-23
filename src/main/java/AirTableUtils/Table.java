package AirTableUtils;

import Utils.FileLogger;
import com.google.gson.*;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Table{
    private int numChanges;
    private final String id;
    private final String name;
    private final List<Field> fields = new ArrayList<>();
    private final List<Record> records = new ArrayList<>();

    protected Table(JsonObject table, String baseId, String token) {
        this.id = table.get("id").getAsString();
        this.name = table.get("name").getAsString();
        table.get("fields").getAsJsonArray().forEach(field -> this.fields.add(new Field(field.getAsJsonObject())));
        // SYNC
        syncRecord(baseId, token);
    }

    protected void syncRecord(String baseId, String token) {
        String records = Record.listRecords(id, baseId, token);
        if (records == null) {
            FileLogger.write("Error: Could not get records for table: " + name);
        } else {
            this.records.clear();
            JsonObject recordsJson = new Gson().fromJson(records, JsonObject.class);
            JsonArray listRecords = recordsJson.get("records").getAsJsonArray();
            listRecords.forEach(record -> this.records.add(new Record(record.getAsJsonObject())));
        }
    }

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

    protected Field getField(String name) {
        for (Field field : this.fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    private boolean updateRecord(JsonObject fields, Record record, String baseId, String token) {
        String recordUpdate = Record.updateRecord(fields, record.getId(), id, baseId, token);
        if (recordUpdate == null) {
            FileLogger.write("Error: Could not update record: " + record.getValOfId() + " in table: " + name);
            return false;
        }
        JsonObject recordJson = JsonParser.parseString(recordUpdate).getAsJsonObject();
        records.remove(record);
        records.add(new Record(recordJson));
        FileLogger.write("Updated record: " + record.getValOfId() + " in table: " + name);
        return true;
    }
    private boolean addRecord(JsonObject fields, String baseId, String token) {
        String recordCreate = Record.createRecord(fields, id, baseId, token);
        if (recordCreate == null) {
            FileLogger.write("Error: Could not create record: " + fields.get("Id").getAsString() + " in table: " + name + "has id: " + id + " baseId: " + baseId);
            return false;
        }
        JsonObject recordJson = new Gson().fromJson(recordCreate, JsonObject.class);
        JsonArray listRecords = recordJson.get("records").getAsJsonArray();

        listRecords.forEach(record -> this.records.add(new Record(record.getAsJsonObject())));

        FileLogger.write("Created record: " + fields.get("Id").getAsString() + " in table: " + name);
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
    private boolean processRecord(JsonObject fields, String baseId, String token) {
        Record oldRecord = null;
        try {
            oldRecord = getRecord(fields.get("Id").getAsLong());
            if (oldRecord == null) {
                if (addRecord(fields, baseId, token)){
                    FileLogger.write("Add record: " + fields.get("Id").getAsString() + " in table: " + name);
                    numChanges++;
                    return true;
                }
                return false;
            }
            if (oldRecord.equals(fields, this.fields)) {
                return true;
            }
            if (updateRecord(fields, oldRecord, baseId, token)) {
                FileLogger.write("Update record: " + fields.get("Id").getAsString() + " in table: " + name);
                numChanges++;
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    protected void processAllRecords(List<JsonObject> fields, String baseId, String token) {
        numChanges = 0;
        for (JsonObject field : fields) {
            if (!processRecord(field, baseId, token)) {
                try {
                    FileLogger.write("Error: Could not check record: " + field.get("Id").getAsString() + " in table: " + name);
                } catch (NullPointerException e) {}
                return;
            }
        }
        FileLogger.write("Checked all records in table: " + name);
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
                if (Record.deleteRecord(record.getId(), id, baseId, token)) {
                    FileLogger.write("Deleted record: " + record.getValOfId() + " in table: " + name);
                    dropList.add(record);
                } else {
                    FileLogger.write("Error: Could not delete record: " + record.getValOfId() + " in table: " + name);
                }
            }
        }
        this.records.removeAll(dropList);
    }

    protected static String getListTables(String baseId, String token) {
        String url = "https://api.airtable.com/v0/meta/bases/" + baseId + "/tables";
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Bearer " + token);
            ClassicHttpResponse response = client.execute(get);
            if (response.getCode() != 200) {
                FileLogger.write("Error: Could not get the list of tables");
                return null;
            }
            return EntityUtils.toString(response.getEntity());
        } catch (IOException | ParseException e) {
            FileLogger.write("Error: Could not get the list tables: " + e.getMessage());
            return null;
        }
    }
}
