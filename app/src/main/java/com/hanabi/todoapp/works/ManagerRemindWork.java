package com.hanabi.todoapp.works;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hanabi.todoapp.dao.TodoDao;

public class ManagerRemindWork extends Worker {

    private TodoDao todoDao;

    public ManagerRemindWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        todoDao = new TodoDao();
        todoDao.getTodos();


        return null;
    }
}
