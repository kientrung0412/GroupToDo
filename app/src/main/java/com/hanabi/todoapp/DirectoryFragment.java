package com.hanabi.todoapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.hanabi.todoapp.dao.Database;
import com.hanabi.todoapp.dao.FriendDao;
import com.hanabi.todoapp.models.Friend;

public class DirectoryFragment extends Fragment {

    private FriendDao friendDao = new FriendDao();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_directory, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        initViews();
    }

    private void initViews() {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_directory, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.it_add_friend:
                intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
                break;
            case R.id.it_notification:
                intent = new Intent(getActivity(), AddFriendActivity.class);
                break;
        }
        return true;
    }
}