package com.hanabi.todoapp.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;

import com.hanabi.todoapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RemindPickDateDialog implements View.OnClickListener {

    private Activity activity;
    private AlertDialog dialog;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView tvCancel, tvSave, tvDate, tvTime;
    private OnClickSaveListener listener;

    public void setListener(OnClickSaveListener listener) {
        this.listener = listener;
    }

    public RemindPickDateDialog(Activity activity) {
        this.activity = activity;
        initViews();
    }

    private void initViews() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_pick_time_remind_todo, null);

        tvCancel = view.findViewById(R.id.tv_cancel);
        tvSave = view.findViewById(R.id.tv_save);
        tvDate = view.findViewById(R.id.tv_date);
        tvTime = view.findViewById(R.id.tv_time);
        datePicker = view.findViewById(R.id.dp_remind);
        timePicker = view.findViewById(R.id.tp_remind);

        tvCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        tvDate.setOnClickListener(this);

        timePicker.setIs24HourView(true);
        timePicker.setHour(7);
        timePicker.setMinute(0);

        builder.setView(view);
        dialog = builder.create();
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissLoadingDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_date:
                tvDate.setTextColor(activity.getResources().getColor(R.color.colorPrimary, null));
                tvTime.setTextColor(activity.getResources().getColor(R.color.colorGray, null));
                if (datePicker.getVisibility() == View.GONE) {
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_time:
                tvTime.setTextColor(activity.getResources().getColor(R.color.colorPrimary, null));
                tvDate.setTextColor(activity.getResources().getColor(R.color.colorGray, null));
                if (timePicker.getVisibility() == View.GONE) {
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_cancel:
                dismissLoadingDialog();
                break;
            case R.id.tv_save:
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                String dataString = String.format("%s/%s/%s %s:%s", day, month, year, hour, minute);
                try {
                    Date date = simpleDateFormat.parse(dataString);
                    if (listener != null) {
                        listener.clickSave(date);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dismissLoadingDialog();
                break;
        }
    }

    public interface OnClickSaveListener {
        void clickSave(Date date);
    }
}
