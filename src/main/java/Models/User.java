package Models;

import com.google.gson.JsonObject;

public class User {
    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private long chatId;
    private String type;

    public User(long id, String username, String firstName, String lastName, long chatId, String type) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.chatId = chatId;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getChatId() {
        return chatId;
    }

    public String getType() {
        return type;
    }

    public static User fromJson(JsonObject json) {
        long id = json.get("Id").getAsLong();
        String username = json.get("Username").getAsString();
        String firstName = json.get("First Name").getAsString();
        String lastName = json.get("Last Name").getAsString();
        long chatId = json.get("Chat Id").getAsLong();
        String type = json.get("Type").getAsString(); // new field
        return new User(id, username, firstName, lastName, chatId, type);
    }
}