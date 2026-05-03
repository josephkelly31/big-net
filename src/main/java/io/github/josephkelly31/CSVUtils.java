package io.github.josephkelly31;

public class CSVUtils {

    public static boolean isValidCSV(String filePath) {
        return filePath != null && filePath.toLowerCase().endsWith(".csv");
    }

    public static boolean isValidEntry(String[] entry) {
        return entry != null && entry.length == 4; // Assuming 4 fields: type, amount, loss/gain, date
    }
}
