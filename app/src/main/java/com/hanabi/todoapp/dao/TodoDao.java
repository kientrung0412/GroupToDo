package com.hanabi.todoapp.dao;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.ManagerDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TodoDao {

    public static final String TAG = TodoDao.class.getName();

    public static ArrayList<Todo> todos;

    private Calendar calendar = Calendar.getInstance();
    private Activity activity;
    private DataChangeListener listener;
    private RemindTodoListener reminderListener;
    private CollectionReference reference;
    private FirebaseUser firebaseUser;
    private OnRealTimeUpdate realTimeUpdate;
    private ManagerDate managerDate;

    public TodoDao() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = Database.getDb().collection(Todo.TODO_COLL)
                .document(firebaseUser.getUid()).collection(Todo.TODO_COLL_MY_TODO);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public static ArrayList<Todo> getTodos() {
        return todos;
    }

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    public void setReminderListener(RemindTodoListener reminderListener) {
        this.reminderListener = reminderListener;
    }

    public void setRealTimeUpdate(OnRealTimeUpdate realTimeUpdate) {
        this.realTimeUpdate = realTimeUpdate;
    }

    public void getTodos(Date startDate, Date endDate, int status, int bookmark) {

        Query querySnapshotTask = reference;

        if (status == Todo.TODO_STATUS_DONE || status == Todo.TODO_STATUS_NEW) {
            querySnapshotTask = reference.whereEqualTo("status", status);
        }

        if (startDate != null) {
            querySnapshotTask = querySnapshotTask.whereLessThan("createdAt", startDate);
        }

        if (endDate != null) {
            querySnapshotTask = querySnapshotTask.whereGreaterThan("createdAt", endDate);
        }

        if (bookmark == Todo.BOOKMARK_TRUE) {
            querySnapshotTask = querySnapshotTask.whereEqualTo("bookmark", true);
        } else if (bookmark == Todo.BOOKMARK_FALSE) {
            querySnapshotTask = querySnapshotTask.whereEqualTo("bookmark", false);
        }

        querySnapshotTask
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(task -> listener.getTodoSuccess(status, task))
                .addOnFailureListener(e -> Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void getTodosRedmind(Date startDate, Date endDate) {

        Query querySnapshotTask = reference;

        querySnapshotTask = querySnapshotTask.whereEqualTo("status", Todo.TODO_STATUS_NEW);

        if (startDate != null) {
            querySnapshotTask = querySnapshotTask.whereLessThan("createdAt", startDate);
        }
        if (endDate != null) {
            querySnapshotTask = querySnapshotTask.whereGreaterThan("createdAt", endDate);
        }

        querySnapshotTask.whereNotEqualTo("remindDate", null);

        querySnapshotTask
                .get()
                .addOnSuccessListener(task -> listener.getTodoSuccess(Todo.TODO_STATUS_NEW, task))
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

//                        if (todo.getCompletedDate() != null) {
//                            managerDate.isEqualDay(todo.getCompletedDate(), calendar.getTime());
//                            return;
//                        }

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

                        if (days > 1 && days % 7 != 0) {
                            long diff = calendar.getTimeInMillis() - todo.getCreatedAt().getTime();
                            int day = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                            if (day % days == 0) {
                                resetTodo(todo);
                            }
                            return;
                        }

                        if (days % 7 == 0) {
//                            long diff = calendarNow.getTimeInMillis() - todo.getCreatedAt().getTime();
//                            int day = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
//                            if (day == days) {
                            if (sunday) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                                    resetTodo(todo);
                                    return;
                                }
                            }
                            if (monday) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                                    resetTodo(todo);
                                    return;
                                }
                            }
                            if (tuesday) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
                                    resetTodo(todo);
                                    return;
                                }
                            }
                            if (wednesday) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
                                    resetTodo(todo);
                                    return;
                                }
                            }
                            if (thursday) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
                                    resetTodo(todo);
                                    return;
                                }
                            }
                            if (friday) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                                    resetTodo(todo);
                                    return;
                                }
                            }
                            if (saturday) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                                    resetTodo(todo);
                                    return;
                                }
                            }
