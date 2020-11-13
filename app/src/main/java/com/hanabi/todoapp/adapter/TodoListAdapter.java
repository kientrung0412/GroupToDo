package com.hanabi.todoapp.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanabi.todoapp.R;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.ManagerDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TodoListAdapter extends BaseAdapter {

    private ArrayList<Todo> data;
    private Context context;
    private OnClickMyTodoListener listener;

    public TodoListAdapter(ArrayList<Todo> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setListener(OnClickMyTodoListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View itemView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (itemView == null) {
            holder = new ViewHolder();
            itemView = LayoutInflater.from(context).inflate(R.layout.todo_widget, null);
            holder.tvContent = itemView.findViewById(R.id.edt_content_todo);
            holder.ivLoop = itemView.findViewById(R.id.iv_loop);
            holder.ivRemind = itemView.findViewById(R.id.iv_remind);
            holder.cbStar = itemView.findViewById(R.id.cb_bookmark);
            holder.tvTime = itemView.findViewById(R.id.tv_todo_create_at);
            holder.cbDone = itemView.findViewById(R.id.cb_done);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        Todo todo = data.get(i);
        holder.bindView(todo);
        if (listener != null) {
            holder.cbStar.setOnClickListener(view -> listener.onCheckBookmark(todo));
            holder.cbDone.setOnClickListener(view -> listener.onChangeCheckbox(todo));
            itemView.setOnClickListener(view -> listener.onClickMyTodo(todo));
        }

        return itemView;
    }


    static class ViewHolder {
        private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        private ManagerDate managerDate = new ManagerDate();
        private TextView tvContent, tvTime;
        private CheckBox cbDone, cbStar;
        private ImageView ivLoop, ivRemind;

        private void bindView(Todo todo) {
            tvContent.setText(todo.getContent());
            if (managerDate.isEqualDay(new Date(), todo.getCreatedAt())) {
                tvTime.setText("Hôm nay");
            } else {
                tvTime.setText(dateFormat.format(todo.getCreatedAt()));
            }

            if (todo.getLoop()) {
                ivLoop.setVisibility(View.VISIBLE);
            } else {
                ivLoop.setVisibility(View.GONE);
            }

            if (todo.getRemindDate() != null) {
                ivRemind.setVisibility(View.VISIBLE);
            } else {
                ivRemind.setVisibility(View.GONE);
            }

            cbStar.setChecked(todo.getBookmark());

            switch (todo.getStatus()) {
                case Todo.TODO_STATUS_NEW:
                    cbDone.setChecked(false);
                    if ((tvContent.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
                        tvContent.setPaintFlags(tvContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                    break;
                case Todo.TODO_STATUS_DONE:
                    cbDone.setChecked(true);
                    tvContent.setPaintFlags(tvContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    break;
            }
        }
    }

    public interface OnClickMyTodoListener {
        void onClickMyTodo(Todo todo);

        void onChangeCheckbox(Todo todo);

        void onCheckBookmark(Todo todo);
    }

}
