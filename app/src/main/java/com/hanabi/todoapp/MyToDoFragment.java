package com.hanabi.todoapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.adapter.MyTodoAdapter;
import com.hanabi.todoapp.models.Todo;

import java.util.ArrayList;

public class MyToDoFragment extends Fragment {

    private RecyclerView rcvMyTodos;
    private MyTodoAdapter adapter;
    private Button btnAdd;
    private EditText edtContent;
    private Boolean status;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_to_do, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rcvMyTodos = getActivity().findViewById(R.id.rcv_my_todo);

        adapter = new MyTodoAdapter(getLayoutInflater());
        rcvMyTodos.setAdapter(adapter);
        loadingData();
        updateData();
    }

    private void updateData() {
        db.collection(Todo.TODO_COLL).document(user.getUid())
                .collection(Todo.TODO_COLL_MY_TODO)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getActivity(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!value.isEmpty() && value != null) {
                            loadingData();
                        }
                    }
                });
    }

    private void loadingData() {
        final ArrayList<Todo> todos = new ArrayList<>();
        db.collection(Todo.TODO_COLL).document(user.getUid())
                .collection(Todo.TODO_COLL_MY_TODO)
                .whereEqualTo("status", Todo.TODO_STATUS_NEW)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Todo todo = document.toObject(Todo.class);
                            todos.add(todo);
                            adapter.setData(todos);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Lỗi:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}