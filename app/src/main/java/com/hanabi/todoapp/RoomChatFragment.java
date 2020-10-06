package com.hanabi.todoapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.adapter.RoomChatAdapter;
import com.hanabi.todoapp.models.Message;
import com.hanabi.todoapp.models.RoomChat;

import java.util.Arrays;
import java.util.List;

public class RoomChatFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private RoomChatAdapter adapter;
    private RecyclerView rcvRoom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room_chat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new RoomChatAdapter(getLayoutInflater());
        rcvRoom = getActivity().findViewById(R.id.rcv_room_chat);
        rcvRoom.setAdapter(adapter);
        loadingAdapter();
    }

    private void loadingAdapter() {
        RoomChat roomChat = new RoomChat();
        roomChat.setUserIds(Arrays.asList(user.getUid(), "263bgy1v3h1h3uiy89bsdas"));

        CollectionReference collection = db.collection("room_chat");
        collection.document().set(roomChat);

//        collection.whereArrayContains("userIds", user.getUid()).get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        List<RoomChat> roomChats = queryDocumentSnapshots.toObjects(RoomChat.class);
//                    }
//                });

    }
}