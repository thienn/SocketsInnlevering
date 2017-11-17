package no.westerdals;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientUser {
    public static void main(String[] args) {
        new ClientUser();
    }

    public ClientUser() {
        // We set up the scanner to receive user input
        Scanner scanner = new Scanner(System.in);
        try{
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
                break;
            }
            //Get response from server
            String response;
            while ((response = input.readLine()) != null)
            {
                System.out.println( response );

            }

            /*
            //Get response from server
            String response;
            while ((response = input.readLine()) != null)
            {
                System.out.println( response );
            }
            */

        } catch (IOException e) {
            System.out.println("Error: " + e);
        }


    }

}
