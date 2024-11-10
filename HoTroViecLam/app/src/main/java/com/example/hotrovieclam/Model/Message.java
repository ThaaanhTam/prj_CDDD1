package com.example.hotrovieclam.Model;

import java.util.Objects;

public class Message {
    private String id;
    private String content;
    private String receiver_id;
    private String sender_id;
    private Long sent_at;

    // Constructor với tham số
    public Message(String content, String receiver_id, String sender_id, Long sent_at) {
        this.content = content;
        this.receiver_id = receiver_id;
        this.sender_id = sender_id;
        this.sent_at = sent_at;
    }

    // Constructor mặc định
    public Message() {
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public Long getSent_at() {
        return sent_at;
    }

    public void setSent_at(Long sent_at) {
        this.sent_at = sent_at;
    }

    // Cài đặt phương thức equals() và hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(sender_id, message.sender_id) &&
                Objects.equals(receiver_id, message.receiver_id) &&
                Objects.equals(content, message.content) &&
                Objects.equals(sent_at, message.sent_at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender_id, receiver_id, content, sent_at);
    }
}
