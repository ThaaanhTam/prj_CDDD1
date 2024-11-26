package com.example.hotrovieclam.Model;

import com.example.hotrovieclam.Interface.UserSession;

public class UserSessionManager implements UserSession {

    // Biến static để lưu trữ UID của người dùng
    private static String userUid;
    private static String name;
    private static String phoneNumber;
    private static String email;
    private static int a =0;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        UserSessionManager.name = name;
    }

    public static String getPhoneNumber() {
        return phoneNumber;
    }

    public static void setPhoneNumber(String phoneNumber) {
        UserSessionManager.phoneNumber = phoneNumber;
    }

    public static String getEmail() {
        return email;
    }
    public static int geta() {
        return a;
    }


    public static void setEmail(String email) {
        UserSessionManager.email = email;
    }

    @Override
    public String getUserUid() {
        return userUid;
    }

    @Override
    public void setUserUid(String uid) {
        userUid = uid;
        a++;
    }
}
