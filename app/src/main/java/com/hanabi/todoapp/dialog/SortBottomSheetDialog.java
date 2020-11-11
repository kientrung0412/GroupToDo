package com.hanabi.todoapp.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hanabi.todoapp.R;

public class  SortBottomSheetDialog implements View.OnClickListener {

    private LinearLayout llByDate, llByBookmark, llByAZ;
    private Activity activity;
    private BottomSheetDialog bottomSheetDialog;
    private OnClickMenuListener onClickMenuListener;

    public SortBottomSheetDialog(Activity activity, OnClickMenuListener onClickMenuListener) {
        this.activity = activity;
        this.onClickMenuListener = onClickMenuListener;
        initViews();
    }

    private void initViews() {
        View view = View.inflate(activity, R.layout.dialog_sort_todo, null);
        llByDate = view.findViewById(R.id.ll_sort_by_date);
        llByBookmark = view.findViewById(R.id.ll_sort_by_bookmark);
        llByAZ = view.findViewById(R.id.ll_sort_by_az);

        llByDate.setOnClickListener(this);
        llByBookmark.setOnClickListener(this);
        llByAZ.setOnClickListener(this);

        bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(view);
    }

    public void show() {
        bottomSheetDialog.show();
    }

    public void dismiss() {
        if (bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_sort_by_date:
                onClickMenuListener.clickDate();
                dismiss();
                break;
            case R.id.ll_sort_by_bookmark:
                onClickMenuListener.clickBookmark();
                dismiss();
                break;
            case R.id.ll_sort_by_az:
                onClickMenuListener.clickAZ();
                dismiss();
                break;
        }
    }

    public interface OnClickMenuListener {
        void clickDate();

        void clickBookmark();

        void clickAZ();
    }
}
