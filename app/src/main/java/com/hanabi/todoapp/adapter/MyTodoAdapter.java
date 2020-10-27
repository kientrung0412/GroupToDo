package com.hanabi.todoapp.adapter;


import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.R;

import java.util.ArrayList;

public class MyTodoAdapter extends RecyclerView.Adapter<MyTodoAdapter.HolderMyTodo> {


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
            holder.lavStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.isCheckBookmark){
                            holder.lavStar.setSpeed(-1.5f);
                            holder.lavStar.playAnimation();
                            holder.isCheckBookmark = false;

                    } else {
                        holder.lavStar.setSpeed(1.5f);
                        holder.lavStar.playAnimation();
                        holder.isCheckBookmark = true;
                    }
                    listener.onCheckBookmark(todo);
                }
            });
            holder.cbDone.setOnClickListener(view -> listener.onChangeCheckbox(todo));

            holder.itemView.setOnClickListener(view -> listener.onClickMyTodo(todo));

            holder.itemView.setOnLongClickListener(view -> {
                listener.onClickLongMyTodo(todo);
                return true;
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
        private ImageView ivLoop;
        private LottieAnimationView lavStar;
        private Boolean isCheckBookmark = false;

        public HolderMyTodo(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.edt_content_todo);
            ivLoop = itemView.findViewById(R.id.iv_loop);
            lavStar = itemView.findViewById(R.id.lav_star);
//            tvTime = itemView.findViewById(R.id.tv_todo_create_at);
            cbDone = itemView.findViewById(R.id.cb_done);
        }

        private void bindView(Todo todo) {
            tvContent.setText(todo.getContent());

            if (todo.getLoop()) {
                ivLoop.setVisibility(View.VISIBLE);
            } else {
                ivLoop.setVisibility(View.GONE);
            }

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

        void onClickLongMyTodo(Todo todo);

        void onChangeCheckbox(Todo todo);

        void onCheckBookmark(Todo todo);
    }

}
