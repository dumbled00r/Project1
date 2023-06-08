package services;

public class ConvertToLong extends authorize{
    public static long toLong(String arg) {
        long chatId = 0;
        try {
            chatId = Long.parseLong(arg);
        } catch (NumberFormatException ignored) {
        }
        return chatId;
    }
}

