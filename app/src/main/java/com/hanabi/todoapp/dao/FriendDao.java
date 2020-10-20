package com.hanabi.todoapp.dao;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.models.Friend;

import java.util.Arrays;
import java.util.Collections;

public class FriendDao {

    public static final String TAG = FriendDao.class.getName();
    private GetData getData;

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
                .addOnSuccessListener(queryDocumentSnapshots -> getData.getFriends(queryDocumentSnapshots))
                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
    }

    public void declineFriendInvitation(Friend friend) {
        reference.document(friend.getFriendId()).delete();
    }

    private void updateFriend(Friend friend) {
        reference
                .document(friend.getFriendId())
                .set(friend)
                .addOnFailureListener(e -> Log.d(TAG, e.getMessage()));
    }

    interface GetData {
        void getFriends(QuerySnapshot snapshot);
    }
}
