package com.hanabi.todoapp.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.hanabi.todoapp.R;
import com.hanabi.todoapp.models.Todo;

public class CreateMyToDialog implements View.OnClickListener, DialogInterface.OnDismissListener {

    private ImageView imgAdd;
    private EditText edtContent;
    private Activity activity;
    private AlertDialog dialog;
    private InputMethodManager imm;

    private clickButtonListener listener;

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

    public void dismiss() {
        dialog.dismiss();
        edtContent.setText("");
        edtContent.clearFocus();
        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
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
                listener.onClickButtonSend(todo);
                todo = null;
                break;
        }
    }

    public void setListener(clickButtonListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        dismiss();
    }


    public interface clickButtonListener {
        void onClickButtonSend(Todo todo);
    }
}
