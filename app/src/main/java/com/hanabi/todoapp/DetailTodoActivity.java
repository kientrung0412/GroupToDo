package com.hanabi.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.hanabi.todoapp.dao.TodoDao;
import com.hanabi.todoapp.dialog.RemindPickDateDialog;
import com.hanabi.todoapp.models.LoopTodo;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.ManageDate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class DetailTodoActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    public static final int TAG_EMPTY = 1;
    public static final int TAG_NOT_EMPTY = 2;

    private LottieAnimationView lavStar;
    private EditText edtContentChildren, edtContent;
    private LinearLayout llAddChildren, llSetTime, llLoop, llRemind;
    private TextView tvSetTime, tvLoop, tvRemind;
    private RecyclerView rcvChildren;
    private CheckBox cbStatus;
    private MaterialToolbar toolbar;
    private PopupMenu popupMenu;
    private Todo todo;
    private LoopTodo loopTodo = new LoopTodo();

    private TodoDao todoDao = new TodoDao();

    private Boolean checkStar = false;
    private Calendar calendar = Calendar.getInstance();
    private ManageDate manageDate = new ManageDate();
    private Date now = calendar.getTime();
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_todo);
        initViews();
        bindViews();
    }

    private void initViews() {
        Intent intent = getIntent();
        todo = (Todo) intent.getSerializableExtra(MainActivity.EXTRA_DETAIL_TODO);

        lavStar = findViewById(R.id.lav_star);
        edtContent = findViewById(R.id.edt_content_children);
        llAddChildren = findViewById(R.id.ll_add_children_todo);
        llSetTime = findViewById(R.id.ll_set_time);
        llLoop = findViewById(R.id.ll_loop);
        llRemind = findViewById(R.id.ll_remind);
        edtContent = findViewById(R.id.edt_content_todo);
        tvSetTime = findViewById(R.id.tv_set_time);
        tvLoop = findViewById(R.id.tv_loop);
        tvRemind = findViewById(R.id.tv_remind);
        rcvChildren = findViewById(R.id.rcv_children_todo);
        cbStatus = findViewById(R.id.cb_status);
        toolbar = findViewById(R.id.tb_main);

        setSupportActionBar(toolbar);

        llAddChildren.setOnClickListener(this);
        llLoop.setOnClickListener(this);
        llRemind.setOnClickListener(this);
        llSetTime.setOnClickListener(this);
        cbStatus.setOnClickListener(this);
        lavStar.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        realtimeUpdate();
    }

    private void realtimeUpdate() {
        todoDao.realtimeUpdateTodo(todo.getId() + "");
        todoDao.setRealTimeUpdate(todo -> {
            this.todo = todo;
            bindViews();
        });
    }

    private void bindViews() {
        edtContent.setText(todo.getContent());
        cbStatus.setChecked(todo.getStatus() == Todo.TODO_STATUS_DONE);
        tvRemind.setTag(TAG_EMPTY);
        tvLoop.setTag(TAG_EMPTY);
        tvSetTime.setTag(TAG_EMPTY);

        //lặp
        if (todo.getLoop()) {
            Map<String, Object> map = todo.getLoopTodoMap();

            String aboutTime = "";
            String listDay = "";

            int days = Integer.parseInt(map.get("days").toString());
            int months = Integer.parseInt(map.get("months").toString());
            int years = Integer.parseInt(map.get("years").toString());
            boolean monday = Boolean.parseBoolean(String.valueOf(map.get("monday")));
            boolean tuesday = Boolean.parseBoolean(String.valueOf(map.get("tuesday")));
            boolean wednesday = Boolean.parseBoolean(String.valueOf(map.get("wednesday")));
            boolean thursday = Boolean.parseBoolean(String.valueOf(map.get("thursday")));
            boolean friday = Boolean.parseBoolean(String.valueOf(map.get("friday")));
            boolean saturday = Boolean.parseBoolean(String.valueOf(map.get("saturday")));
            boolean sunday = Boolean.parseBoolean(String.valueOf(map.get("sunday")));

            if (days > 0) {
                if (days % 7 == 0) {
                    aboutTime = days / 7 + " tuần";

                    if (monday) {
                        listDay += "Thứ Hai, ";
                    }
                    if (tuesday) {
                        listDay += "Thứ Ba, ";
                    }
                    if (wednesday) {
                        listDay += "Thứ Tư, ";
                    }
                    if (thursday) {
                        listDay += "Thứ Năm, ";
                    }
                    if (friday) {
                        listDay += "Thứ Sáu, ";
                    }
                    if (saturday) {
                        listDay += "Thứ Bảy, ";
                    }
                    if (sunday) {
                        listDay += "Chủ Nhật, ";
                    }

                } else {
                    aboutTime = days + " ngày";
                }
            }
            if (months > 0) {
                aboutTime = months + " tháng";
            }
            if (years > 0) {
                aboutTime = years + " năm";
            }


            String loopStr = String.format("Lặp lại mỗi %s", aboutTime);
            if (!listDay.isEmpty()) {
                loopStr += " vào " + listDay.trim().substring(0, listDay.length() - 2);
            }
            setProperty(tvLoop, loopStr);
        }

        //ngày hiển thị
        if (manageDate.isEqualDay(now, todo.getCreatedAt())) {
            setProperty(tvSetTime, "Hôm nay");
        } else {
            setProperty(tvSetTime, dateFormat.format(todo.getCreatedAt()));
        }

        //Nhắc nhở
        if (todo.getRemindDate() != null) {
            if (manageDate.isEqualDay(todo.getRemindDate(), now)) {
                String time = timeFormat.format(todo.getRemindDate());
                setProperty(tvRemind, "Nhắc tôi hôm nay lúc " + time + " giờ");
            } else {
                setProperty(tvRemind, String.format("Nhắc ngày %s lúc %s giờ", dateFormat.format(todo.getRemindDate()), timeFormat.format(todo.getRemindDate())));
            }
        }
    }

    private void setProperty(TextView textView, String content) {
        textView.setTag(TAG_NOT_EMPTY);
        textView.setText(content);
        if (textView.getId() != R.id.tv_set_time) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0);
        }
        textView.setTextColor(getResources().getColor(R.color.colorPrimary, null));
    }

    private void resetProperty(TextView textView, String content) {
        textView.setTag(TAG_EMPTY);
        textView.setText(content);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.colorBlackLight, null));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                finish();
                break;
            case R.id.lav_star:
                if (checkStar) {
                    lavStar.setSpeed(-1.5f);
                    lavStar.playAnimation();
                    checkStar = false;
                } else {
                    lavStar.setSpeed(1.5f);
                    lavStar.playAnimation();
                    checkStar = true;
                }
                break;
            case R.id.ll_add_children_todo:
                edtContent.setFocusable(true);
                break;
            case R.id.ll_loop:
                if (Integer.parseInt(String.valueOf(tvLoop.getTag())) == TAG_NOT_EMPTY) {
                    todo.setLoop(false);
                    loopTodo.reset();
                    todo.setLoopTodoMap(loopTodo.toMap());
                    todoDao.updateTodo(todo);
                    resetProperty(tvLoop, "Lặp lại");
                    return;
                }
                popupMenu = new PopupMenu(this, view);
                popupMenu.inflate(R.menu.menu_loop_todo);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
            case R.id.ll_set_time:
                popupMenu = new PopupMenu(this, view);
                popupMenu.inflate(R.menu.menu_time_todo);
                MenuItem tomorrow = popupMenu.getMenu().findItem(R.id.it_tomorrow);
                MenuItem nextTomorrow = popupMenu.getMenu().findItem(R.id.it_next_tomorrow);
                MenuItem nextWeek = popupMenu.getMenu().findItem(R.id.it_next_week);
                tomorrow.setTitle("Ngày mai (" + manageDate.getTomorrow(now) + ")");
                nextTomorrow.setTitle("Ngày kia (" + manageDate.getNextTomorrow(now) + ")");
                nextWeek.setTitle("Tuần sau (Thứ hai)");
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
            case R.id.ll_remind:
                if (Integer.parseInt(String.valueOf(tvRemind.getTag())) == TAG_NOT_EMPTY) {
                    todo.setRemindDate(null);
                    todoDao.updateTodo(todo);
                    resetProperty(tvRemind, "Nhắc nhở");
                    return;
                }
                RemindPickDateDialog pickDatedialog = new RemindPickDateDialog(this);
                pickDatedialog.showDialog();
                pickDatedialog.setListener(date -> {
                    todo.setRemindDate(date);
                    todoDao.updateTodo(todo);
                });
                break;
            case R.id.cb_status:
                if (cbStatus.isChecked()) {
                    todo.setStatus(Todo.TODO_STATUS_DONE);
                } else {
                    todo.setStatus(Todo.TODO_STATUS_NEW);
                }
                todoDao.updateTodo(todo);
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
                todo.setCreatedAt(manageDate.getDateTomorrow(now));
                break;
            case R.id.it_next_tomorrow:
                todo.setCreatedAt(manageDate.getDateNextTomorrow(now));
                break;
            case R.id.it_next_week:
                todo.setCreatedAt(manageDate.getDateNextWeek(now));
                break;
            case R.id.it_loop_day:
                loopTodo.setDays(1);
                todo.setLoopTodoMap(loopTodo.toMap());
                todo.setLoop(true);
                break;
            case R.id.it_loop_week:
                loopTodo.setMonday(true);
                loopTodo.setDays(7);
                todo.setLoopTodoMap(loopTodo.toMap());
                todo.setLoop(true);
                break;
            case R.id.it_loop_moth:
                loopTodo.setMonths(1);
                todo.setLoopTodoMap(loopTodo.toMap());
                todo.setLoop(true);
                break;
            case R.id.it_loop_year:
                loopTodo.setYears(1);
                todo.setLoopTodoMap(loopTodo.toMap());
                todo.setLoop(true);
                break;
            case R.id.it_loop_custom:
                todo.setLoop(true);
//                todo.setLoopTodoMap(loopTodo.toMap());
//                chipCheck(cpLoop, "Mỗi ngày");
                break;
        }

        todoDao.updateTodo(todo);

        return true;
    }

    private void pickDateCreater() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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
                    todo.setCreatedAt(date);
                    todoDao.updateTodo(todo);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, year, month, day);
        datePickerDialog.show();
    }
}