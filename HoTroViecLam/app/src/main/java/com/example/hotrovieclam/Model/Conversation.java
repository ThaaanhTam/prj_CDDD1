package com.example.hotrovieclam.Model;

public class Conversation {
    private String receiverId; // ID của người nhận
    private String senderId;   // ID của người gửi
    private String content;     // Nội dung tin nhắn
    private Long sentAt;        // Thời gian gửi
    private boolean seen;       // Trạng thái đã xem
    private String userName;    // Tên người dùng
    private String avatar;      // Avatar của người dùng

    // Constructor
    public Conversation() {
        // Constructor rỗng để Firebase có thể sử dụng
    }

    public Conversation(String receiverId, String senderId, String content, Long sentAt, boolean seen, String userName, String avatar) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = sentAt;
        this.seen = seen;
        this.userName = userName;
        this.avatar = avatar;
    }

    // Getter và Setter
    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSentAt() {
        return sentAt;
    }

    public void setSentAt(Long sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "receiverId='" + receiverId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", content='" + content + '\'' +
                ", sentAt=" + sentAt +
                ", seen=" + seen +
                ", userName='" + userName + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
