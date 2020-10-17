package com.hanabi.todoapp.works;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hanabi.todoapp.dao.TodoDao;

import java.util.concurrent.TimeUnit;

public class LoopWork extends Worker {
    private TodoDao todoDao = new TodoDao();

    public LoopWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        todoDao.updeteTodoLoop();
        return Result.success();
    }
}
