package com.hanabi.todoapp.dao;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.models.Friend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FriendDao {

    private Activity activity;
    private GetData getData;
    private OnListener listener;

    public void setGetData(GetData getData) {
        this.getData = getData;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setListener(OnListener listener) {
        this.listener = listener;
    }

    private CollectionReference reference = Database.getDb().collection(Friend.NAME_COLL);

    public void sendFriendInvitations(Friend friend) {
        friend.setStatus(Friend.FRIEND_STATUS_Q);
        updateFriend(friend);
    }

    public void acceptFriendInvitation(Friend friend) {
        friend.setStatus(Friend.FRIEND_STATUS_Y);
        updateFriend(friend);
    }

    public void getListOfFriend() {
        reference
                .whereArrayContains("userIds", Arrays.asList(Database.getFirebaseUser().getUid()))
                .get()
//                .addOnSuccessListener(snapshost -> getData.getFriends(snapshost))
                .addOnFailureListener(e -> {
                    if (activity != null) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void listenerFriend(Friend friend) {
        reference
                .whereArrayContains("userIds", Arrays.asList(Database.getFirebaseUser().getUid()))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        listener.dataChanged(value);

                    }
                });
    }

    public void declineFriendInvitation(Friend friend) {
        reference.document(friend.getFriendId()).delete();
    }

    private void updateFriend(Friend friend) {
        reference
                .document(friend.getFriendId())
                .set(friend)
                .addOnFailureListener(e -> {
                    if (activity != null) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void isFriend(List<String> userIds) {
        reference
                .whereArrayContainsAny("userIds", Arrays.asList(userIds.get(0)))
                .get()
                .addOnSuccessListener(snapshost -> {
                    ArrayList<Friend> friends = new ArrayList<>();
                    for (DocumentSnapshot item : snapshost.getDocuments()) {
                        Friend friend = item.toObject(Friend.class);
                        if (friend.getUserIds().get(0).equals(friend.getUserIds().get(1))) {
                            getData.getFriends(friends);
                            return;
                        }
                        if (friend.getUserIds().contains(userIds.get(1))) {
                            friends.add(friend);
                        }
                    }
                    getData.getFriends(friends);
                })
                .addOnFailureListener(e -> {
                    if (activity != null) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public interface GetData {
        void getFriends(ArrayList<Friend> friends);
    }

    public interface OnListener {
        void dataChanged(QuerySnapshot value);
    }
}
