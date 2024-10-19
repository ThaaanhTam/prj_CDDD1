package com.example.tvl;

public class Message {
    private String content;
    private boolean fromApplicant;

    public Message(String content, boolean fromApplicant) {
        this.content = content;
        this.fromApplicant = fromApplicant;
    }

    public String getContent() {
        return content;
    }

    public boolean isFromApplicant() {
        return fromApplicant;
    }
}

