package com.hanabi.todoapp.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hanabi.todoapp.R;
import com.hanabi.todoapp.models.Todo;

public class CreateMyToDialog implements View.OnClickListener {

    private ImageView imgAdd;
    private EditText edtContent;
    private Activity activity;
    private AlertDialog dialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public CreateMyToDialog(Activity activity) {
        this.activity = activity;
        initViews();
    }

    private void initViews() {
        View view = activity.getLayoutInflater().inflate(R.layout.form_add_my_todo, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        edtContent = view.findViewById(R.id.edt_content);
        imgAdd = view.findViewById(R.id.iv_add_my_todo);

        imgAdd.setOnClickListener(this);
        builder.setView(view);
        dialog = builder.create();
    }

    public void show() {
        dialog.show();
        edtContent.requestFocus();
    }

    private void dismiss() {
        dialog.dismiss();
        edtContent.setText("");
        edtContent.clearFocus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_my_todo:
                if (edtContent.getText().toString().isEmpty()) {
                    return;
                }
                addToDatabase();
                break;
        }

        dismiss();
    }

    public void addToDatabase() {
        Todo todo = new Todo();
        todo.setContent(edtContent.getText().toString());
        todo.setStatus(Todo.TODO_STATUS_NEW);
        db.collection(Todo.TODO_COLL).document(firebaseUser.getUid())
                .collection(Todo.TODO_COLL_MY_TODO).document(Long.toString(todo.getId()))
                .set(todo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Có lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        dismiss();

                    }
                });
    }

}
