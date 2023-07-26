package utils;

public class ToInt extends Base{
    public static int toInt(String arg) {
        int result = 0;
        try {
            result = Integer.parseInt(arg);
        } catch (NumberFormatException ignored) {
            FileLogger.write(ignored.getMessage());
        }
        return result;
    }
}
