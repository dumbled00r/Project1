package utils;

public class TruncateString {
    public static String truncateStringIfNeeded(String str) {
        if (str.length() > 27) {
            return str.substring(0, 24) + "...";
        }
        return str;
    }
}
