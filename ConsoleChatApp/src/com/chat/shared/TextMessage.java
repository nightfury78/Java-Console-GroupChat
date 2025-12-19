package com.chat.shared;

// TextMessage 'IS-A' Message (OOP Inheritance)
public class TextMessage extends Message {
    private String content;

    public TextMessage(String sender, String content) {
        super(sender); // Calls the constructor of the Message class
        this.content = content;
    }

    public String getContent() { return content; }

    @Override
    public String getDisplayContent() {
        return getSender() + " : " + content;
    }
}