//                            }
                        }
                        if (days == 1) {
                            resetTodo(todo);
                            return;
                        }
                        if (months > 0) {
                            if (cal.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
                                resetTodo(todo);
                            }
                        }

                        if (years > 0) {
                            if (cal.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
                                resetTodo(todo);
                            }
                        }
                    }
                });
    }

    public void updateTodo(Todo todo) {
        reference.document(todo.getId() + "")
                .set(todo)
                .addOnFailureListener(e -> Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void deleteTodo(Todo todo) {
        Todo originalTodo = new Todo();
        originalTodo.toEquals(todo);

        reference.document(todo.getId() + "")
                .delete()
                .addOnSuccessListener(task -> listener.deleteTodoSuccess(originalTodo))
                .addOnFailureListener(e -> Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void realtimeUpdate(Date startDate, Date endDate, int status, int bookmark) {
        Query querySnapshotTask = reference;

        if (status == Todo.TODO_STATUS_DONE || status == Todo.TODO_STATUS_NEW) {
            querySnapshotTask = querySnapshotTask.whereEqualTo("status", status);
        }

        if (startDate != null) {
            querySnapshotTask = querySnapshotTask.whereLessThan("createdAt", startDate);
        }

        if (endDate != null) {
            querySnapshotTask = querySnapshotTask.whereGreaterThan("createdAt", endDate);
        }

        if (bookmark == Todo.BOOKMARK_TRUE) {
            querySnapshotTask = querySnapshotTask.whereEqualTo("bookmark", true);
        } else if (bookmark == Todo.BOOKMARK_FALSE) {
            querySnapshotTask = querySnapshotTask.whereEqualTo("bookmark", false);
        }

        querySnapshotTask
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(activity, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                realTimeUpdate.add(dc.getDocument().toObject(Todo.class));
                                break;
                            case MODIFIED:
                                realTimeUpdate.modified(dc.getDocument().toObject(Todo.class));
                                break;
                            case REMOVED:
                                realTimeUpdate.remove(dc.getDocument().toObject(Todo.class));
                                break;
                        }
                    }

                });
    }

    public void realtimeUpdateTodo(String todoId) {
        reference.document(todoId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(activity, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (realTimeUpdate != null) {
                        realTimeUpdate.todoUpdate(snapshots.toObject(Todo.class));
                    }
                });
    }

    public void remindTodo() {
        reference
                .whereNotEqualTo("remindDate", null)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        return;
                    }
                    todos = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Todo todo = document.toObject(Todo.class);
                        todos.add(todo);
                    }
                    return;
                })
                .addOnFailureListener(e -> Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void updateRemindTodo() {
        reference.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Toast.makeText(activity, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                if (realTimeUpdate != null) {
                    switch (dc.getType()) {
                        case ADDED:
                            realTimeUpdate.add(dc.getDocument().toObject(Todo.class));
                            break;
                        case MODIFIED:
                            realTimeUpdate.modified(dc.getDocument().toObject(Todo.class));
                            break;
                        case REMOVED:
                            realTimeUpdate.remove(dc.getDocument().toObject(Todo.class));
                            break;
                    }
                }
            }
        });
    }

    private void resetTodo(Todo todo) {
        todo.setStatus(Todo.TODO_STATUS_NEW);
        todo.setCreatedAt(calendar.getTime());
        updateTodo(todo);
    }


    public interface DataChangeListener {
        void getTodoSuccess(int core, QuerySnapshot queryDocumentSnapshots);

        void deleteTodoSuccess(Todo todo);

    }

    public interface RemindTodoListener {
        void getTodoSuccess(ArrayList<Todo> todos);
    }

    public interface OnRealTimeUpdate {
        void todoUpdate(Todo todo);

        void add(Todo todo);

        void remove(Todo todo);

        void modified(Todo todo);
    }
}
