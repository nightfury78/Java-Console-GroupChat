package com.chat.server;

import com.chat.shared.TextMessage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    // List to keep track of all connected clients for broadcasting
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    
    private Socket socket;
    private ObjectOutputStream objectWriter;
    private ObjectInputStream objectReader;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            // Object streams allow us to send our TextMessage objects directly
            this.objectWriter = new ObjectOutputStream(socket.getOutputStream());
            this.objectReader = new ObjectInputStream(socket.getInputStream());

            // 1. READ USERNAME: The client sends their name immediately upon connection
            Object input = objectReader.readObject();
            if (input instanceof TextMessage) {
                this.clientUsername = ((TextMessage) input).getContent();
                clientHandlers.add(this);

                // 2. DB LOG: Record the Join event in MS SQL
                DatabaseManager.logUserJoin(clientUsername);

                // 3. HISTORY: Read chat_history.txt and send it ONLY to this new user
                List<String> history = HistoryManager.getChatHistory();
                for (String oldLine : history) {
                    // We send history lines as "SERVER" messages or a special type
                    objectWriter.writeObject(new TextMessage("HISTORY", oldLine));
                }
                objectWriter.flush();

                // 4. NOTIFICATION: Tell everyone else that a new user joined
                broadcastMessage(new TextMessage("SERVER", clientUsername + " has joined the chat!"));
            }
        } catch (Exception e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        TextMessage messageFromClient;
        while (socket.isConnected()) {
            try {
                // Listen for new messages from this specific client
                messageFromClient = (TextMessage) objectReader.readObject();
                
                // 5. FILE LOG: Save the message to our text file layout
                HistoryManager.saveMessage(messageFromClient.getDisplayContent());
                
                // 6. BROADCAST: Send the message to every other connected user
                broadcastMessage(messageFromClient);
                
            } catch (Exception e) {
                closeEverything();
                break;
            }
        }
    }

    public void broadcastMessage(TextMessage message) {
        for (ClientHandler handler : clientHandlers) {
            try {
                // Don't send the message back to the person who sent it
                if (!handler.clientUsername.equals(clientUsername) || message.getSender().equals("SERVER")) {
                    handler.objectWriter.writeObject(message);
                    handler.objectWriter.flush();
                }
            } catch (IOException e) {
                handler.closeEverything();
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        // 7. DB LOG: Record the Leave event in MS SQL
        if (clientUsername != null) {
            DatabaseManager.logUserLeave(clientUsername);
            broadcastMessage(new TextMessage("SERVER", clientUsername + " has left the chat."));
        }
    }

    public void closeEverything() {
        removeClientHandler();
        try {
            if (objectReader != null) objectReader.close();
            if (objectWriter != null) objectWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}