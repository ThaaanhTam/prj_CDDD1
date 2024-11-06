package com.example.hotrovieclam.Model;

public class Message {
  private String id;
  private String content;
  private String receiver_id;
  private String sender_id;


    public Message(String content, String receiver_id, String sender_id, String sent_at) {
        this.content = content;
        this.receiver_id = receiver_id;
        this.sender_id = sender_id;
        //this.sent_at = sent_at;
    }

    public Message() {
    }

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


}
