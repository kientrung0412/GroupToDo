package com.hanabi.todoapp.adapter;


import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.R;
import com.hanabi.todoapp.utils.ManagerDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MyTodoAdapter extends RecyclerView.Adapter<MyTodoAdapter.HolderMyTodo> {


    private LayoutInflater layoutInflater;
    private ArrayList<Todo> data = new ArrayList<>();
    private OnClickMyTodoListener listener;
    private ManagerDate managerDate = new ManagerDate();
    private Calendar calendar = Calendar.getInstance();
    private Date now = calendar.getTime();


    public MyTodoAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void setListener(OnClickMyTodoListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<Todo> data) {
        this.data = data;
        sortByCreatedAt();
    }

    public ArrayList<Todo> getData() {
        return data;
    }

    public void addItem(Todo todo) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Todo rs = data.stream().filter(td -> td.getId() == todo.getId()).findAny().orElse(null);
            if (rs == null) {
                data.add(todo);
            }
        }
    }

    public void addItem(int index, Todo todo) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Todo rs = data.stream().filter(td -> td.getId() == todo.getId()).findAny().orElse(null);
            if (rs == null) {
                data.add(index, todo);
            } else {
                rs.toEquals(todo);
                data.add(rs);
            }
        }
    }

    public void sortByCreatedAt() {
        Collections.sort(data, (t1, t2) -> (int) (t2.getId() - t1.getId()));
        notifyDataSetChanged();
    }

    public void sortByBookmark() {
        Collections.sort(data, (t1, t2) -> Boolean.compare(t2.getBookmark(), t1.getBookmark()));
        notifyDataSetChanged();
    }

    public void sortByContent() {
        Collections.sort(data, (t1, t2) -> t1.getContent().compareTo(t2.getContent()));
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
            holder.cbStar.setOnClickListener(view -> listener.onCheckBookmark(todo));
            holder.cbDone.setOnClickListener(view -> listener.onChangeCheckbox(todo));
            holder.itemView.setOnClickListener(view -> listener.onClickMyTodo(todo));
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class HolderMyTodo extends RecyclerView.ViewHolder {

        private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        private TextView tvContent, tvTime;
        private CheckBox cbDone, cbStar;
        private ImageView ivLoop, ivRemind;

        public HolderMyTodo(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.edt_content_todo);
            ivLoop = itemView.findViewById(R.id.iv_loop);
            ivRemind = itemView.findViewById(R.id.iv_remind);
            cbStar = itemView.findViewById(R.id.cb_bookmark);
            tvTime = itemView.findViewById(R.id.tv_todo_create_at);
            cbDone = itemView.findViewById(R.id.cb_done);
        }

        private void bindView(Todo todo) {
            tvContent.setText(todo.getContent());
            if (managerDate.isEqualDay(now, todo.getCreatedAt())) {
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
