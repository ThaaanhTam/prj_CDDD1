package com.example.hotrovieclam.Model;

public class Notification {
    private String title;
    private String message;
    private String type; // Loại thông báo (ứng viên mới, gia hạn bài đăng, v.v.)
    private long timestamp;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Notification(String title, String message, String type, long timestamp) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }

    public Notification() {
    }
}
