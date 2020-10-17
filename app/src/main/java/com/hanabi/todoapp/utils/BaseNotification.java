package com.hanabi.todoapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public abstract class BaseNotification {

    private int notificationId;
    private String channelId;
    private Context context;
    private NotificationCompat.Builder builder;

    public BaseNotification(Context context, int notificationId, String channelId) {
        this.context = context;
        this.channelId = channelId;
        this.notificationId = notificationId;
    }

    public void builderNotification(String title, String content, int smallIcon) {
        builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    public void showNotification(String title, String content, int smallIcon, CharSequence name, String description) {
        createNotificationChannel(name, description);
        builderNotification(title, content, smallIcon);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel(CharSequence name, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public NotificationCompat.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(NotificationCompat.Builder builder) {
        this.builder = builder;
    }
}
