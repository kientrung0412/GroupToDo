package com.hanabi.todoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

public class LoopTodoService extends Service {

    private PowerManager.WakeLock wakeLock = null;
    private Boolean isServiceStarted = false;

    public LoopTodoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}