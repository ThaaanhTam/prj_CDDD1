package com.example.hotrovieclam.Model;

public class ListMess {
    private String name;
    private String avatar;
    private String reicever_id;

    public String getReicever_id() {
        return reicever_id;
    }

    public void setReicever_id(String reicever_id) {
        this.reicever_id = reicever_id;
    }

    private String date;
    private String lastMes;
    private String status;

    public String getMessID() {
        return messID;
    }

    public void setMessID(String messID) {
        this.messID = messID;
    }

    private String messID;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastMes() {
        return lastMes;
    }

    public void setLastMes(String lastMes) {
        this.lastMes = lastMes;
    }
}
