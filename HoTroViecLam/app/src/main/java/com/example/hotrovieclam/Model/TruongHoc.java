package com.example.hotrovieclam.Model;

public class TruongHoc {
    private String id_Shool,uid_Users,nameSchool, nganhHoc, timeStart, timeEnd, deltail;
    private Integer type;//0 và 1 bểu thị cho ti đang học va méo hc ở đâu

    public TruongHoc() {
    }

    public TruongHoc(String id_Shool, String uid_Users, String nameSchool, String nganhHoc, String timeStart, String timeEnd, String deltail, Integer type) {
        this.id_Shool = id_Shool;
        this.uid_Users = uid_Users;
        this.nameSchool = nameSchool;
        this.nganhHoc = nganhHoc;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.deltail = deltail;
        this.type = type;
    }

    public String getId_Shool() {
        return id_Shool;
    }

    public void setId_Shool(String id_Shool) {
        this.id_Shool = id_Shool;
    }

    public String getUid_Users() {
        return uid_Users;
    }

    public void setUid_Users(String uid_Users) {
        this.uid_Users = uid_Users;
    }

    public String getNameSchool() {
        return nameSchool;
    }

    public void setNameSchool(String nameSchool) {
        this.nameSchool = nameSchool;
    }

    public String getNganhHoc() {
        return nganhHoc;
    }

    public void setNganhHoc(String nganhHoc) {
        this.nganhHoc = nganhHoc;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getDeltail() {
        return deltail;
    }

    public void setDeltail(String deltail) {
        this.deltail = deltail;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return nameSchool + "\n" + nganhHoc + "\n" + timeEnd + "-" +timeStart ;
    }

}
