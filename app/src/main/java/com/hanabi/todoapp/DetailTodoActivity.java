package com.hanabi.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.appbar.MaterialToolbar;
import com.hanabi.todoapp.adapter.ChildrenTodoAdapter;
import com.hanabi.todoapp.dao.TodoDao;
import com.hanabi.todoapp.dialog.CustomerLoopDialog;
import com.hanabi.todoapp.dialog.RemindPickDateDialog;
import com.hanabi.todoapp.models.ChildrenTodo;
import com.hanabi.todoapp.models.LoopTodo;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.ManagerDate;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DetailTodoActivity extends AppCompatActivity
        implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, TextView.OnEditorActionListener, KeyboardVisibilityEventListener, ChildrenTodoAdapter.OnClickChildrenTodoListener {

    public static final int TAG_EMPTY = 1;
    public static final int TAG_NOT_EMPTY = 2;

    private CheckBox cbBookmark;
    private EditText edtContentChildren, edtContent;
    private LinearLayout llAddChildren, llSetTime, llLoop, llRemind;
    private TextView tvSetTime, tvLoop, tvRemind, tvCreateAt;
    private RecyclerView rcvChildren;
    private CheckBox cbStatus;
    private MaterialToolbar toolbar;
    private ImageView ivDelete;
    private PopupMenu popupMenu;
    private Todo todo;
    private LoopTodo loopTodo = new LoopTodo();

    private TodoDao todoDao = new TodoDao();
    private ChildrenTodoAdapter adapter;

    private Calendar calendar = Calendar.getInstance();
    private ManagerDate managerDate = new ManagerDate();
    private Date now = calendar.getTime();
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private boolean clickDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_todo);
        initViews();
        bindViews();
    }

    private void initViews() {
        KeyboardVisibilityEvent.setEventListener(this, this);
        adapter = new ChildrenTodoAdapter(getLayoutInflater());

        Intent intent = getIntent();
        todo = (Todo) intent.getSerializableExtra(MainActivity.EXTRA_DETAIL_TODO);

        cbBookmark = findViewById(R.id.cb_bookmark);
        edtContentChildren = findViewById(R.id.edt_content_children);
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
        tvCreateAt = findViewById(R.id.tv_create_at);
        ivDelete = findViewById(R.id.iv_delete);
        setSupportActionBar(toolbar);

        edtContent.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edtContent.setRawInputType(InputType.TYPE_CLASS_TEXT);
        edtContentChildren.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edtContentChildren.setRawInputType(InputType.TYPE_CLASS_TEXT);

        edtContent.setOnEditorActionListener(this);
        edtContentChildren.setOnEditorActionListener(this);
        llAddChildren.setOnClickListener(this);
        llLoop.setOnClickListener(this);
        llRemind.setOnClickListener(this);
        llSetTime.setOnClickListener(this);
        cbStatus.setOnClickListener(this);
        cbBookmark.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(this);

        adapter.setListener(this, this);
        rcvChildren.setAdapter(adapter);
        realtimeUpdate();

        swipeRecyclerView(rcvChildren);
    }

    private void realtimeUpdate() {
        todoDao.realtimeUpdateTodo(todo.getId() + "");
        todoDao.setRealTimeUpdate(new TodoDao.OnRealTimeUpdate() {
            @Override
            public void todoUpdate(Todo todo) {
                DetailTodoActivity.this.todo = todo;
                bindViews();
            }

            @Override
            public void add(Todo todo) {

            }

            @Override
            public void remove(Todo todo) {

            }

            @Override
            public void modified(Todo todo) {

            }
        });
    }

    private void bindViews() {
        if (todo.getChildrenTodo() != null) {
            adapter.setData(todo.getChildrenTodo());
        }
        edtContent.setText(todo.getContent());
        cbStatus.setChecked(todo.getStatus() == Todo.TODO_STATUS_DONE);
        tvRemind.setTag(TAG_EMPTY);
        tvLoop.setTag(TAG_EMPTY);
        tvSetTime.setTag(TAG_EMPTY);
        cbBookmark.setChecked(todo.getBookmark());
        if (managerDate.isEqualDay(now, todo.getCreatedAt())) {
            tvCreateAt.setText("Hôm nay");
        } else {
            tvCreateAt.setText(dateFormat.format(todo.getCreatedAt()));
        }

        //lặp
        if (todo.getLoop()) {
            Map<String, Object> map = todo.getLoopTodoMap();
            LoopTodo loopTodo = LoopTodo.parse(map);
            setProperty(tvLoop, loopTodo.toString());
        }

        //ngày hiển thị
        if (managerDate.isEqualDay(now, todo.getCreatedAt())) {
            setProperty(tvSetTime, "Hôm nay");
        } else {
            setProperty(tvSetTime, dateFormat.format(todo.getCreatedAt()));
        }

        //Nhắc nhở
        if (todo.getRemindDate() != null) {
            if (managerDate.isEqualDay(todo.getRemindDate(), now)) {
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
            case R.id.cb_bookmark:
                todo.setBookmark(!todo.getBookmark());
                todoDao.updateTodo(todo);
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
                tomorrow.setTitle("Ngày mai (" + managerDate.getTomorrow(now) + ")");
                nextTomorrow.setTitle("Ngày kia (" + managerDate.getNextTomorrow(now) + ")");
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
            case R.id.iv_delete:
                todoDao.deleteTodo(todo);
                onBackPressed();
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
                todo.setCreatedAt(managerDate.getDateTomorrow(now));
                break;
            case R.id.it_next_tomorrow:
                todo.setCreatedAt(managerDate.getDateNextTomorrow(now));
                break;
            case R.id.it_next_week:
                todo.setCreatedAt(managerDate.getDateNextWeek(now));
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
                CustomerLoopDialog dialog = new CustomerLoopDialog(this);
                dialog.show();
                dialog.setListener(loopTodo -> {
                    Toast.makeText(this, loopTodo.toString(), Toast.LENGTH_SHORT).show();
                    todo.setLoopTodoMap(loopTodo.toMap());
                    todoDao.updateTodo(todo);
                });
                return true;
        }

        todoDao.updateTodo(todo);

        return true;
    }

    private void pickDateCreater() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, yearSelect, monthOfYearSelect, dayOfMonthSelect) -> {
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
        }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            clickDone = true;
            switch (textView.getId()) {
                case R.id.edt_content_todo:
                    updateContentTodo();
                    return false;
                case R.id.edt_content_children:
                    updateChildren();
                    return true;
            }
        }
        return false;
    }


    @Override
    public void onVisibilityChanged(boolean b) {
        if (b) {
            clickDone = false;
        } else {
            if (!clickDone) {
                updateContentTodo();
                updateChildren();
            }
        }
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
        }
    }

    private void updateContentTodo() {
        String content = edtContent.getText().toString();
        if (content.isEmpty()) {
            edtContent.setText(todo.getContent());
        } else {
            todo.setContent(content);
            todoDao.updateTodo(todo);
        }
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
        }
    }

    private void updateChildren() {
        ArrayList<Map<String, Object>> children = todo.getChildrenTodo();
        if (children == null) {
            children = new ArrayList<>();
        }
        String contentChildren = edtContentChildren.getText().toString();
        if (contentChildren.isEmpty()) {
            return;
        }
        ChildrenTodo childrenTodo = new ChildrenTodo(false, contentChildren);
        children.add(childrenTodo.toMap());
        todo.setChildrenTodo(children);
        todoDao.updateTodo(todo);
        edtContentChildren.setText("");
    }

    @Override
    public void onClickRemoveChildren(int position) {
        deleteChildren(position);
    }

    @Override
    public void onClickUpdateChildren(int position, String s) {
        Map<String, Object> map = todo.getChildrenTodo().get(position);
        map.put("content", s);
        todoDao.updateTodo(todo);
//        Log.e(this.getClass().getName(), "onClickUpdateChildren: ");
    }

    @Override
    public void onClickCheck(int position) {
        Map<String, Object> map = todo.getChildrenTodo().get(position);
        map.put("isDone", !(boolean) map.get("isDone"));
        todoDao.updateTodo(todo);
    }

    private void deleteChildren(int position) {
        todo.getChildrenTodo().remove(position);
        todoDao.updateTodo(todo);
    }

    private void swipeRecyclerView(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallbackDelete =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        todo.getChildrenTodo().remove(viewHolder.getAdapterPosition());
                        todoDao.updateTodo(todo);
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                                .addSwipeLeftBackgroundColor(ContextCompat.getColor(DetailTodoActivity.this, R.color.colorDanger))
                                .setSwipeLeftLabelColor(DetailTodoActivity.this.getResources().getColor(R.color.colorWhite, null))
                                .create()
                                .decorate();
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };

        new ItemTouchHelper(simpleCallbackDelete).attachToRecyclerView(recyclerView);
    }
}