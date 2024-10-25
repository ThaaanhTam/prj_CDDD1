package com.example.hotrovieclam.Model;

public class Message {
    private String senderId;
    private String receiverId;
    private String content;
    private String sentAt;

    public Message(String senderId, String receiverId, String content, String sentAt) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public String getSentAt() {
        return sentAt;
    }
}
