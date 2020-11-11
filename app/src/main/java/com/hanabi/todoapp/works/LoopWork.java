package com.hanabi.todoapp.works;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hanabi.todoapp.dao.TodoDao;



public class LoopWork extends Worker {
    private TodoDao todoDao = new TodoDao();

    public LoopWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            todoDao.updeteTodoLoop();
            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }



}
