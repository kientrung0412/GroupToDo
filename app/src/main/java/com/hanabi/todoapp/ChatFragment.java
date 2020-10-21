package com.hanabi.todoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChatFragment extends Fragment {

    private String titleToolBar = "Tin nhắn";


    private BottomNavigationView bottomNavigationView;

    private DirectoryFragment directoryFragment = new DirectoryFragment();
    private RoomChatFragment roomChatFragment = new RoomChatFragment();


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
        showFrag(roomChatFragment);
    }

    private void initViews() {
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.it_message) {
                showFrag(roomChatFragment);
                return true;
            }
            return true;
        });
    }

    public void showFrag(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fm_chat, fragment);
        transaction.commit();
    }


}