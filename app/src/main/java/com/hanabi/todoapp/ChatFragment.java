package com.hanabi.todoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hanabi.todoapp.adapter.RoomChatAdapter;

public class ChatFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener {

    private String titleToolBar = "Tin nhắn";

    private RoomChatAdapter adapter;
    private RecyclerView rcvRoom;
    private SearchView searchView;

    public String getTitle() {
        return titleToolBar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        loadingAdapter();
    }

    private void initViews() {
        adapter = new RoomChatAdapter(getLayoutInflater());
        rcvRoom = getActivity().findViewById(R.id.rcv_room_chat);

        getActivity().setTitle("Danh sách nhóm");

        rcvRoom.setAdapter(adapter);
    }

    private void loadingAdapter() {
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_chat, menu);
//        inflater.inflate(R.menu.menu_todo, menu);
//        searchView = (SearchView) menu.findItem(R.id.it_search).getActionView();
//        searchView.setOnClickListener(this);
//        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(getContext(), "agdhg", Toast.LENGTH_SHORT).show();
    }
}