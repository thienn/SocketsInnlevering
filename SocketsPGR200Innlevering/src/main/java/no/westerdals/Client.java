package no.westerdals;

import java.io.IOException;

public class Client {
    public static void main(String[] args) {
        DBHandler program = new DBHandler();

        try {
            program.getConnection();
            System.out.println("Database Connection Success");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Drops table if it exist - for testing purposes
        program.dropTable();

        // Create table
        program.createTable();

        // Runs through method for reading from CSV File
        try {
            program.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Setup of DB and populating successful");
        // Ask for user input - Read part
        program.userInput();
    }
}
