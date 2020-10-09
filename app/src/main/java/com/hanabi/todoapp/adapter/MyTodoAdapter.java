package com.hanabi.todoapp.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.R;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyTodoAdapter extends RecyclerView.Adapter<MyTodoAdapter.HolderMyTodo> {

    public static final String TAG = MyTodoAdapter.class.getName();
    private LayoutInflater layoutInflater;
    private ArrayList<Todo> data;
    private OnClickMyTodoListener listener;

    public MyTodoAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void setListener(OnClickMyTodoListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<Todo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public ArrayList<Todo> getData() {
        return data;
    }

    public void addData(Todo todo) {
        data.add(todo);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public HolderMyTodo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_to_do, parent, false);
        return new HolderMyTodo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMyTodo holder, int position) {
        final Todo todo = data.get(position);
        holder.bindView(todo);
        if (listener != null) {
            holder.cbDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onChangeCheckbox(todo);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickMyTodo(todo);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onClickLongMyTodo(todo);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class HolderMyTodo extends RecyclerView.ViewHolder {

        private TextView tvContent, tvTime;
        private CheckBox cbDone;

        public HolderMyTodo(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content_todo);
            tvTime = itemView.findViewById(R.id.tv_todo_create_at);
            cbDone = itemView.findViewById(R.id.cb_done);
        }

        private void bindView(Todo todo) {
            tvContent.setText(todo.getContent());
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
            String dateStr = dateFormat.format(todo.getId());
            tvTime.setText(dateStr);

            switch (todo.getStatus()) {
                case Todo.TODO_STATUS_NEW:
                    cbDone.setChecked(false);
                    break;
                case Todo.TODO_STATUS_DONE:
                    cbDone.setChecked(true);
                    break;
            }
        }

    }

    public interface OnClickMyTodoListener {
        void onClickMyTodo(Todo todo);

        void onClickLongMyTodo(Todo todo);

        void onChangeCheckbox(Todo todo);

        void onClickHeader();
    }

}
