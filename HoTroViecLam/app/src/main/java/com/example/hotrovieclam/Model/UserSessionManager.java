package com.example.hotrovieclam.Model;

import com.example.hotrovieclam.Interface.UserSession;

public class UserSessionManager implements UserSession {

    // Biến static để lưu trữ UID của người dùng
    private static String userUid;

    @Override
    public String getUserUid() {
        return userUid;
    }

    @Override
    public void setUserUid(String uid) {
        userUid = uid;
    }
}
