package com.example.hotrovieclam.Model;

public class KiNang {
    private String id,uid,name,description;

    public KiNang(String id, String uid, String name, String description) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.description = description;
    }

    public KiNang() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}
