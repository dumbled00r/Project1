package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
public class PromptString extends Base {
    public static String promptString(String prompt) {
        System.out.print(prompt);
        currentPrompt = prompt;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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
