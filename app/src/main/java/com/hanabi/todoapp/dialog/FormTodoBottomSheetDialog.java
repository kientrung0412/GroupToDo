package com.hanabi.todoapp.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.hanabi.todoapp.R;
import com.hanabi.todoapp.dao.TodoDao;
import com.hanabi.todoapp.models.LoopTodo;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.ManagerDate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormTodoBottomSheetDialog extends BottomSheetDialogFragment
        implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private Calendar calendar = Calendar.getInstance();

    private ImageView ivAdd;
    private Chip cpLoop, cpTime, cpRemind;
    private EditText edtContent;

    private PopupMenu popupMenu;

    private Boolean bookmark = false;
    private Boolean isLoop = false;
    private Date remindDate, createdAt, now = null;
    private LoopTodo loopTodo = new LoopTodo();
    private ManagerDate managerDate;
    private TodoDao todoDao;
    private InputMethodManager imm;

    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private DateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yy HH:mm");

    public FormTodoBottomSheetDialog() {
    }

    public FormTodoBottomSheetDialog(Boolean bookmark) {
        this.bookmark = bookmark;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.dialog_add_todo, null);
        edtContent = view.findViewById(R.id.edt_content);
        cpLoop = view.findViewById(R.id.cp_set_loop_todo);
        cpTime = view.findViewById(R.id.cp_set_time_todo);
        cpRemind = view.findViewById(R.id.cp_set_remind);
        ivAdd = view.findViewById(R.id.iv_add_my_todo);

        managerDate = new ManagerDate();
        todoDao = new TodoDao();
        todoDao.setContext(getActivity());
        now = calendar.getTime();

        imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        cpLoop.setOnClickListener(this);
        cpTime.setOnClickListener(this);
        cpRemind.setOnClickListener(this);
        cpLoop.setOnCloseIconClickListener(this);
        cpTime.setOnCloseIconClickListener(this);
        cpRemind.setOnCloseIconClickListener(this);
        ivAdd.setOnClickListener(this);

        bottomSheet.setContentView(view);

        return bottomSheet;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_my_todo:
                if (edtContent.getText().toString().trim().isEmpty()) {
                    return;
                }
                Todo todo = new Todo();
                todo.setBookmark(bookmark);
                todo.setContent(edtContent.getText().toString().trim());
                todo.setStatus(Todo.TODO_STATUS_NEW);
                if (createdAt != null) {
                    todo.setCreatedAt(createdAt);
                }
                todo.setRemindDate(remindDate);
                todo.setLoopTodoMap(loopTodo.toMap());
                todo.setLoop(isLoop);

                todoDao.updateTodo(todo);
                resetDataForm();
                break;
            case R.id.cp_set_loop_todo:
                if (cpLoop.isCloseIconVisible()) {
                    chipClick(cpLoop, "Lặp lại");
                    loopTodo.reset();
                    isLoop = false;
                    return;
                }
                popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.inflate(R.menu.menu_loop_todo);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
            case R.id.cp_set_remind:
                if (cpRemind.isCloseIconVisible()) {
                    chipClick(cpRemind, "Nhắc nhở");
                    remindDate = null;
                    return;
                }
                RemindPickDateDialog pickDatedialog = new RemindPickDateDialog(getActivity());
                pickDatedialog.showDialog();
                pickDatedialog.setListener(date -> {
                    remindDate = date;
                    chipClick(cpRemind, dateTimeFormat.format(date));
                });
                break;
            case R.id.cp_set_time_todo:
                if (cpTime.isCloseIconVisible()) {
                    chipClick(cpTime, "Đặt lịch");
                    createdAt = null;
                    return;
                }
                popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.inflate(R.menu.menu_time_todo);
                MenuItem tomorrow = popupMenu.getMenu().findItem(R.id.it_tomorrow);
                MenuItem nextTomorrow = popupMenu.getMenu().findItem(R.id.it_next_tomorrow);
                MenuItem nextWeek = popupMenu.getMenu().findItem(R.id.it_next_week);
                tomorrow.setTitle("Ngày mai (" + managerDate.getTomorrow(now) + ")");
                nextTomorrow.setTitle("Ngày kia (" + managerDate.getNextTomorrow(now) + ")");
                nextWeek.setTitle("Tuần sau (Thứ hai)");
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_select_day:
                pickDateCreater();
                break;
            case R.id.it_tomorrow:
                createdAt = managerDate.getDateTomorrow(now);
                chipClick(cpTime, dateFormat.format(createdAt));
                break;
            case R.id.it_next_tomorrow:
                createdAt = managerDate.getDateNextTomorrow(now);
                chipClick(cpTime, dateFormat.format(createdAt));
                break;
            case R.id.it_next_week:
                createdAt = managerDate.getDateNextWeek(now);
                chipClick(cpTime, dateFormat.format(createdAt));
                break;
            case R.id.it_loop_day:
                loopTodo.setDays(1);
                isLoop = true;
                chipClick(cpLoop, loopTodo.toString());
                break;
            case R.id.it_loop_week:
                loopTodo.setMonday(true);
                loopTodo.setDays(7);
                isLoop = true;
                chipClick(cpLoop, loopTodo.toString());
                break;
            case R.id.it_loop_moth:
                loopTodo.setMonths(1);
                isLoop = true;
                chipClick(cpLoop, loopTodo.toString());
                break;
            case R.id.it_loop_year:
                loopTodo.setYears(1);
                isLoop = true;
                chipClick(cpLoop, loopTodo.toString());
                break;
            case R.id.it_loop_custom:
                isLoop = true;
                CustomerLoopDialog dialog = new CustomerLoopDialog(getActivity());
                dialog.show();
                dialog.setListener(loopTodo -> {
                    this.loopTodo = loopTodo;
                    chipClick(cpLoop, loopTodo.toString());
                });
                break;
        }
        return true;
    }

    private void resetDataForm() {
        isLoop = false;
        createdAt = null;
        remindDate = null;
        loopTodo.reset();
        edtContent.setText("");
        cpLoop.setText("Lặp lại");
        cpLoop.setCloseIconVisible(false);
        cpLoop.setChipBackgroundColorResource(R.color.colorGray);
        cpTime.setText("Đặt lịch");
        cpTime.setCloseIconVisible(false);
        cpTime.setChipBackgroundColorResource(R.color.colorGray);
        cpRemind.setText("Nhắc nhở");
        cpRemind.setCloseIconVisible(false);
        cpRemind.setChipBackgroundColorResource(R.color.colorGray);
    }

    private void chipClick(Chip chip, String text) {
        chip.setText(text);
        chip.setCloseIconVisible(!chip.isCloseIconVisible());
        if (chip.isCloseIconVisible()) {
            chip.setChipBackgroundColorResource(R.color.colorPrimary);
            return;
        }
        chip.setChipBackgroundColorResource(R.color.colorGray);
    }

    private void pickDateCreater() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearSelect, int monthOfYearSelect, int dayOfMonthSelect) {
                if (yearSelect - year < 0) {
                    return;
                }
                if (monthOfYearSelect - month < 0) {
                    return;
                } else if (monthOfYearSelect - month == 0) {
                    if (dayOfMonthSelect - day < 0) {
                        return;
                    }
                }
                try {
                    Date date = dateFormat.parse(dayOfMonthSelect + "/" + (monthOfYearSelect + 1) + "/" + yearSelect);
                    createdAt = date;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                chipClick(cpTime, dateFormat.format(createdAt));
            }
        }, year, month, day);
        datePickerDialog.show();
    }


}
