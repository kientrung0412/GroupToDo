package com.hanabi.todoapp.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.flexbox.FlexboxLayout;
import com.hanabi.todoapp.R;
import com.hanabi.todoapp.models.LoopTodo;
import com.hanabi.todoapp.models.Todo;


public class CustomerLoopDialog implements OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private Activity activity;
    private AlertDialog dialog;
    private Spinner spinner;
    private TextView tvOk, tvCancel;
    private EditText edtNum;
    private FlexboxLayout flexboxLayout;
    private LoopTodo loopTodo;
    private OnClickButtonListener listener;
    private int[] ids = new int[]{R.id.cb_mon, R.id.cb_tues, R.id.cb_wed, R.id.cb_thurs, R.id.cb_fri, R.id.cb_sat, R.id.cb_sun};
    private CheckBox[] checkBoxes = new CheckBox[7];

    public CustomerLoopDialog(Activity activity) {
        this.activity = activity;
        initViews();
    }

    public void setListener(OnClickButtonListener listener) {
        this.listener = listener;
    }

    private void initViews() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_customer_time_loop, null);

        spinner = view.findViewById(R.id.spn_time);
        edtNum = view.findViewById(R.id.edt_num);
        tvOk = view.findViewById(R.id.tv_save);
        tvCancel = view.findViewById(R.id.tv_cancel);
        flexboxLayout = view.findViewById(R.id.fbl_day_week);
        for (int i = 0; i < ids.length; i++) {
            checkBoxes[i] = view.findViewById(ids[i]);
            checkBoxes[i].setOnCheckedChangeListener(this);
        }

        String[] strings = new String[]{"ngày", "tuần", "tháng", "năm"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        edtNum.setText("1");
        spinner.setAdapter(adapter);
        spinner.setSelection(1);
        tvCancel.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);

        builder.setView(view);
        dialog = builder.create();
    }

    public void show() {
        loopTodo = new LoopTodo();
        dialog.show();
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save:
                if (listener != null) {
                    int number = Integer.parseInt(edtNum.getText().toString());
                    switch (spinner.getSelectedItemPosition()) {
                        case 0:
                            loopTodo.setDays(number);
                            break;
                        case 1:
                            loopTodo.setWeeks(number);
                            break;
                        case 2:
                            loopTodo.setMonths(number);
                            break;
                        case 3:
                            loopTodo.setYears(number);
                            break;
                    }
                    listener.OnClickOk(loopTodo);
                }
                break;
            case R.id.tv_cancel:

                break;
        }
        dismiss();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 1) {
            flexboxLayout.setVisibility(View.VISIBLE);
        } else {
            flexboxLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cb_mon:
                if (b) {
                    checkBoxes[0].setTextColor(activity.getResources().getColor(R.color.colorWhite, null));
                } else {
                    checkBoxes[0].setTextColor(activity.getResources().getColor(R.color.colorBlack, null));
                }
                loopTodo.setMonday(b);
                break;
            case R.id.cb_tues:
                if (b) {
                    checkBoxes[1].setTextColor(activity.getResources().getColor(R.color.colorWhite, null));
                } else {
                    checkBoxes[1].setTextColor(activity.getResources().getColor(R.color.colorBlack, null));
                }
                loopTodo.setTuesday(b);
                break;
            case R.id.cb_wed:
                if (b) {
                    checkBoxes[2].setTextColor(activity.getResources().getColor(R.color.colorWhite, null));
                } else {
                    checkBoxes[2].setTextColor(activity.getResources().getColor(R.color.colorBlack, null));
                }
                loopTodo.setWednesday(b);
                break;
            case R.id.cb_thurs:
                if (b) {
                    checkBoxes[3].setTextColor(activity.getResources().getColor(R.color.colorWhite, null));
                } else {
                    checkBoxes[3].setTextColor(activity.getResources().getColor(R.color.colorBlack, null));
                }
                loopTodo.setThursday(b);
                break;
            case R.id.cb_fri:
                if (b) {
                    checkBoxes[4].setTextColor(activity.getResources().getColor(R.color.colorWhite, null));
                } else {
                    checkBoxes[4].setTextColor(activity.getResources().getColor(R.color.colorBlack, null));
                }
                loopTodo.setFriday(b);
                break;
            case R.id.cb_sat:
                if (b) {
                    checkBoxes[5].setTextColor(activity.getResources().getColor(R.color.colorWhite, null));
                } else {
                    checkBoxes[5].setTextColor(activity.getResources().getColor(R.color.colorBlack, null));
                }
                loopTodo.setSaturday(b);
                break;
            case R.id.cb_sun:
                if (b) {
                    checkBoxes[6].setTextColor(activity.getResources().getColor(R.color.colorWhite, null));
                } else {
                    checkBoxes[6].setTextColor(activity.getResources().getColor(R.color.colorBlack, null));
                }
                loopTodo.setSunday(b);
                break;
        }
    }

    public interface OnClickButtonListener {
        void OnClickOk(LoopTodo loopTodo);
    }
}
