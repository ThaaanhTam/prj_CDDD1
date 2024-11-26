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

    private Timestamp createdAt; // created_at timestamp
    private Timestamp updatedAt;
    private Integer status; // Thêm thuộc tính status
private String idJob;

    public String getIdJob() {
        return idJob;
    }

    public void setIdJob(String idJob) {
        this.idJob = idJob;
    }

    public User() {
    }

    public User(String id, String name, String email, String phoneNumber, int userTypeId, String avatar, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userTypeId = userTypeId;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public Integer getStatus() {return status;}

    public void setStatus(Integer status) {this.status = status;}

}
