package com.hanabi.todoapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.adapter.MyTodoAdapter;
import com.hanabi.todoapp.dao.TodoDao;
import com.hanabi.todoapp.models.LoopTodo;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.ManageDate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MyToDoFragment extends Fragment implements MyTodoAdapter.OnClickMyTodoListener,
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener, TodoDao.DataChangeListener {

    private String titleToolBar = "Hôm nay";

    private RecyclerView rcvTodoNew, rcvTodoDone;
    private MyTodoAdapter adapterNew, adapterDone;
    private SwipeRefreshLayout srlReload;
    private FloatingActionButton fabAdd;

    private LinearLayout llAddTodo, llMore;
    private Chip cpLoop, cpTime, cpPrompt;
    private ImageView ivAdd;
    private EditText edtContent;
    private TextView tvCountDone;

    private InputMethodManager imm;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private ManageDate manageDate = new ManageDate();
    private Calendar calendar = Calendar.getInstance();
    private Date now = calendar.getTime();
    private LoopTodo loopTodo = new LoopTodo();

    private Boolean isLoop = false;
    private Date prompt, createdAt = null;

    private TodoDao todoDao = new TodoDao(getActivity());

    public LinearLayout getLlAddTodo() {
        return llAddTodo;
    }

    public FloatingActionButton getFabAdd() {
        return fabAdd;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_to_do, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        tvCountDone = getActivity().findViewById(R.id.tv_count_done);
        rcvTodoNew = getActivity().findViewById(R.id.rcv_my_todo_new);
        rcvTodoDone = getActivity().findViewById(R.id.rcv_my_todo_done);
        srlReload = getActivity().findViewById(R.id.srl_loading_my_todo);
        fabAdd = getActivity().findViewById(R.id.fab_add_my_todo);
        llAddTodo = getActivity().findViewById(R.id.ll_add_my_todo);
        llMore = getActivity().findViewById(R.id.ll_more);
        ivAdd = getActivity().findViewById(R.id.iv_add_my_todo);
        edtContent = getActivity().findViewById(R.id.edt_content);
        cpLoop = getActivity().findViewById(R.id.cp_set_loop_todo);
        cpTime = getActivity().findViewById(R.id.cp_set_time_todo);
        cpPrompt = getActivity().findViewById(R.id.cp_set_prompt);

        adapterNew = new MyTodoAdapter(getLayoutInflater());
        adapterDone = new MyTodoAdapter(getLayoutInflater());

        rcvTodoNew.setHasFixedSize(true);
        rcvTodoNew.setNestedScrollingEnabled(false);
        rcvTodoDone.setHasFixedSize(true);
        rcvTodoDone.setNestedScrollingEnabled(false);

        llAddTodo.setVisibility(View.GONE);
        ivAdd.setOnClickListener(this);
        cpLoop.setOnClickListener(this);
        cpTime.setOnClickListener(this);
        cpPrompt.setOnClickListener(this);
        llMore.setOnClickListener(this);
        cpLoop.setOnCloseIconClickListener(this);
        cpTime.setOnCloseIconClickListener(this);
        cpPrompt.setOnCloseIconClickListener(this);
        adapterNew.setListener(this);
        adapterDone.setListener(this);
        srlReload.setOnRefreshListener(this);
        fabAdd.setOnClickListener(this);
        srlReload.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorPrimary, null));
        rcvTodoNew.setAdapter(adapterNew);
        rcvTodoDone.setAdapter(adapterDone);

        getActivity().setTitle(titleToolBar);

        swipeRecyclerView(rcvTodoNew, adapterNew);
        swipeRecyclerView(rcvTodoDone, adapterDone);

        todoDao.setListener(this);

        loadingData();
        realtimeUpdate();
    }

    private void realtimeUpdate() {
        todoDao.realtimeUpdate();
    }

    private void loadingData() {

        todoDao.getTodos(manageDate.getNow(manageDate.getDateTomorrow(now)), manageDate.getNow(now), Todo.TODO_STATUS_NEW);
        todoDao.getTodos(manageDate.getNow(manageDate.getDateTomorrow(now)), manageDate.getNow(now), Todo.TODO_STATUS_DONE);

    }

    @Override
    public void onClickMyTodo(final Todo todo) {

    }

    @Override
    public void onClickLongMyTodo(Todo todo) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_my_todo:
                llAddTodo.setVisibility(View.VISIBLE);
                fabAdd.setVisibility(View.GONE);

