package com.hanabi.todoapp.dao;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.models.Todo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TodoDao {

    public static ArrayList<Todo> todos = new ArrayList<>();

    private Calendar calendarNow = Calendar.getInstance();
    private Activity activity;
    private DataChangeListener listener;
    private CollectionReference reference = Database.getDb().collection(Todo.TODO_COLL)
            .document(Database.getFirebaseUser().getUid()).collection(Todo.TODO_COLL_MY_TODO);

    public TodoDao() {
    }

    public TodoDao(Activity activity) {
        this.activity = activity;
    }

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    public void getTodos(Date startDate, Date endDate, int status) {
        Query querySnapshotTask = reference.whereEqualTo("status", status);

        if (startDate != null) {
            querySnapshotTask = querySnapshotTask.whereLessThan("createdAt", startDate);
        }

        if (endDate != null) {
            querySnapshotTask = querySnapshotTask.whereGreaterThan("createdAt", endDate);
        }

        querySnapshotTask
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> listener.getTodoSuccess(status, queryDocumentSnapshots))
                .addOnFailureListener(e -> Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void updeteTodoLoop() {
        reference.whereEqualTo("loop", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        return;
                    }

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Todo todo = document.toObject(Todo.class);
                        Calendar cal = Calendar.getInstance();

                        if (todo.getCompletedDate() != null) {
                            cal.setTime(todo.getCompletedDate());
                            if (cal.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR)
                                    && cal.get(Calendar.MONTH) == calendarNow.get(Calendar.MONTH)
                                    && cal.get(Calendar.DATE) == calendarNow.get(Calendar.DATE)) {
                                return;
                            }
                        }

                        Map<String, Object> map = todo.getLoopTodoMap();
                        cal.setTime(todo.getCreatedAt());

                        int days = Integer.parseInt(map.get("days").toString());
                        int months = Integer.parseInt(map.get("months").toString());
                        int years = Integer.parseInt(map.get("years").toString());
                        boolean monday = Boolean.parseBoolean(String.valueOf(map.get("monday")));
                        boolean tuesday = Boolean.parseBoolean(String.valueOf(map.get("tuesday")));
                        boolean wednesday = Boolean.parseBoolean(String.valueOf(map.get("wednesday")));
                        boolean thursday = Boolean.parseBoolean(String.valueOf(map.get("thursday")));
                        boolean friday = Boolean.parseBoolean(String.valueOf(map.get("friday")));
                        boolean saturday = Boolean.parseBoolean(String.valueOf(map.get("saturday")));
                        boolean sunday = Boolean.parseBoolean(String.valueOf(map.get("sunday")));

                        if (sunday) {
                            if (calendarNow.get(Calendar.DAY_OF_WEEK) == 1) {
                                resetTodo(todo);
                                return;
                            }
                        }
                        if (monday) {
                            if (calendarNow.get(Calendar.DAY_OF_WEEK) == 2) {
                                resetTodo(todo);
                                return;
                            }
                        }
                        if (tuesday) {
                            if (calendarNow.get(Calendar.DAY_OF_WEEK) == 3) {
                                resetTodo(todo);
                                return;
                            }
                        }
                        if (wednesday) {
                            if (calendarNow.get(Calendar.DAY_OF_WEEK) == 4) {
                                resetTodo(todo);
                                return;
                            }
                        }
                        if (thursday) {
                            if (calendarNow.get(Calendar.DAY_OF_WEEK) == 5) {
                                resetTodo(todo);
                                return;
                            }
                        }
                        if (friday) {
                            if (calendarNow.get(Calendar.DAY_OF_WEEK) == 6) {
                                resetTodo(todo);
                                return;
                            }
                        }

                        if (saturday) {
                            if (calendarNow.get(Calendar.DAY_OF_WEEK) == 7) {
                                resetTodo(todo);
                                return;
                            }
                        }

                        if (days == 1) {
                            resetTodo(todo);
                            return;
                        }

                        if (days > 1) {
                            long diff = calendarNow.getTimeInMillis() - todo.getCreatedAt().getTime();
                            int day = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                            if (day % days == 0) {
                                resetTodo(todo);
                            }
                            return;
                        }

                        if (months > 0) {
                            if (cal.get(Calendar.DATE) == calendarNow.get(Calendar.DATE)) {
                                resetTodo(todo);
                            }
                        }

                        if (years > 0) {
                            if (cal.get(Calendar.DAY_OF_YEAR) == calendarNow.get(Calendar.DAY_OF_YEAR)) {
                                resetTodo(todo);
                            }
                        }
                    }
                });
    }

    public void updateTodo(Todo todo) {
        reference.document(todo.getId() + "")
                .set(todo)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void deleteTodo(Todo todo) {
        Todo originalTodo = new Todo();
        originalTodo.toEquals(todo);

        reference.document(todo.getId() + "")
                .delete()
                .addOnSuccessListener(aVoid -> listener.deleteTodoSuccess(originalTodo))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void realtimeUpdate() {
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(activity, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                listener.realtimeUpdateSuccess();
            }
        });
    }

    public void remindTodo() {

    }

    private void resetTodo(Todo todo) {
        todo.setStatus(Todo.TODO_STATUS_NEW);
        todo.setCreatedAt(calendarNow.getTime());
        updateTodo(todo);
    }

    public interface DataChangeListener {
        void getTodoSuccess(int core, QuerySnapshot queryDocumentSnapshots);

        void deleteTodoSuccess(Todo todo);

        void realtimeUpdateSuccess();
    }

    public interface RemindListener {

    }
}