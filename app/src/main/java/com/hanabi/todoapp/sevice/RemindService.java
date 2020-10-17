package com.hanabi.todoapp.sevice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.hanabi.todoapp.dao.TodoDao;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.MessageNotification;

import java.util.ArrayList;

public class RemindService extends Service {
    public static final String TAG = RemindService.class.getName();

    private TodoDao todoDao;
    private MessageNotification notification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        todoDao = new TodoDao();
//        todoDao.remindTodo();
//        notification = new MessageNotification(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "adhgahdg");
            }
        });
        thread.start();
//        todoDao.updateRemindTodo();
//        Log.d(this.getClass().getName(), "onStartCommand: " + todoDao.getTodos().size());
        return START_STICKY;
    }


}
