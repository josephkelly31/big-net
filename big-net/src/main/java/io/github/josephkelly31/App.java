package io.github.josephkelly31;

import java.util.Scanner;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class App {
    public static void main(String[] args) {

        // Update csv with new finance data
        App.add_to_finance_csv();

    }

    public static void add_to_finance_csv(){
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
                try (PrintWriter pw = new PrintWriter(new FileWriter("finance.csv", true))) {
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
