package no.westerdals;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public final static String HOST = "127.0.0.1";
    public final static int PORT = 6143;

    private ServerSocket server;

    // Client Stuff
    private ArrayList<ClientSocket> connectedClients = new ArrayList<>();
    private Thread clientAcceptanceThread;

    public Server() {
        try {
            // DB related
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


            server = new ServerSocket(Server.PORT);

            // Start accepting clients
            (clientAcceptanceThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        try{
                            // Accept a new client
                            System.out.println("Listening for new clients..");

                            ClientSocket newClient = new ClientSocket(server.accept());

                            System.out.println("New client accepted");

                            connectedClients.add(newClient);

                            newClient.send("Welcome to server...");
                            System.out.println(newClient.receive());

                        } catch (IOException e) {
                            e.printStackTrace(System.err);
                        }
                    }
                }
            }, "Client acceptance thread")).start();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        new Server();
    }

    public void checkDB() {

    }

}
