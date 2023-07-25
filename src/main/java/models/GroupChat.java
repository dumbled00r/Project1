package models;

import com.google.gson.JsonObject;

public class GroupChat {
    private long id;
    private String type;
    private String title;
    private int membersCount;
    private String description;
    private String inviteLink;

    public GroupChat(long id, String type, String title, int membersCount, String description, String inviteLink) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.membersCount = membersCount;
        this.description = description;
        this.inviteLink = inviteLink;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public String getDescription() {
        return description;
    }

    public String getInviteLink() {
        return inviteLink;
    }

    public static GroupChat fromJson(JsonObject json) {
        long id = json.get("Id").getAsLong();
        String type = json.get("type").getAsString();
        String title = json.get("title").getAsString();
        int membersCount = json.get("members count").getAsInt();
        String description = json.get("description").getAsString();
        String inviteLink = json.get("invite link").getAsString();
        return new GroupChat(id, type, title, membersCount, description, inviteLink);
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Id", getId());
        jsonObject.addProperty("type", getType());
        jsonObject.addProperty("title", getTitle());
        jsonObject.addProperty("description", getDescription());
        jsonObject.addProperty("invite link", getInviteLink());
        return jsonObject;
    }
}