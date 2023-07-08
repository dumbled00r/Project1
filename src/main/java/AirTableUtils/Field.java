package AirTableUtils;

import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.net.URI;

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
