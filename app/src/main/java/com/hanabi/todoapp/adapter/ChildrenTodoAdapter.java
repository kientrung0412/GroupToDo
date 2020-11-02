package com.hanabi.todoapp.adapter;


import android.app.Activity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hanabi.todoapp.R;


import java.util.ArrayList;
import java.util.Map;

public class ChildrenTodoAdapter extends RecyclerView.Adapter<ChildrenTodoAdapter.HolderChildrenTodo> {

    private LayoutInflater layoutInflater;
    private ArrayList<Map<String, Object>> data;
    private OnClickChildrenTodoListener listener;
    private Activity activity;

    public ChildrenTodoAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void setListener(Activity activity, OnClickChildrenTodoListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void setData(ArrayList<Map<String, Object>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public ArrayList<Map<String, Object>> getData() {
        return data;
    }

    @NonNull
    @Override
    public HolderChildrenTodo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_children_todo, parent, false);
        return new HolderChildrenTodo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderChildrenTodo holder, int position) {
        final Map<String, Object> childrenTodo = data.get(position);
        holder.bindView(childrenTodo);
        if (listener != null) {
            holder.imageView.setOnClickListener(view -> listener.onClickRemoveChildren(position));
            holder.cbDone.setOnClickListener(view -> listener.onClickCheck(position));
            holder.edtContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE && !holder.edtContent.getText().toString().isEmpty()) {
                        listener.onClickUpdateChildren(position, holder.edtContent.getText().toString());
                    }
                    return false;
                }
            });
//            if (activity != null) {
//                KeyboardVisibilityEvent.setEventListener(activity, b -> {
//                    if (!b) {
//                        listener.onClickUpdateChildren(position);
//                    } else {
//                        listener.onClickUpdateChildren(-1);
//                    }
//                });
//            }
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    public class HolderChildrenTodo extends RecyclerView.ViewHolder {

        private EditText edtContent;
        private CheckBox cbDone;
        private ImageView imageView;


        public HolderChildrenTodo(@NonNull View itemView) {
            super(itemView);
            edtContent = itemView.findViewById(R.id.edt_content);
            cbDone = itemView.findViewById(R.id.cb_done);
            imageView = itemView.findViewById(R.id.iv_close);
        }

        private void bindView(Map<String, Object> childrenTodo) {
            edtContent.setImeOptions(EditorInfo.IME_ACTION_DONE);
            edtContent.setRawInputType(InputType.TYPE_CLASS_TEXT);
            edtContent.setText(childrenTodo.get("content").toString());
            cbDone.setChecked(((boolean) childrenTodo.get("isDone")));
//            cbDone.setChecked((Boolean) childrenTodo.get("done"));
        }
    }

    public interface OnClickChildrenTodoListener {
        void onClickRemoveChildren(int position);

        void onClickUpdateChildren(int position, String s);

        void onClickCheck(int position);
    }

}
