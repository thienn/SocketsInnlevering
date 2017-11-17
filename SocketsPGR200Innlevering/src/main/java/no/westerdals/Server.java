package no.westerdals;

import java.io.*;
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
        String message;

        // This constructor will be passed the socket
        public ClientThread(Socket socket){
            threadSocket = socket;
        }
        // This run method is what is executed when the thread starts
        public void run(){
            //Set up the PrintWriter and BufferReader here
            try {
                DBHandler program = new DBHandler();
                program.getConnection();

                // Create the streams
                PrintWriter output = new PrintWriter(threadSocket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));

                // Tell the client that he/she has connected
               // output.println("You have connected at :" + new Date());
                output.println("What do you want to search up? (Use SubjectID): ");

                while(true) {
                    // This will wait until a line of text has been sent
                    String chatInput = input.readLine();
                    System.out.println(chatInput);

                    values = chatInput;

                    // If server get null from a specific thread (Client) it that specific thread.
                    // Ensures that the server doesn't crash and only prints null once.
                    if (values == null) {
                        Thread.currentThread().stop();
                    } else {
                        readInput(values);

                        message = readInput(values);

                      //  if(message.equals(null)){
                     //       output.println("Invalid input");
                      //  } else {
                            output.println("Your result: " + message);
                       // }

                    }


                    /*
                    // Get info sent from Client
                    String clientInput = input.nextLine();
                    */

                    /*
                    if(input.readLine() == null) {
                        threadSocket.close();
                    }
                    */
                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    // Handles the communication with the DBHandler
    public String readInput(String values) {
        String message = null;
        DBHandler program = new DBHandler();

        // Send query to DBHandler for method clientInput
        program.clientInput(values);
        message = program.clientInput(values);

        //Store into array - then return

     //   System.out.println("Test: " + message);
        return message;

    }

}
