package com.example.hotrovieclam.Model;

import java.sql.Timestamp;

public class User {
    // Các thuộc tính của User
    private String id;
    private String name;// varchar [primary key]
    private String email;        // varchar [unique, note: 'Email đăng ký']
    private String phoneNumber;  // phone_number varchar
    private int userTypeId;      // user_type_id integer [ref: > user_types.id]
    private String avatar;
    private String Introduction;
    private  String Adresss;// avatar varchar
    private String Birthday;
    private Timestamp createdAt; // created_at timestamp
    private Timestamp updatedAt; // updated_at timestamp

    public User() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIntroduction() {
        return Introduction;
    }

    public void setIntroduction(String introduction) {
        Introduction = introduction;
    }

    public String getAdresss() {
        return Adresss;
    }

    public void setAdresss(String adresss) {
        Adresss = adresss;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User(String id, String name, String email, String phoneNumber, int userTypeId, String avatar, String introduction, String adresss, String birthday, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userTypeId = userTypeId;
        this.avatar = avatar;
        Introduction = introduction;
        Adresss = adresss;
        Birthday = birthday;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
