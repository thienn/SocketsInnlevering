package no.westerdals;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        // We set up the scanner to receive user input
        Scanner scanner = new Scanner(System.in);
        try{
            // Set up socket & streams
            Socket socket = new Socket("localhost", 6143);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //This will wait for the server to send the string to the client saying a connection has been made
            String inputString = input.readLine();
            System.out.println(inputString);

            //Code that will run the client, this will continue to look for input from user then
            // send that info to the server
            while(true) {
                String userInput = scanner.nextLine();
                // Now we write it to the server
                output.println(userInput);
               // break;
                String response = (String) input.readLine();
                System.out.println(response);

                System.out.println("Input next subjectID or quit by typing YES: ");
                //Add code for if number YES then quit

            }

        } catch (IOException e) {
            System.out.println("Error: " + e);
        }


    }

}
