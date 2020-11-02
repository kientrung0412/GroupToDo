package com.hanabi.todoapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

public class CustomCheckbox extends LottieAnimationView implements View.OnClickListener {

    private Boolean isCheck = false;

    public Boolean getCheckBookmark() {
        return isCheck;
    }

    public void setCheck(Boolean checkBookmark) {
        isCheck = checkBookmark;
        bindViews();
    }

    public CustomCheckbox(Context context) {
        super(context);
        initViews();
    }

    public CustomCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public CustomCheckbox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        isCheck = !isCheck;
        if (isCheck) {
            setSpeed(1.5f);
        } else {
            setSpeed(-1.5f);
        }
        playAnimation();
    }

    private void bindViews() {
        if (isCheck) {
            setSpeed(1.5f);
            playAnimation();
        } else {
            setSpeed(-1.5f);
            playAnimation();
        }
    }
}
