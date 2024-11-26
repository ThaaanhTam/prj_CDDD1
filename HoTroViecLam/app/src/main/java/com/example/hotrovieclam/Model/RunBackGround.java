package com.example.hotrovieclam.Model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.hotrovieclam.R;

public class RunBackGround extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Tạo và hiển thị thông báo khi dịch vụ bắt đầu
        createNotification();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotification() {
        // Kiểm tra nếu Android phiên bản 8.0 trở lên cần tạo Notification Channel
        String channelId = "default_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Foreground Service";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Tạo thông báo sử dụng NotificationCompat.Builder
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Ứng dụng đang chạy")
                .setContentText("Thông báo đang hiển thị")
                .setSmallIcon(R.drawable.check_circle)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Hiển thị thông báo dưới dạng Foreground Service
        startForeground(1, notificationBuilder.build()); // Đặt service ở trạng thái Foreground
    }
}
