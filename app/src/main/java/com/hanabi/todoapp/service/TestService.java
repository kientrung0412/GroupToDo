package com.hanabi.todoapp.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.hanabi.todoapp.R;
import com.hanabi.todoapp.dao.TodoDao;
import com.hanabi.todoapp.models.Todo;

import java.util.Date;
import java.util.Random;

public class TestService extends JobService {
    private TodoDao todoDao;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        doBackgroupWork();
        return false;
    }

    private void doBackgroupWork() {
        todoDao = new TodoDao();
        Todo todo = new Todo();
        todo.setStatus(Todo.TODO_STATUS_NEW);
        todo.setContent(todo.getCreatedAt().toString() + "");
        todoDao.updateTodo(todo);
        pushNotification(todo);
    }

    private void pushNotification(Todo todo) {
        String channel = "demo";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel)
                .setContentTitle(new Date().toString())
                .setSmallIcon(R.drawable.ic_add)
                .setContentText("Dm");

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channel, channel, NotificationManager.IMPORTANCE_LOW
            );
            manager.createNotificationChannel(notificationChannel);
        }

        manager.notify(1234, builder.build());
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}