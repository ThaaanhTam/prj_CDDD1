package com.example.hotrovieclam.Model;

public class TruongHoc {
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

    public TruongHoc() {
    }

    public TruongHoc(String nameSchool, String nganhHoc, String timeStart, String timeEnd, String deltail, Integer type) {
        this.nameSchool = nameSchool;
        this.nganhHoc = nganhHoc;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.deltail = deltail;
        this.type = type;
    }

    private String nameSchool,nganhHoc,timeStart,timeEnd,deltail;
    private Integer type;//0 và 1 bểu thị cho ti đang học va méo hc ở đâu
}
