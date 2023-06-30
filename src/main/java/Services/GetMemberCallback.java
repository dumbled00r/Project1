package Services;

import com.google.gson.JsonObject;

import java.util.List;

public interface GetMemberCallback {
    void onResult(List<JsonObject> members);
}