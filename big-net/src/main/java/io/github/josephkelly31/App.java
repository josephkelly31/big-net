package io.github.josephkelly31;

import java.util.Scanner;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class App {
    public static void main(String[] args) {

        // Print greeting
        App.print_greeting();
        // Update csv with new finance data
        App.create_finance_csv("finance.csv");
        // Print farewell
        App.print_farewell();

    }

    public static void print_greeting(){
        System.out.println("Welcome to Big Net - Your Personal Finance Tracker!");
    }

    public static void print_farewell(){
        System.out.println("Thank you for using Big Net. Goodbye!");
    }

    public static void compress_entry(String file_path){
        // Placeholder for future implementation
        // Check if compressed file with today's date exists
        // If so, decompress and append new entry
        // Else, create new compressed file with entry
    }

    public static void create_finance_csv(String csv_file_path){
        
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

        System.out.println("Exiting Big Net application.");
        scanner.close();
    }
}
