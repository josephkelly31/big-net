package io.github.josephkelly31;

import java.util.Scanner;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.text.SimpleDateFormat;

public class App {
    public static void main(String[] args) {

        String temporary_csv_file_path = "finance.csv";
        // Print greeting
        App.setUp(temporary_csv_file_path);
        // Update csv with new finance data
        App.createFinanceCSV(temporary_csv_file_path);
        // Print farewell
        App.printFarewell();

    }

    public static void setUp(String csv_file_path){
        // Print greeting and check for existing finance.csv file
        System.out.println("Welcome to Big Net - Your Personal Finance Tracker!");
        
        File csv_file = new File(csv_file_path);
        if (!csv_file.exists()) {
            try {
                if (csv_file.createNewFile()) {
                    System.out.println("Created new finance.csv file.");
                }
                return; 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Date lastModified = new java.util.Date(csv_file.lastModified());
        System.out.println(csv_file_path + " file found. Last modified: " + lastModified.toString());
        App.compressEntry(csv_file_path, lastModified);
        csv_file.delete();
    }

    public static void printFarewell(){
        // Print farewell message
        System.out.println("Thank you for using Big Net. Goodbye!");
    }

    public static void compressEntry(String file_path, Date date){
        // Compress the file_path file into a gzip file named with the current date
        
        File fileToCompress = new File(file_path);
        if (!fileToCompress.exists()) {
            System.out.println("File not found: " + file_path);
            return;
        }

        if (date == null){
            throw new IllegalArgumentException("Date cannot be null for compression purposes");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(date);
        String compressedFileName = "finance_" + formattedDate + ".gz";
        File compressedFile = new File(compressedFileName);
        if (compressedFile.exists()) {
            System.out.println("Compressed file for" + date.toString() +  " already exists: " + compressedFileName);
            App.updateCompressedEntry(date, compressedFileName, fileToCompress);
            return;
        }
        App.compressFile(fileToCompress, compressedFileName);
    }

    public static void updateCompressedEntry(Date date, String compressedFileName, File updateFile){
        // Update existing compressed file with new entries from updateFile, deleting updateFile after use

        System.out.println("Appending to existing compressed files is not yet implemented.");
        String decompressed_file_name = "decompressed_" + compressedFileName;
        App.decompressFile(compressedFileName, decompressed_file_name);
        File decompressed_file = new File(decompressed_file_name);
        App.conjugateFiles(decompressed_file, updateFile);
        App.updateCompressedFile(decompressed_file, compressedFileName);
        decompressed_file.delete();
        updateFile.delete();
    }

    public static void decompressFile(String compressedFilePath, String decompressedFilePath) {
        // Decompress a gzip file to a specified location
        
        try(
            FileInputStream fis = new FileInputStream(compressedFilePath);
            GZIPInputStream gzis = new GZIPInputStream(fis);
            FileOutputStream fos = new FileOutputStream(decompressedFilePath))
        {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            System.out.println("File " + compressedFilePath + " decompressed to: " + decompressedFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void conjugateFiles(File originalFile, File decompressedFile){
        // Append contents of decompressedFile to originalFile

        try(
            FileInputStream fis = new FileInputStream(decompressedFile);
            FileOutputStream fos = new FileOutputStream(originalFile, true)) // Append mode
        {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            System.out.println("Decompressed file " + decompressedFile + " appended to: " + originalFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateCompressedFile(File fileToCompress, String compressedFileName) {
        // Re-compress the updated file, overwriting the existing compressed file
        
        File currentCompressedFile = new File(compressedFileName);
        if (currentCompressedFile.delete()){
            App.compressFile(fileToCompress, compressedFileName);
        } else {
            System.out.println("Failed to delete existing compressed file: " + compressedFileName);
        }
    }

    public static void compressFile(File fileToCompress, String compressedFileName) {
        // Compress a file into gzip format

        try (
            FileInputStream fis = new FileInputStream(fileToCompress);
            FileOutputStream fos = new FileOutputStream(compressedFileName);
            GZIPOutputStream gzos = new GZIPOutputStream(fos))
        {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzos.write(buffer, 0, len);
            }
            System.out.println("File "+ fileToCompress +" compressed to: " + compressedFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createFinanceCSV(String csv_file_path){
        // Create or update finance.csv with user input
        
        String[] finance_questions = {
                "Enter the type of transaction (e.g., 'income', 'expense'): ",
                "Enter the amount: ",
                "Is this a 'gain' or a 'loss'?: ",
                "Enter the date (YYYY-MM-DD): "
        };

        boolean request_entry = true;

        String[] entry = new String[4];
        Scanner scanner = new Scanner(System.in);
        while (request_entry) {

            for (int i = 0; i < finance_questions.length; i++) {
                String question = finance_questions[i];
                System.out.print(question);
                String answer = scanner.nextLine();
                entry[i] = answer;
            }

            System.out.println(String.join(" ", entry));
            if (CSVUtils.isValidEntry(entry)) {
                try (PrintWriter pw = new PrintWriter(new FileWriter(csv_file_path, true))) {
                    CSVWriter writer = new CSVWriter(pw);
                    writer.writeNext(entry);
                    writer.close();
                    System.out.println("Entry added to finance.csv");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Invalid entry. Please try again.");
            }
            System.out.print("Do you want to add another entry? (yes/no): ");
            String continueAnswer = scanner.nextLine().trim().toLowerCase();
            if (!continueAnswer.equals("yes")) {
                    request_entry = false;
            }
        }

        App.compressEntry(csv_file_path, new Date()); 
        System.out.println("Exiting Big Net application.");
        scanner.close();

    }
}
