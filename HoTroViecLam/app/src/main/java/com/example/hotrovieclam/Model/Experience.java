package com.example.hotrovieclam.Model;

public class Experience {

    // ID của người dùng, là mã định danh duy nhất của người dùng trong hệ thống
    private String id_uid ;

    // ID của kinh nghiệm, là mã định danh duy nhất của kinh nghiệm đó (có thể là UUID hoặc mã riêng)
    private String idExperiences ;

    // Tên tổ chức nơi người dùng đã từng làm việc
    private String name_organization;

    // Vị trí công việc mà người dùng đã đảm nhiệm tại tổ chức đó
    private String position ;

    // Thời gian bắt đầu làm việc (định dạng yyyy-MM-dd)
    private String time_start ;

    // Thời gian kết thúc làm việc (định dạng yyyy-MM-dd, hoặc "Present" nếu vẫn đang làm việc tại đây)
    private String time_end ;

    // Mô tả công việc hoặc nhiệm vụ chính trong vai trò này
    private String description ;

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
