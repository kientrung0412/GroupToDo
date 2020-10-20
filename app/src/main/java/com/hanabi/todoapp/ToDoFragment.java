package com.hanabi.todoapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.adapter.MyTodoAdapter;
import com.hanabi.todoapp.dao.TodoDao;
import com.hanabi.todoapp.dialog.FormTodoBottomSheetDialog;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.ManageDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ToDoFragment extends Fragment
        implements View.OnClickListener, MyTodoAdapter.OnClickMyTodoListener, SwipeRefreshLayout.OnRefreshListener, TodoDao.DataChangeListener {

    public static final String TAG = ToDoFragment.class.getName();
    private String titleToolBar = "Hôm nay";

    private RecyclerView rcvTodoNew, rcvTodoDone;
    private MyTodoAdapter adapterNew, adapterDone;
    private SwipeRefreshLayout srlReload;
    private FloatingActionButton fabAdd;

    private LinearLayout llMore;
    private TextView tvCountDone;

    private ManageDate manageDate = new ManageDate();
    private Calendar calendar = Calendar.getInstance();
    private Date now = calendar.getTime();

    private TodoDao todoDao = new TodoDao(getActivity());

    public FloatingActionButton getFabAdd() {
        return fabAdd;
    }

    public String getTitleToolBar() {
        return titleToolBar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        tvCountDone = getActivity().findViewById(R.id.tv_count_done);
        rcvTodoNew = getActivity().findViewById(R.id.rcv_my_todo_new);
        rcvTodoDone = getActivity().findViewById(R.id.rcv_my_todo_done);
        srlReload = getActivity().findViewById(R.id.srl_loading_my_todo);
        fabAdd = getActivity().findViewById(R.id.fab_add_my_todo);
        llMore = getActivity().findViewById(R.id.ll_more);

        adapterNew = new MyTodoAdapter(getLayoutInflater());
        adapterDone = new MyTodoAdapter(getLayoutInflater());

        rcvTodoNew.setHasFixedSize(true);
        rcvTodoNew.setNestedScrollingEnabled(false);
        rcvTodoDone.setHasFixedSize(true);
        rcvTodoDone.setNestedScrollingEnabled(false);

        llMore.setOnClickListener(this);

        adapterNew.setListener(this);
        adapterDone.setListener(this);
        srlReload.setOnRefreshListener(this);
        fabAdd.setOnClickListener(this);
        srlReload.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorPrimary, null));
        rcvTodoNew.setAdapter(adapterNew);
        rcvTodoDone.setAdapter(adapterDone);

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

        todoDao.getTodos(manageDate.getDate(manageDate.getDateTomorrow(now)), manageDate.getDate(now), Todo.TODO_STATUS_NEW);
        todoDao.getTodos(manageDate.getDate(manageDate.getDateTomorrow(now)), manageDate.getDate(now), Todo.TODO_STATUS_DONE);

    }

    @Override
    public void onClickMyTodo(Todo todo) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(MainActivity.EXTRA_DETAIL_TODO, todo);
        startActivity(intent);
    }

    @Override
    public void onClickLongMyTodo(Todo todo) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_my_todo:
                FormTodoBottomSheetDialog sheetDialog = new FormTodoBottomSheetDialog();
                sheetDialog.show(getActivity().getSupportFragmentManager(), TAG);
                break;
            case R.id.ll_more:
                if (rcvTodoDone.getVisibility() == View.VISIBLE) {
                    rcvTodoDone.setVisibility(View.GONE);
                    return;
                }
                if (rcvTodoDone.getVisibility() == View.GONE) {
                    rcvTodoDone.setVisibility(View.VISIBLE);
                    setAnimation(rcvTodoDone);
                    return;
                }
        }
    }


    @Override
    public void onChangeCheckbox(Todo todo) {
        switch (todo.getStatus()) {
            case Todo.TODO_STATUS_NEW:
                todo.setCompletedDate(now);
                todo.setStatus(Todo.TODO_STATUS_DONE);
                todoDao.updateTodo(todo);
                break;
            case Todo.TODO_STATUS_DONE:
                todo.setCompletedDate(null);
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
        inflater.inflate(R.menu.menu_todo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return true;
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
                }
                setAnimation(rcvTodoNew);
                adapterNew.setData(todos);
                break;

            case Todo.TODO_STATUS_DONE:
                if (queryDocumentSnapshots.isEmpty()) {
                    llMore.setVisibility(View.GONE);
                    adapterDone.setData(todos);
                    return;
                }
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Todo todo = document.toObject(Todo.class);
                    todos.add(todo);
                }
                setAnimation(rcvTodoDone);
                adapterDone.setData(todos);
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


    private void setAnimation(RecyclerView recyclerView) {
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);
        recyclerView.setLayoutAnimation(animationController);
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
                                .setSwipeLeftLabelColor(getActivity().getResources().getColor(R.color.colorWhite, null))
                                .create()
                                .decorate();
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };

        new ItemTouchHelper(simpleCallbackDelete).attachToRecyclerView(recyclerView);
    }
}