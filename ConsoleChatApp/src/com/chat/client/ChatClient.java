package com.chat.client;

import com.chat.shared.TextMessage;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private ObjectOutputStream objectWriter;
    private ObjectInputStream objectReader;
    private String username;

    public ChatClient(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            // Initialize streams - Writer first to avoid deadlock
            this.objectWriter = new ObjectOutputStream(socket.getOutputStream());
            this.objectReader = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void sendMessage() {
        try {
            // First: Send the username as a TextMessage so the server recognizes us
            objectWriter.writeObject(new TextMessage(username, username));
            objectWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                // Send the actual chat message
                TextMessage textMessage = new TextMessage(username, messageToSend);
                objectWriter.writeObject(textMessage);
                objectWriter.flush();
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void listenForMessage() {
        // Run the listener in a background thread so it doesn't block the UI/Scanner
        new Thread(new Runnable() {
            @Override
            public void run() {
                TextMessage msgFromChat;
                while (socket.isConnected()) {
                    try {
                        msgFromChat = (TextMessage) objectReader.readObject();
                        // This will print "Saim : Hello!" or "SERVER : Ahmed joined"
                        System.out.println(msgFromChat.getDisplayContent());
                    } catch (Exception e) {
                        closeEverything();
                        break;
                    }
                }
            }
        }).start();
    }

    public void closeEverything() {
        try {
            if (objectReader != null) objectReader.close();
            if (objectWriter != null) objectWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username for the group chat: ");
        String username = scanner.nextLine();
        
        // Connect to the server running on your local machine (localhost)
        Socket socket = new Socket("127.0.0.1", 1234);
        ChatClient client = new ChatClient(socket, username);
        
        // Start the separate listening thread
        client.listenForMessage();
        // Start the main loop for sending messages
        client.sendMessage();
    }
}