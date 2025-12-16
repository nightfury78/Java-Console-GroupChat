package com.chat.shared;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Abstract class cannot be instantiated directly (OOP Abstraction)
public abstract class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sender;
    private String timestamp;

    public Message(String sender) {
        this.sender = sender;
        // Generate timestamp automatically upon creation
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        this.timestamp = dtf.format(LocalDateTime.now());
    }

    public String getSender() { return sender; }
    public String getTimestamp() { return timestamp; }

    // Every child class MUST implement this (OOP Polymorphism)
    public abstract String getDisplayContent();
}	