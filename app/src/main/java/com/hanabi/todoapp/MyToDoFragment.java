package com.hanabi.todoapp;

import android.graphics.Canvas;
import android.nfc.Tag;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.adapter.MyTodoAdapter;
import com.hanabi.todoapp.dialog.CreateMyToDialog;
import com.hanabi.todoapp.models.Todo;

import java.util.ArrayList;
import java.util.Arrays;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MyToDoFragment extends Fragment implements MyTodoAdapter.OnClickMyTodoListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private RecyclerView rcvMyTodos;
    private MyTodoAdapter adapter;
    private SwipeRefreshLayout srlReload;
    private FloatingActionButton fabAdd;

    private CreateMyToDialog createMyToDialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_to_do, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createMyToDialog = new CreateMyToDialog(getActivity());

        rcvMyTodos = getActivity().findViewById(R.id.rcv_my_todo);
        srlReload = getActivity().findViewById(R.id.srl_loading_my_todo);
        fabAdd = getActivity().findViewById(R.id.fab_add_todo);


        adapter = new MyTodoAdapter(getLayoutInflater());
        adapter.setListener(this);
        srlReload.setOnRefreshListener(this);
        fabAdd.setOnClickListener(this);
        srlReload.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorPrimary, null));
        rcvMyTodos.setAdapter(adapter);
        new ItemTouchHelper(simpleCallbackDelete).attachToRecyclerView(rcvMyTodos);

        loadingData();
        updateData();
    }

    private void updateData() {

        db.collection(Todo.TODO_COLL).document(user.getUid())
                .collection(Todo.TODO_COLL_MY_TODO)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        db.collection(Todo.TODO_COLL).document(user.getUid())
                .collection(Todo.TODO_COLL_MY_TODO)
                .whereEqualTo("status", Todo.TODO_STATUS_NEW)
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

    private void updateTodo(Todo todo) {
        db.collection(Todo.TODO_COLL).document(user.getUid())
                .collection(Todo.TODO_COLL_MY_TODO)
                .document(todo.getId() + "")
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

    private void dissTodo(Todo todo) {
        todo.setStatus(Todo.TODO_STATUS_DISABLED);
        updateTodo(todo);
    }

    private void fallTodo(Todo todo) {
        todo.setStatus(Todo.TODO_STATUS_FAILED);
        updateTodo(todo);
    }

    private void doneTodo(Todo todo) {
        todo.setStatus(Todo.TODO_STATUS_DONE);
        updateTodo(todo);
    }


    ItemTouchHelper.SimpleCallback simpleCallbackDelete =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    Todo todo = adapter.getData().get(viewHolder.getAdapterPosition());
                    switch (direction) {
                        case ItemTouchHelper.LEFT:
                            dissTodo(todo);
                            break;
                        case ItemTouchHelper.RIGHT:
                            fallTodo(todo);
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
                            .addSwipeRightActionIcon(R.drawable.ic_diss)
                            .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorYellow))
                            .addSwipeRightLabel("Không hoàn thành")
                            .setSwipeRightLabelColor(getActivity().getResources().getColor(R.color.colorWhite, null))
                            .create()
                            .decorate();
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };


    @Override
    public void onClickMyTodo(final Todo todo) {
        createMyToDialog.updateTodoDialog(todo);
    }

    @Override
    public void onClickLongMyTodo(Todo todo) {

    }

    @Override
    public void onChangeCheckbox(final Todo todo, final CompoundButton compoundButton) {
        doneTodo(todo);
        Snackbar.make(getView(), "Thành công", Snackbar.LENGTH_LONG).setAction("Hoàn tác", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todo.setStatus(Todo.TODO_STATUS_NEW);
                updateTodo(todo);
            }
        }).show();
        compoundButton.setChecked(false);
    }

    @Override
    public void onRefresh() {
        loadingData();
        srlReload.setRefreshing(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_todo:
                createMyToDialog.show();
                break;
        }
    }
}