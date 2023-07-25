package utils;

import java.util.concurrent.CompletableFuture;

public class PromptString extends Base {
    public static CompletableFuture<String> promptStringAsync(String prompt) {
        CompletableFuture<String> future = new CompletableFuture<>();
        System.out.print(prompt);
        currentPrompt = prompt;
        try {
            String str = reader.readLine();
            currentPrompt = null;
            future.complete(str);
        } catch (Exception e) {
            currentPrompt = null;
            future.completeExceptionally(e);
        }
        return future;
    }
}