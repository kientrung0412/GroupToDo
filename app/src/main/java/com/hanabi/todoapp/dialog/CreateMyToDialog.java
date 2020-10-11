package com.hanabi.todoapp.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

import com.hanabi.todoapp.R;
import com.hanabi.todoapp.models.Todo;

public class CreateMyToDialog implements View.OnClickListener, DialogInterface.OnDismissListener, PopupMenu.OnMenuItemClickListener {

    private ImageView imgAdd, imgSetTime, imgSet;
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
        imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_add_my_todo, null);
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
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
                dialog.dismiss();
                todo = null;
                break;
            default:
                showPopupMenu(view);
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


    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(activity, view);
        switch (view.getId()) {
            case R.id.cp_set_loop_todo:
                popupMenu.inflate(R.menu.menu_loop_todo);
                break;
        }
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_todo_day:
                Toast.makeText(activity, "day", Toast.LENGTH_SHORT).show();
                break;
            case R.id.it_todo_week:
                Toast.makeText(activity, "week", Toast.LENGTH_SHORT).show();
                break;
            case R.id.it_todo_moth:
                Toast.makeText(activity, "it_to_moth", Toast.LENGTH_SHORT).show();
                break;
            case R.id.it_todo_year:
                Toast.makeText(activity, "week", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public interface clickButtonListener {
        void onClickButtonSend(Todo todo);
    }
}