//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                break;
            case R.id.iv_add_my_todo:
                if (edtContent.getText().toString().trim().isEmpty()) {
                    return;
                }
                Todo todo = new Todo();
                todo.setContent(edtContent.getText().toString().trim());
                todo.setStatus(Todo.TODO_STATUS_NEW);
                if (createdAt != null) {
                    todo.setCreatedAt(createdAt);
                }
                todo.setPromptDate(prompt);
                todo.setLoopTodoMap(loopTodo.toMap());
                todo.setLoop(isLoop);

                todoDao.updateTodo(todo);
                resetDataForm();

                break;

            case R.id.ll_more:
                if (rcvTodoDone.getVisibility() == View.VISIBLE) {
                    rcvTodoDone.setVisibility(View.GONE);
                    return;
                }
                if (rcvTodoDone.getVisibility() == View.GONE) {
                    rcvTodoDone.setVisibility(View.VISIBLE);
                    return;
                }

            default:
                showPopupMemu(view);
        }
    }

    private void resetDataForm() {
        isLoop = false;
        createdAt = null;
        prompt = null;
        loopTodo.reset();
        edtContent.setText("");
        cpLoop.setText("Lặp lại");
        cpLoop.setCloseIconVisible(false);
        cpLoop.setChipBackgroundColorResource(R.color.colorGray);
        cpTime.setText("Đặt lịch");
        cpTime.setCloseIconVisible(false);
        cpTime.setChipBackgroundColorResource(R.color.colorGray);
    }

    @Override
    public void onChangeCheckbox(Todo todo) {
        switch (todo.getStatus()) {
            case Todo.TODO_STATUS_NEW:
                todoDao.doneTodo(todo);
                break;
            case Todo.TODO_STATUS_DONE:
                todo.setStatus(Todo.TODO_STATUS_NEW);
                todoDao.updateTodo(todo);
                break;
        }

    }

    @Override
    public void onRefresh() {
        loadingData();
        srlReload.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_todo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return true;
    }

    private void showPopupMemu(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);

        switch (view.getId()) {
            case R.id.cp_set_loop_todo:
                if (cpLoop.isCloseIconVisible()) {
                    chipClick(cpLoop, "Lặp lại");
                    loopTodo.reset();
                    isLoop = false;
                    return;
                }
                popupMenu.inflate(R.menu.menu_loop_todo);
                break;
            case R.id.cp_set_prompt:
                break;
            case R.id.cp_set_time_todo:
                if (cpTime.isCloseIconVisible()) {
                    chipClick(cpTime, "Đặt lịch");
                    createdAt = null;
                    return;
                }
                popupMenu.inflate(R.menu.menu_time_todo);
                MenuItem tomorrow = popupMenu.getMenu().findItem(R.id.it_tomorrow);
                MenuItem nextTomorrow = popupMenu.getMenu().findItem(R.id.it_next_tomorrow);
                MenuItem nextWeek = popupMenu.getMenu().findItem(R.id.it_next_week);
                tomorrow.setTitle("Ngày mai (" + manageDate.getTomorrow(now) + ")");
                nextTomorrow.setTitle("Ngày kia (" + manageDate.getNextTomorrow(now) + ")");
                nextWeek.setTitle("Tuần sau (Thứ hai)");
                break;
        }

        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_select_day:
                pickDateCreater();
                break;
            case R.id.it_tomorrow:
                createdAt = manageDate.getDateTomorrow(now);
                chipClick(cpTime, dateFormat.format(createdAt));
                break;
            case R.id.it_next_tomorrow:
                createdAt = manageDate.getDateNextTomorrow(now);
                chipClick(cpTime, dateFormat.format(createdAt));
                break;
            case R.id.it_next_week:
                createdAt = manageDate.getDateNextWeek(now);
                chipClick(cpTime, dateFormat.format(createdAt));
                break;
            case R.id.it_loop_day:
                loopTodo.setDays(1);
                isLoop = true;
                chipClick(cpLoop, "Mỗi ngày");
                break;
            case R.id.it_loop_week:
                loopTodo.setDays(7);
                isLoop = true;
                chipClick(cpLoop, "Mỗi tuần vào " + manageDate.getStingDayOfWeek(now));
                break;
            case R.id.it_loop_moth:
                loopTodo.setMonths(1);
                isLoop = true;
                chipClick(cpLoop, "Mỗi tháng");
                break;
            case R.id.it_loop_year:
                loopTodo.setYears(1);
                isLoop = true;
                chipClick(cpLoop, "Mỗi năm");
                break;
            case R.id.it_loop_custom:
                isLoop = true;
//                chipCheck(cpLoop, "Mỗi ngày");
                break;
        }
        return true;
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

    @Override
    public void getTodoSuccess(int code, QuerySnapshot queryDocumentSnapshots) {
        ArrayList<Todo> todos = new ArrayList<>();
        switch (code) {
            case Todo.TODO_STATUS_NEW:
                if (queryDocumentSnapshots.isEmpty()) {
                    adapterNew.setData(todos);
                    return;
                }

                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Todo todo = document.toObject(Todo.class);
                    todos.add(todo);
                    adapterNew.setData(todos);
                }

                break;

            case Todo.TODO_STATUS_DONE:
                if (queryDocumentSnapshots.isEmpty()) {
                    adapterDone.setData(todos);
                    llMore.setVisibility(View.GONE);
                    return;
                }
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Todo todo = document.toObject(Todo.class);
                    todos.add(todo);
                    adapterDone.setData(todos);
                }
                llMore.setVisibility(View.VISIBLE);
                tvCountDone.setText(adapterDone.getItemCount() + "");
                break;
        }

    }

    @Override
    public void deleteTodoSuccess(Todo todo) {
        Snackbar.make(rcvTodoNew, "Thành công", Snackbar.LENGTH_LONG).setAction("Hoàn tác",
                view -> todoDao.updateTodo(todo)).show();
    }

    @Override
    public void realtimeUpdateSuccess() {
        loadingData();
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

    private void swipeRecyclerView(RecyclerView recyclerView, MyTodoAdapter adapter) {
        ItemTouchHelper.SimpleCallback simpleCallbackDelete =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        Todo todo = adapter.getData().get(viewHolder.getAdapterPosition());
                        switch (direction) {
                            case ItemTouchHelper.LEFT:
                                todoDao.deleteTodo(todo);
                                break;
                        }
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                                .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorDanger))
                                .addSwipeLeftLabel("Xóa bỏ")
                                .setSwipeLeftLabelColor(getActivity().getResources().getColor(R.color.colorWhite, null))
                                .create()
                                .decorate();
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };

        new ItemTouchHelper(simpleCallbackDelete).attachToRecyclerView(recyclerView);
    }
}