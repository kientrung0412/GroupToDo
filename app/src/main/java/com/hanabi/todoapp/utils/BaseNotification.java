package com.hanabi.todoapp.utils;

import android.app.Activity;

public abstract class BaseNotification {

    private Activity activity;

    public BaseNotification(Activity activity) {
        this.activity = activity;
    }
    public void showNotification(String title, String content){

    }
    private void createNotificationChannel() {

    }
}
