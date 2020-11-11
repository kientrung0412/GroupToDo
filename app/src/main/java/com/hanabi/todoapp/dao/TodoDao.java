package com.hanabi.todoapp.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class TodoDao {

    public static final String TAG = TodoDao.class.getName();

    public static ArrayList<Todo> todos;

    private Calendar calendar = Calendar.getInstance();
    private Context context;
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

    public void setContext(Context context) {
        this.context = context;
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
                .addOnSuccessListener(task -> {
                    if (listener != null) {
                        listener.getTodoSuccess(status, task);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void getTodosRedmind(Date startDate, Date endDate) {

        Query querySnapshotTask = reference;
        querySnapshotTask = querySnapshotTask
                .whereEqualTo("status", Todo.TODO_STATUS_NEW)
                .whereNotEqualTo("remindDate", null);

        if (startDate != null) {
            querySnapshotTask = querySnapshotTask.whereLessThan("createdAt", startDate);
        }
        if (endDate != null) {
            querySnapshotTask = querySnapshotTask.whereGreaterThan("createdAt", endDate);
        }

        querySnapshotTask
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if ()
                    }
                });
    }

    public void updeteTodoLoop() {
        reference.whereEqualTo("loop", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        return;
                    }
                    Calendar cal = Calendar.getInstance();
                    managerDate = new ManagerDate();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Todo todo = document.toObject(Todo.class);

//                        if (managerDate.isEqualDay(todo.getCompletedDate(), calendar.getTime())) {
//                            continue;
//                        }

                        Map<String, Object> map = todo.getLoopTodoMap();
                        LoopTodo loopTodo = LoopTodo.parse(map);

                        cal.setTime(todo.getCreatedAt());

                        if (loopTodo.getDays() == 1) {
                            resetTodo(todo);
                        } else if (loopTodo.getDays() > 1) {
                            if (managerDate.subtractionDays(calendar.getTime(), todo.getCreatedAt()) % loopTodo.getDays() == 0) {
                                resetTodo(todo);
                            }
                        } else if (loopTodo.getWeeks() > 0) {
                            if (managerDate.subtractionDays(calendar.getTime(), todo.getCreatedAt()) > loopTodo.getWeeks() * 7 - 7) {
                                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                                if (loopTodo.getSunday() && dayOfWeek == Calendar.SUNDAY) {
                                    resetTodo(todo);
                                } else if (loopTodo.getMonday() && dayOfWeek == Calendar.MONDAY) {
                                    resetTodo(todo);
                                } else if (loopTodo.getTuesday() && dayOfWeek == Calendar.TUESDAY) {
                                    resetTodo(todo);
                                } else if (loopTodo.getWednesday() && dayOfWeek == Calendar.WEDNESDAY) {
                                    resetTodo(todo);
                                } else if (loopTodo.getThursday() && dayOfWeek == Calendar.THURSDAY) {
                                    resetTodo(todo);
                                } else if (loopTodo.getFriday() && dayOfWeek == Calendar.FRIDAY) {
                                    resetTodo(todo);
                                } else if (loopTodo.getSaturday() && dayOfWeek == Calendar.SATURDAY) {
                                    resetTodo(todo);
                                }
                            }
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
                .addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void deleteTodo(Todo todo) {
        Todo originalTodo = new Todo();
        originalTodo.toEquals(todo);

        reference.document(todo.getId() + "")
                .delete()
                .addOnSuccessListener(task -> listener.deleteTodoSuccess(originalTodo))
                .addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
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
                        Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void realtimeUpdate(Date startDate, Date endDate, int status, int bookmark, OnRealTimeUpdate event) {
        this.realtimeUpdate(startDate, endDate, status, bookmark);
        this.realTimeUpdate = event;
    }

    public void realtimeUpdateTodo(String todoId) {
        reference.document(todoId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(context, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (realTimeUpdate != null) {
                        realTimeUpdate.todoUpdate(snapshots.toObject(Todo.class));
                    }
                });
    }

    public void realtimeRemind(Date startDate, Date endDate) {
        Query querySnapshotTask = reference;

        querySnapshotTask = querySnapshotTask
                .whereEqualTo("status", Todo.TODO_STATUS_NEW)
                .whereNotEqualTo("remindDate", null);

        if (startDate != null) {
            querySnapshotTask = querySnapshotTask.whereLessThan("createdAt", startDate);
        }
        if (endDate != null) {
            querySnapshotTask = querySnapshotTask.whereGreaterThan("createdAt", endDate);
        }
        querySnapshotTask
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, error.getMessage());
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (realTimeUpdate != null) {
                            switch (dc.getType()) {
                                case ADDED:
                                    reminderListener.add(dc.getDocument().toObject(Todo.class));
                                    break;
                                case MODIFIED:
                                    reminderListener.modified(dc.getDocument().toObject(Todo.class));
                                    break;
                                case REMOVED:
                                    reminderListener.remove(dc.getDocument().toObject(Todo.class));
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
        void getTodoSuccess(int status, QuerySnapshot queryDocumentSnapshots);

        void deleteTodoSuccess(Todo todo);

    }

    public interface RemindTodoListener {
        void add(Todo todo);

        void remove(Todo todo);

        void modified(Todo todo);
    }

    public interface OnRealTimeUpdate {
        void todoUpdate(Todo todo);

        void add(Todo todo);

        void remove(Todo todo);

        void modified(Todo todo);
    }
}
