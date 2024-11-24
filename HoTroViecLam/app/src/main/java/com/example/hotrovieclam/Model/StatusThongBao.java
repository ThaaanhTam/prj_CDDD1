package com.example.hotrovieclam.Model;

import android.content.Context;
import android.content.SharedPreferences;

public class StatusThongBao {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "StatusThongBaoPrefs";
    private static final String NOTIFICATION_SENT_KEY = "notification_sent";

    public StatusThongBao(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit(); // Khởi tạo editor
    }

    // Lưu trạng thái thông báo đã được gửi
    public void setNotificationSent(boolean isSent) {
        editor.putBoolean(NOTIFICATION_SENT_KEY, isSent);
        editor.commit(); // Lưu ngay lập tức
    }

    // Kiểm tra trạng thái thông báo đã gửi
    public boolean isNotificationSent() {
        return sharedPreferences.getBoolean(NOTIFICATION_SENT_KEY, false);
    }
}
