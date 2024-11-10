package com.example.hotrovieclam.Model;

public class Profile {
    String birthday, address;
    int gioitinh;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(int gioitinh) {
        this.gioitinh = gioitinh;
    }

    public Profile(String birthday, String address, int gioitinh) {
        this.birthday = birthday;
        this.address = address;
        this.gioitinh = gioitinh;
    }

    public Profile() {

    }
}
