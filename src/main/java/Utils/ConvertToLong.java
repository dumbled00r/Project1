package Utils;

public class ConvertToLong extends Base{
    public static long toLong(String arg) {
        long chatId = 0;
        try {
            chatId = Long.parseLong(arg);
        } catch (NumberFormatException ignored) {
        }
        return chatId;
    }
}

