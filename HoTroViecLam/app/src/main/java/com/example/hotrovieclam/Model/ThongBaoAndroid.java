package com.example.hotrovieclam.Model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.hotrovieclam.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ThongBaoAndroid extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Kiểm tra xem có thông báo hay không
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            sendNotification(this,title, body);
        }
    }

    // Đổi phương thức sendNotification thành static
    public static void sendNotification(Context context, String title, String messageBody) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "default_channel";

        // Kiểm tra nếu Android phiên bản 8.0 trở lên cần một Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Thông báo thay đổi dữ liệu";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Tạo thông báo
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setSmallIcon(R.drawable.check_circle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        // Hiển thị thông báo
        if (notificationManager != null) {
            notificationManager.notify(0, notification);
        }
    }
}
