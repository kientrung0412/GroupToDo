package com.hanabi.todoapp.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.hanabi.todoapp.MainActivity;
import com.hanabi.todoapp.R;

public class MessageNotification extends BaseNotification {

    public static final int NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "CHANNEL_MESSAGE";
    private String name = "Tin nhắn";
    private String description = "Thông báo tin nhắn đến";
    private int smallIcon = R.drawable.ic_chat;
    private Context context;

    public MessageNotification(Context context) {
        super(context, NOTIFICATION_ID, CHANNEL_ID);
        this.context = context;
    }

    @Override
    public void builderNotification(String title, String content, int smallIcon) {
        super.builderNotification(title, content, smallIcon);
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("name", "trung");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, MainActivity.REQUEST_CODE_OFF, intent, Intent.FILL_IN_ACTION);
        getBuilder().addAction(R.drawable.ic_calendar, "Gia hạn", pendingIntent);

    }

    public void showNotification(String title, String content) {
        super.showNotification(title, content, smallIcon, description, name);
    }
}
