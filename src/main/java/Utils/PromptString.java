package Utils;

import java.io.IOException;

public class PromptString extends Base {
    public static String promptString(String prompt) {
        System.out.print(prompt);
        currentPrompt = prompt;
        String str = "";
        try {
            str = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentPrompt = null;
        return str;
    }
}
