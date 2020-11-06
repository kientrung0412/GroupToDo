package com.hanabi.todoapp.works;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.dao.TodoDao;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.ManagerDate;
import com.hanabi.todoapp.utils.ManagerRemind;

import java.util.Calendar;
import java.util.Date;

public class ManagerRemindWork extends Worker implements TodoDao.DataChangeListener {

    private TodoDao todoDao;
    private ManagerDate managerDate = new ManagerDate();
    private Calendar calendar = Calendar.getInstance();
    private Date now = calendar.getTime();

    public ManagerRemindWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        todoDao = new TodoDao();
    }

    @NonNull
    @Override
    public Result doWork() {
        Todo todo = new Todo();
        todo.setContent(now.toString());
        todoDao.updateTodo(todo);
//        todoDao = new TodoDao();
//        todoDao.getTodosRedmind(manageDate.getDate(manageDate.getDateTomorrow(now)), manageDate.getDate(now));
//        todoDao.setListener(this);
        return Result.success();
    }

    @Override
    public void getTodoSuccess(int core, QuerySnapshot queryDocumentSnapshots) {
        if (!queryDocumentSnapshots.isEmpty()) {
            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                ManagerRemind.getTodos().add(snapshot.toObject(Todo.class));
            }
        }
    }

    @Override
    public void deleteTodoSuccess(Todo todo) {

    }
}
