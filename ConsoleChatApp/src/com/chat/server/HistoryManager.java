package com.chat.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final String FILE_PATH = "chat_history.txt";

    // Method to append a message to the text file
    public static void saveMessage(String formattedMessage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(formattedMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Could not save history: " + e.getMessage());
        }
    }

    // Method to read all messages (for new users who join)
    public static List<String> getChatHistory() {
        List<String> history = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) return history;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                history.add(line);
            }
        } catch (IOException e) {
            System.err.println("Could not read history: " + e.getMessage());
        }
        return history;
    }
}	