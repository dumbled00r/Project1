package AirTableUtils;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class WriteToCSV {
    private static CSVWriter csvWriter;
    private static boolean fileCleanedUp = false;
    public static void writeToCSV(String[] data, Long chatId){
        try {
            // Set up the CSV writer with UTF-8 encoding
            String filePath = "./users.csv";
            File file = new File(filePath);

            if (!fileCleanedUp && file.exists()) {
                file.delete();
                fileCleanedUp = true;
            }

            // Use UTF-8-SIG encoding instead of UTF-8
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8.newEncoder().onMalformedInput(java.nio.charset.CodingErrorAction.REPLACE).onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPLACE));
            csvWriter = new CSVWriter(out, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

            // Write the data to the CSV file
            csvWriter.writeNext(data);

            // Flush the CSV writer
            csvWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (csvWriter != null) {
                    csvWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}