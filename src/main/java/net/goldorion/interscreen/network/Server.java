package net.goldorion.interscreen.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    private Socket ownerSocket;
    private Socket clientSocket;
    private ObjectOutputStream clientWriter;
    private ObjectInputStream clientReader;
    private ObjectOutputStream ownerWriter;
    private ObjectInputStream ownerReader;

    public Server(int port) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                ownerSocket = serverSocket.accept();
                ownerWriter = new ObjectOutputStream(ownerSocket.getOutputStream());
                ownerReader = new ObjectInputStream(ownerSocket.getInputStream());

                waitForClient();
                client2Owner();
                owner2Client();

            } catch (IOException e) {
                System.out.println("The server could not start " + e.getMessage());
            }
        }, "Server").start();
    }

    private void owner2Client() {
        new Thread(() -> {
            while (!ownerSocket.isClosed()) {
                if (clientSocket != null && clientWriter != null && ownerReader != null && clientSocket.isConnected()) {
                    try {
                        clientWriter.writeObject(ownerReader.readObject());
                        clientWriter.flush();
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("An error occurred while getting an object from the owner and sending it to the client. " + e.getMessage());
                    }
                }
            }
        }, "Owner2Client thread").start();
    }

    private void client2Owner() {
        new Thread(() -> {
            while (!ownerSocket.isClosed()) {
                if (clientSocket != null && clientSocket.isConnected() && ownerWriter != null && clientReader != null) {
                    try {
                        ownerWriter.writeObject(clientReader.readObject());
                        ownerWriter.flush();
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("An error occurred while getting an object from the client and sending it to the owner. " + e.getMessage());
                    }
                }
            }
        }, "Client2Owner thread");
    }

    private void waitForClient() {
        new Thread(() -> {
            try {
                clientSocket = serverSocket.accept();
                clientWriter = new ObjectOutputStream(clientSocket.getOutputStream());
                clientReader = new ObjectInputStream(clientSocket.getInputStream());
                System.out.println("Client connected");
            } catch (IOException e) {
                System.out.println("The client was not able to connect to the server" + e.getMessage());
            }
        }, "ClientsListener").start();
    }

    public void stopServer() {
        try {
            if (clientSocket != null) {
                clientReader.close();
                clientWriter.flush();
                clientWriter.close();
                clientSocket.close();
            }

            ownerReader.close();
            ownerWriter.flush();
            ownerWriter.close();
            ownerSocket.close();

            serverSocket.close();
        } catch (IOException e) {
            System.out.println("An error happened while stopping the server " + e.getMessage());
        }

    }

}
