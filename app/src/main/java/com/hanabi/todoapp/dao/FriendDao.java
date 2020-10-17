package com.hanabi.todoapp.dao;

import com.google.firebase.firestore.CollectionReference;
import com.hanabi.todoapp.models.Friend;

public class FriendDao {

    private CollectionReference reference = Database.getDb().collection(Friend.NAME_COLL);

    public void sendFriendInvitations(Friend friend) {
        friend.setStatus(Friend.FRIEND_STATUS_Q);
        updateFriend(friend);
    }

    private void updateFriend(Friend friend) {
        //Ghi vào danh sách bạn của mình
        reference
                .document(Database.getFirebaseUser().getUid())
                .collection(Friend.NAME_COLL_LIST_FRIENDS)
                .document(friend.getUserFriendId())
                .set(friend);
        //Ghi vào danh sách bạn của người muốn kết bạn
        reference
                .document(friend.getUserFriendId())
                .collection(Friend.NAME_COLL_LIST_FRIENDS)
                .document(Database.getFirebaseUser().getUid())
                .set(friend);
    }


}
