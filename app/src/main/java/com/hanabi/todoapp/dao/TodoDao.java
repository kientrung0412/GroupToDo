package com.hanabi.todoapp.dao;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.models.Todo;

import java.util.Date;

public class TodoDao {

    private Activity activity;
    private DataChangeListener listener;
    private CollectionReference reference = Database.getDb().collection(Todo.TODO_COLL)
            .document(Database.getFirebaseUser().getUid()).collection(Todo.TODO_COLL_MY_TODO);

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

    public void doneTodo(Todo todo) {
        todo.setStatus(Todo.TODO_STATUS_DONE);
        updateTodo(todo);
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

    public interface DataChangeListener {
        void getTodoSuccess(int core, QuerySnapshot queryDocumentSnapshots);

        void deleteTodoSuccess(Todo todo);

        void realtimeUpdateSuccess();

    }
}
