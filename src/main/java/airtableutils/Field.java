package airtableutils;

import com.google.gson.JsonObject;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

public class Field {
    private static final HttpClientResponseHandler<? extends ClassicHttpResponse> responseHandler = (HttpClientResponseHandler<ClassicHttpResponse>) classicHttpResponse -> null;

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
}
