package com.hanabi.todoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.MainActivity;
import com.hanabi.todoapp.dao.TodoDao;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.ManagerDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TodoService extends Service implements TodoDao.OnRealTimeUpdate {

    private TodoDao todoDao;

    private Date now = Calendar.getInstance().getTime();
    private ManagerDate managerDate;


    @Override
    public void onCreate() {
        super.onCreate();
        managerDate = new ManagerDate();
        todoDao = new TodoDao();
        todoDao.realtimeUpdate(null, null, Todo.TODO_STATUS_NEW, Todo.BOOKMARK_NONE);
        todoDao.realtimeUpdate(null, null, Todo.TODO_STATUS_DONE, Todo.BOOKMARK_NONE);
        todoDao.setRealTimeUpdate(this);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new TodoBinder(this);
    }


    @Override
    public void todoUpdate(Todo todo) {

    }

    @Override
    public void add(Todo todo) {
        MainActivity.getAllTodoFragment().add(todo);
        if (managerDate.isEqualDay(todo.getCreatedAt(), now)) {
            MainActivity.getToDoFragment().add(todo);
        }
    }

    @Override
    public void remove(Todo todo) {
        MainActivity.getAllTodoFragment().remove(todo);
        MainActivity.getToDoFragment().remove(todo);
    }

    @Override
    public void modified(Todo todo) {
        MainActivity.getToDoFragment().modified(todo);
        MainActivity.getAllTodoFragment().modified(todo);
    }

    public class TodoBinder extends Binder {
        private TodoService service;

        public TodoBinder(TodoService service) {
            this.service = service;
        }

        public TodoService getService() {
            return service;
        }
    }
}