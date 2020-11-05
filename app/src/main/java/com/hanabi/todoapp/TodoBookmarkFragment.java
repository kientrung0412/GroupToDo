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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.adapter.MyTodoAdapter;
import com.hanabi.todoapp.dao.TodoDao;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.ManageDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TodoBookmarkFragment extends Fragment implements TodoDao.OnRealTimeUpdate, TodoDao.DataChangeListener, SwipeRefreshLayout.OnRefreshListener, MyTodoAdapter.OnClickMyTodoListener {

    public final String title = "Quan trọng";

    private TodoDao todoDao;
    private MyTodoAdapter adapter;
    private RecyclerView rcvBookmarks;
    private SwipeRefreshLayout srlReload;

    private ManageDate manageDate = new ManageDate();
    private Calendar calendar = Calendar.getInstance();
    private Date now = calendar.getTime();

    public String getTitle() {
        return title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo_bookmark, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        todoDao = new TodoDao();
        todoDao.setActivity(getActivity());

        adapter = new MyTodoAdapter(getLayoutInflater());

        srlReload = getActivity().findViewById(R.id.srl_reloading);
        rcvBookmarks = getActivity().findViewById(R.id.rcv_bookmark);

        srlReload.setOnRefreshListener(this);
        srlReload.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorPrimary, null));
        todoDao.setListener(this);
        adapter.setListener(this);
        rcvBookmarks.setAdapter(adapter);

        swipeRecyclerView(rcvBookmarks, adapter);

        loadingData();
        realtimeData();
    }

    private void realtimeData() {
        todoDao.realtimeUpdate(null, null, Todo.TODO_STATUS_ALL, Todo.BOOKMARK_TRUE);
        todoDao.setRealTimeUpdate(this);
    }

    private void loadingData() {
        todoDao.getTodos(null, null, Todo.TODO_STATUS_ALL, Todo.BOOKMARK_TRUE);
    }

    @Override
    public void todoUpdate(Todo todo) {

    }

    @Override
    public void add(Todo todo) {
        if (adapter.getData() != null) {
            adapter.getData().add(0, todo);
            adapter.sortByCreatedAt();
        }
    }

    @Override
    public void remove(Todo todo) {
        if (adapter.getData() != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Todo todoRemove = adapter.getData().stream().filter(td -> td.getId() == todo.getId()).findAny().orElse(null);
                adapter.getData().remove(todoRemove);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void modified(Todo todo) {
        if (adapter.getData() != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Todo todoModified = adapter.getData().stream().filter(td -> td.getId() == todo.getId()).findAny().orElse(null);
                todoModified.toEquals(todo);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void getTodoSuccess(int core, QuerySnapshot queryDocumentSnapshots) {
        ArrayList<Todo> todos = new ArrayList<>();
        if (queryDocumentSnapshots.isEmpty()) {
            adapter.setData(todos);
            return;
        }
        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
            Todo todo = document.toObject(Todo.class);
            todos.add(todo);
        }
//        setAnimation(rcvTodoNew);
        adapter.setData(todos);
        Log.e(this.getTag(), todos.size() + "");
    }

    @Override
    public void deleteTodoSuccess(Todo todo) {

    }

    @Override
    public void onRefresh() {
        loadingData();
        srlReload.setRefreshing(false);
    }

    @Override
    public void onClickMyTodo(Todo todo) {
        Intent intent = new Intent(getActivity(), DetailTodoActivity.class);
        intent.putExtra(MainActivity.EXTRA_DETAIL_TODO, todo);
        startActivity(intent);
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
    public void onCheckBookmark(Todo todo) {
        todo.setBookmark(!todo.getBookmark());
        todoDao.updateTodo(todo);
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
                        adapter.getData().remove(todo);
                        adapter.notifyDataSetChanged();

                        todoDao.deleteTodo(todo);
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