package com.hanabi.todoapp;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.adapter.MyTodoAdapter;
import com.hanabi.todoapp.dao.Database;
import com.hanabi.todoapp.dialog.CreateMyToDialog;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.utils.ManageDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MyToDoFragment extends Fragment implements MyTodoAdapter.OnClickMyTodoListener,
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, CreateMyToDialog.clickButtonListener, PopupMenu.OnMenuItemClickListener {

    private RecyclerView rcvTodoNew;
    private MyTodoAdapter adapterNew;
    private SwipeRefreshLayout srlReload;
    private FloatingActionButton fabAdd;

    private LinearLayout llAddTodo;
    private Chip cpLoop, cpTime;
    private ImageView ivAdd;
    private EditText edtContent;

    private InputMethodManager imm;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private ManageDate manageDate = new ManageDate();
    private Date now = new Date();

    private Date prompt, createdAt = null;
    private Boolean isLoop = false;


    private CollectionReference reference = Database.getDb().collection(Todo.TODO_COLL)
            .document(Database.getFirebaseUser().getUid()).collection(Todo.TODO_COLL_MY_TODO);

    private ItemTouchHelper.SimpleCallback simpleCallbackDelete =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    Todo todo = adapterNew.getData().get(viewHolder.getAdapterPosition());
                    switch (direction) {
                        case ItemTouchHelper.LEFT:
                            deleteTodo(todo);
                            break;
                        case ItemTouchHelper.RIGHT:
//                            fallTodo(todo);
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
//                            .addSwipeRightActionIcon(R.drawable.ic_diss)
//                            .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorYellow))
//                            .addSwipeRightLabel("Không hoàn thành")
//                            .setSwipeRightLabelColor(getActivity().getResources().getColor(R.color.colorWhite, null))
                            .create()
                            .decorate();
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };

    public LinearLayout getLlAddTodo() {
        return llAddTodo;
    }

    public InputMethodManager getImm() {
        return imm;
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
//        createMyToDialog = new CreateMyToDialog(getActivity());
        imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        rcvTodoNew = getActivity().findViewById(R.id.rcv_my_todo_new);
        srlReload = getActivity().findViewById(R.id.srl_loading_my_todo);
        fabAdd = getActivity().findViewById(R.id.fab_add_my_todo);
        llAddTodo = getActivity().findViewById(R.id.ln_add_my_todo);
        ivAdd = getActivity().findViewById(R.id.iv_add_my_todo);
        edtContent = getActivity().findViewById(R.id.edt_content);
        cpLoop = getActivity().findViewById(R.id.cp_set_loop_todo);
        cpTime = getActivity().findViewById(R.id.cp_set_time_todo);

//        createMyToDialog.setListener(this);
        adapterNew = new MyTodoAdapter(getLayoutInflater());


        llAddTodo.setVisibility(View.GONE);
        ivAdd.setOnClickListener(this);
        cpLoop.setOnClickListener(this);
        cpTime.setOnClickListener(this);

        adapterNew.setListener(this);
        srlReload.setOnRefreshListener(this);
        fabAdd.setOnClickListener(this);
        srlReload.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorPrimary, null));
        rcvTodoNew.setAdapter(adapterNew);

        setTitleToolBar("Hôm nay");

        new ItemTouchHelper(simpleCallbackDelete).attachToRecyclerView(rcvTodoNew);

        loadingData();
        updateData();
    }

    private void updateData() {

        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getActivity(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingData();
            }
        });
    }

    private void loadingData() {
        getTodos(adapterNew);
    }

    private void getTodos(MyTodoAdapter adapter) {
        reference.whereIn("status", Arrays.asList(Todo.TODO_STATUS_DONE, Todo.TODO_STATUS_NEW))
                .orderBy("id", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Todo> todos = new ArrayList<>();
                        if (queryDocumentSnapshots.isEmpty()) {
                            adapter.setData(todos);
                            return;
                        }
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Todo todo = document.toObject(Todo.class);
                            todos.add(todo);
                            adapter.setData(todos);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Lỗi:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTodo(final Todo todo) {
        reference.document(todo.getId() + "")
                .set(todo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteTodo(Todo todo) {
        Todo originalTodo = new Todo();
        originalTodo.toEquals(todo);

        reference.document(todo.getId() + "")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        undoTodo(originalTodo);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void fallTodo(Todo todo) {
        todo.setStatus(Todo.TODO_STATUS_FAILED);
        updateTodo(todo);
    }

    private void doneTodo(Todo todo) {
        Todo originalTodo = new Todo();
        originalTodo.setStatus(todo.getStatus());
        originalTodo.setId(todo.getId());
        originalTodo.setContent(todo.getContent());

        todo.setStatus(Todo.TODO_STATUS_DONE);
        updateTodo(todo);
        undoTodo(originalTodo);
    }

    private void undoTodo(Todo todo) {
        Snackbar.make(rcvTodoNew, "Thành công", Snackbar.LENGTH_LONG).setAction("Hoàn tác",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateTodo(todo);
                    }
                }).show();
    }

    private void setTitleToolBar(String title) {
        ((MainActivity) getActivity()).getToolbar().setTitle(title);
        ((MainActivity) getActivity()).getToolbar().setOnClickListener(view -> {
            Toast.makeText(getActivity(), "ahsdg", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onClickMyTodo(final Todo todo) {

    }

    @Override
    public void onClickLongMyTodo(Todo todo) {
//        if (todo.getStatus() == Todo.TODO_STATUS_NEW) {
//            createMyToDialog.updateTodoDialog(todo);
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_my_todo:
                llAddTodo.setVisibility(View.VISIBLE);
                fabAdd.setVisibility(View.GONE);

                edtContent.forceLayout();
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                break;
            case R.id.iv_add_my_todo:
                if (edtContent.getText().toString().trim().isEmpty()) {
                    return;
                }
                Todo todo = new Todo();
                todo.setContent(edtContent.getText().toString().trim());
                todo.setStatus(Todo.TODO_STATUS_NEW);
                todo.setCreatedAt(createdAt);
                todo.setPromptDate(prompt);

                updateTodo(todo);

                edtContent.setText("");
                prompt = null;
                isLoop = false;

                break;
            default:
                showPopupMemu(view);
        }
    }

    @Override
    public void onClickButtonSend(Todo todo) {
        todo.setStatus(Todo.TODO_STATUS_NEW);
        updateTodo(todo);
    }

    @Override
    public void onChangeCheckbox(Todo todo) {
        switch (todo.getStatus()) {
            case Todo.TODO_STATUS_NEW:
                doneTodo(todo);
                break;
            case Todo.TODO_STATUS_DONE:
                Todo originalTodo = new Todo();
                originalTodo.toEquals(todo);

                todo.setStatus(Todo.TODO_STATUS_NEW);
                updateTodo(todo);
                undoTodo(originalTodo);
                break;
        }

    }

    @Override
    public void onClickHeader() {

    }

    @Override
    public void onRefresh() {
        loadingData();
        srlReload.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_my_todo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return true;
    }

    private void showPopupMemu(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);

        switch (view.getId()) {
            case R.id.cp_set_loop_todo:
                popupMenu.inflate(R.menu.menu_loop_todo);
                break;
            case R.id.co_set_prompt:

                break;
            case R.id.cp_set_time_todo:
                if (cpTime.isCloseIconVisible()) {
                    cpTime.setText("Đặt lịch");
                    createdAt = null;
                    cpTime.setCloseIconVisible(false);
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
                pickDate();
                break;
            case R.id.it_tomorrow:
                createdAt = manageDate.getDateTomorrow(now);
                cpTime.setText(dateFormat.format(createdAt));
                cpTime.setCloseIconVisible(true);
                break;
            case R.id.it_next_tomorrow:
                createdAt = manageDate.getDateNextTomorrow(now);
                cpTime.setText(dateFormat.format(createdAt));
                cpTime.setCloseIconVisible(true);
                break;
            case R.id.it_next_week:
                createdAt = manageDate.getDateNextWeek(now);
                cpTime.setText(dateFormat.format(createdAt));
                cpTime.setCloseIconVisible(true);
                break;
        }
        return true;
    }

    private void pickDate() {
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Chọn một ngày");
        final MaterialDatePicker materialDatePicker;
        materialDatePicker = builder.build();
        materialDatePicker.show(getActivity().getSupportFragmentManager(), "Chon ngày");
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Date date = new Date(Long.parseLong(selection.toString()));
                if (date.getTime() - new Date().getTime() > 0) {
                    createdAt = date;
                    cpTime.setText(dateFormat.format(createdAt));
                    cpTime.setCloseIconVisible(true);
                }

            }
        });
    }
}