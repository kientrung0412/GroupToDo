package com.hanabi.todoapp.dao;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.adapter.MyTodoAdapter;
import com.hanabi.todoapp.models.Todo;

import java.util.Arrays;
import java.util.Date;

public class TodoDao {

    private Activity activity;
    private DataChangeListener listener;
    private CollectionReference reference = Database.getDb().collection(Todo.TODO_COLL)
            .document(Database.getFirebaseUser().getUid()).collection(Todo.TODO_COLL_MY_TODO);

    public TodoDao(Activity activity) {
        this.activity = activity;
    }

    public CollectionReference getReference() {
        return reference;
    }

    public void setListener(DataChangeListener listener) {
        this.listener = listener;
    }

    private void getTodos(MyTodoAdapter adapter, Date startDate, Date endDate, int... status) {
        reference.whereArrayContains("status", Arrays.asList(status))
                .whereLessThan("createdAt", startDate)
                .whereGreaterThan("createdAt", endDate)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        listener.getTodoSuccess(queryDocumentSnapshots);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTodo(Todo todo) {
        reference.document(todo.getId() + "")
                .set(todo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteTodo(Todo todo) {
        Todo originalTodo = new Todo();
        originalTodo.toEquals(todo);

        reference.document(todo.getId() + "")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       listener.deleteTodoSuccess(originalTodo);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void doneTodo(Todo todo) {
        todo.setStatus(Todo.TODO_STATUS_DONE);
        updateTodo(todo);
    }


    interface DataChangeListener {
        void getTodoSuccess(QuerySnapshot queryDocumentSnapshots);
        void deleteTodoSuccess(Todo todo);
    }
}
