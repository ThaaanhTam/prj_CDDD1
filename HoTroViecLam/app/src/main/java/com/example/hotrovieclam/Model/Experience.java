package com.example.hotrovieclam.Model;

public class Experience {
    private String id_uid, idExperiences, name_organization, position ,time_start, time_end, description;
    public Experience() {
    }

    public Experience(String description, String time_end, String time_start, String position, String name_organization, String idExperiences, String id_uid) {
        this.description = description;
        this.time_end = time_end;
        this.time_start = time_start;
        this.position = position;
        this.name_organization = name_organization;
        this.idExperiences = idExperiences;
        this.id_uid = id_uid;
    }

    public String getId_uid() {
        return id_uid;
    }

    public void setId_uid(String id_uid) {
        this.id_uid = id_uid;
    }

    public String getIdExperiences() {
        return idExperiences;
    }

    public void setIdExperiences(String idExperiences) {
        this.idExperiences = idExperiences;
    }

    public String getName_organization() {
        return name_organization;
    }

    public void setName_organization(String name_organization) {
        this.name_organization = name_organization;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name_organization + "\n" +
                position + "\n" +
                time_start + " - " + time_end;
    }
}
