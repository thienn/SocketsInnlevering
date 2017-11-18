package no.westerdals;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Server class that is "always" up and wait for clients to connect to it. Multi threading to accept multiple clients
 * at the same time.
 *
 * Connects to Database through DBHandler only when getting a reply from userInput
 * Then wait for answer from DBHandler and return the String back to Client when possible
 *
 * References for structure on Server taken from
 * https://hubpages.com/technology/How-to-create-a-server-in-Java
 * Reference for multi threading
 * https://hubpages.com/technology/How-to-build-a-server-in-Java-Part-3-Allowing-multiple-users-to-connect
 *
 * @author Thien Cong Pham
 */

public class Server {
    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        try {
            DBHandler program = new DBHandler();
            program.getConnection();
            program.dropTable();
            program.createTable();
            // Runs through method for reading from CSV File
            try {
                program.readFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Setup of DB and populating successful");

            // Socket setup
            ServerSocket sSocket = new ServerSocket(6143);
            System.out.println("Server started at: " + new Date());

            //Loop that runs server functions
            while(true) {
                Socket socket = sSocket.accept();

                ClientThread cT = new ClientThread(socket);

                new Thread(cT).start();

            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

    }

    /**
     * Creates a new thread for every client that connects so they can be handled separately while the server
     * still runs without interruptions
     * Handles the input and output to client. When it receives a input from client it passes along to readInput then
     * when it get back a message it passes that one along to client. Repeated until client decide to close connection
     */
    class ClientThread implements Runnable {

        Socket threadSocket;
        String values;
        String message;

        // This constructor will be passed to the socket
        public ClientThread(Socket socket){
            threadSocket = socket;
        }

        // This run method is what is executed when the thread starts - new for every client
        public void run(){
            try {
                DBHandler program = new DBHandler();
                program.getConnection();

                // Create the streams
                PrintWriter output = new PrintWriter(threadSocket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));

                // Respond to client that the connection is successful and then ask about subject
                output.println("Connected to server - What do you want to search up? (Use SubjectID): ");

                while(true) {
                    // This will wait until a line of text has been sent
                    String chatInput = input.readLine();
                    System.out.println(chatInput);

                    values = chatInput;

                    // If server get null from a specific thread (Client) it closes that specific thread.
                    // Ensures that the server doesn't crash and only prints null once.
                    if (values == null) {
                        Thread.currentThread().stop();
                    } else {
                        readInput(values);

                        message = readInput(values);
                        // Should wrap into something in case of invalid input to report back to user to type a valid one
                        // At the moment it only gives null back and continues
                        output.println("Your result: " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    // Handles the communication with the DBHandler

    /**
     * Get input from ClientThread() pass that along to DBHandler calling upon the method clientInput to request information
     * Waits for answer then passes that along back to ClientThread() run
     *
     * @param values Get a user input from ClientThread()
     * @return message received from DBHandler
     */
    public String readInput(String values) {
        String message;
        DBHandler program = new DBHandler();

        // Send query to DBHandler for method clientInput
        program.clientInput(values);
        message = program.clientInput(values);

        //return to run()
        return message;
    }

}
