package com.hanabi.todoapp.dao;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.models.RoomChat;

import java.util.Arrays;

public class RoomDao {

    private CollectionReference reference = Database.getDb().collection(RoomChat.NAME_COLL);
    private GetData getData;
    private Activity activity;

    public void setGetData(GetData getData) {
        this.getData = getData;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void createPrivateRoom(RoomChat roomChat) {
        reference.document(roomChat.getRoomId()).set(roomChat);
    }

    public void createPublicRoom(RoomChat roomChat) {
        roomChat.setGroup(true);
        createPrivateRoom(roomChat);
    }

    public void deleteRoom(RoomChat roomChat) {
        if (roomChat.getCreatedBy().equals(Database.getFirebaseUser().getUid())) {
            reference.document(roomChat.getRoomId()).delete();
        }
    }

    public void getListOfRoom() {
        reference
                .whereArrayContains("usersId", Arrays.asList(Database.getFirebaseUser().getUid()))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (getData != null) {
                        getData.getRoom(querySnapshot);
                    }
                })
                .addOnFailureListener(e -> {
                    if (activity != null) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    interface GetData {
        void getRoom(QuerySnapshot snapshot);
    }
}
