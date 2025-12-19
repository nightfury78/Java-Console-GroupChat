package com.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private ServerSocket serverSocket;

    public ChatServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            System.out.println("SERVER: Started... Waiting for clients on port 1234");
            
            while (!serverSocket.isClosed()) {
                // The server pauses here until someone tries to connect
                Socket socket = serverSocket.accept();
                System.out.println("SERVER: A new client has connected!");
                
                // Hand the new connection to a ClientHandler
                ClientHandler clientHandler = new ClientHandler(socket);
                
                // Run the handler in its own thread so other users can still connect
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // We tell the server to listen on port 1234
        ServerSocket serverSocket = new ServerSocket(1234);
        ChatServer server = new ChatServer(serverSocket);
        server.startServer();
    }
}