package no.westerdals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientUserOld {
    private Socket client;
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    private String message;

    public ClientUserOld() {
        // Initializing network
        try {
            client = new Socket(ServerOld.HOST, ServerOld.PORT);

            if(client.isConnected()) {
                fromServer = new DataInputStream(client.getInputStream());
                toServer = new DataOutputStream(client.getOutputStream());
/*
                while(!client.isClosed()) {
                    receive();
                }
*/
                // Receive a welcome message
                System.out.println("[Server] " + receive());

                // Send a message back to server
                System.out.println("Sending message to server...");

                Scanner input = new Scanner(System.in);
                System.out.println("What subject do you want to look up?");
                message = input.nextLine();

                send(message);

                System.out.println("Message sent!");

                // Safety disconnect


            } else {
                System.err.println("Could not connect to server");
            }

        } catch (IOException e) {
            e.printStackTrace(System.err);
        }

    }

    private String receive() throws IOException {
        return fromServer.readUTF();
    }

    private void send(String str) throws IOException {
        toServer.writeUTF(str);
    }

    public static void main(String[] args) {
        new ClientUserOld();


    }

    public void userInput() {

    }
}
