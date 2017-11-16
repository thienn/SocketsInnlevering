package no.westerdals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

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
            // Ask for user input - Read part
            // program.userInput();

            ServerSocket sSocket = new ServerSocket(6143);
            // Fjern senere
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

    // We need to use "Implements runnable to tell java that this is a thread
    class ClientThread implements Runnable {

        Socket threadSocket;
        String values;

        // This constructor will be passed the socket
        public ClientThread(Socket socket){
            threadSocket = socket;
        }
        // This run method is what is executed when the thread starts
        public void run(){
            //Set up the PrintWriter and BufferReader here
            try {
                // Create the streams
                PrintWriter output = new PrintWriter(threadSocket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));

                // Tell the client that he/she has connected
                output.println("You have connected at :" + new Date());

                while(true) {
                    // This will wait until a line of text has been sent
                    String chatInput = input.readLine();
                    System.out.println(chatInput);

                    values = chatInput;
                    readInput(values);

                    /*
                    // Get info sent from Client
                    String clientInput = input.nextLine();
                    */
                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public void readInput(String values) {
        // try call on DB
        DBHandler program = new DBHandler();
        program.userInputBasic(values);
    }
}
