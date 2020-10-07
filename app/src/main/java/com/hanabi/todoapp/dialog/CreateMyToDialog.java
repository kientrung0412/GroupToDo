package com.hanabi.todoapp.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hanabi.todoapp.R;
import com.hanabi.todoapp.models.Todo;

public class CreateMyToDialog implements View.OnClickListener, DialogInterface.OnDismissListener {

    private ImageView imgAdd;
    private EditText edtContent;
    private Activity activity;
    private AlertDialog dialog;
    private InputMethodManager imm;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private Todo todo = null;

    public CreateMyToDialog(Activity activity) {
        this.activity = activity;
        initViews();
    }

    private void initViews() {
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = activity.getLayoutInflater().inflate(R.layout.form_add_my_todo, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        edtContent = view.findViewById(R.id.edt_content);
        imgAdd = view.findViewById(R.id.iv_add_my_todo);

        imgAdd.setOnClickListener(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.setOnDismissListener(this);
    }

    public void updateTodoDialog(Todo todo) {
        edtContent.setText(todo.getContent());
        this.todo = todo;
        show();
    }

    public void show() {
        dialog.show();
        edtContent.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void dismiss() {
        dialog.dismiss();
        edtContent.setText("");
        edtContent.clearFocus();
        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_HIDDEN, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_my_todo:
                if (edtContent.getText().toString().isEmpty()) {
                    return;
                }
                if (todo == null) {
                    this.todo = new Todo();
                }
                todo.setContent(edtContent.getText().toString());
                updateDatabase();
                break;
        }

        dismiss();
    }

    public void updateDatabase() {
        todo.setStatus(Todo.TODO_STATUS_NEW);
        db.collection(Todo.TODO_COLL).document(firebaseUser.getUid())
                .collection(Todo.TODO_COLL_MY_TODO).document(Long.toString(todo.getId()))
                .set(todo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Có lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        dismiss();

                    }
                });
        todo = null;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        dismiss();
    }
}
