package Utils;

public class ConvertToLong extends Base{
    public static long toLong(String arg) {
        long num = 0;
        try {
            num = Long.parseLong(arg);
        } catch (NumberFormatException ignored) {
        }
        return num;
    }
}

