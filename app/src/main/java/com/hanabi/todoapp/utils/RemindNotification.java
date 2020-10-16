package com.hanabi.todoapp.utils;

import android.app.Activity;

import com.hanabi.todoapp.R;

public class RemindNotification extends BaseNotification {

    public static final int NOTIFICATION_ID = 2;
    public static final String CHANNEL_ID = "CHANNEL_REMIND";
    private String name = "Nhắn nhở";
    private String description = "Thông báo nhắn nhở công việc";
    private int smallIcon = R.drawable.ic_clock;


    public RemindNotification(Activity activity) {
        super(activity, NOTIFICATION_ID, CHANNEL_ID);
    }

    public void showNotification(String title, String content) {
        super.showNotification(title, content, smallIcon, description, name);
    }
}
