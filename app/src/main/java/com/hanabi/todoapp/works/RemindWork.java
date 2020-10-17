package com.hanabi.todoapp.works;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hanabi.todoapp.dao.TodoDao;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.MessageNotification;

import java.util.ArrayList;

public class RemindWork extends Worker {
    private TodoDao todoDao = new TodoDao();

    public RemindWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        todoDao.remindTodo();
        Log.d(this.getClass().getName(), "doWork: " );
        return Result.retry();
    }
}
