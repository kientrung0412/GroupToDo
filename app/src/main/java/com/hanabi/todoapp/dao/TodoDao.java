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
import com.hanabi.todoapp.models.LoopTodo;
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
                    Calendar cal = Calendar.getInstance();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Todo todo = document.toObject(Todo.class);

                        if (!todo.getLoop()  ) {
                            continue;
                        }

                        if (todo.getCompletedDate() != null) {
                            managerDate.isEqualDay(todo.getCompletedDate(), calendar.getTime());
                        }

                        Map<String, Object> map = todo.getLoopTodoMap();
                        LoopTodo loopTodo = LoopTodo.parse(map);

                        cal.setTime(todo.getCreatedAt());

                        if (loopTodo.getDays() == 1) {
                            resetTodo(todo);
                        } else if (loopTodo.getDays() > 1) {
                            long diff = calendar.getTimeInMillis() - todo.getCreatedAt().getTime();
                            int day = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                            if (day % loopTodo.getDays() == 0) {
                                resetTodo(todo);
                            }
                        } else if (loopTodo.getWeeks() > 0) {
//                            long diff = calendarNow.getTimeInMillis() - todo.getCreatedAt().getTime();
//                            int day = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
//                            if (day == days) {
                            if (loopTodo.getSunday()) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                                    resetTodo(todo);
                                    return;
                                }
                            } else if (loopTodo.getMonday()) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                                    resetTodo(todo);
                                }
                            } else if (loopTodo.getTuesday()) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
                                    resetTodo(todo);
                                }
                            } else if (loopTodo.getWednesday()) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
                                    resetTodo(todo);
                                }
                            } else if (loopTodo.getThursday()) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
                                    resetTodo(todo);
                                }
                            } else if (loopTodo.getFriday()) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                                    resetTodo(todo);
                                }
                            } else if (loopTodo.getSaturday()) {
                                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                                    resetTodo(todo);
                                }
                            }
//                            }
                        } else if (loopTodo.getMonths() > 0) {
                            if (cal.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
                                resetTodo(todo);
                            }
                        } else if (loopTodo.getYears() > 0) {
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
