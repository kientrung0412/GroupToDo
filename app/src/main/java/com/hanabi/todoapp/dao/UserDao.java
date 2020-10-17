package com.hanabi.todoapp.dao;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.models.User;

public class UserDao {

    private CollectionReference reference = Database.getDb().collection(User.NAME_COLL);
    private GetDataQuery dataQuery;

    public void setDataQuery(GetDataQuery dataQuery) {
        this.dataQuery = dataQuery;
    }

    public void createUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.toUser(firebaseUser);
        reference
                .document(user.getUid())
                .set(user);
    }


    public void searchFriendByEmail(String email) {
        reference
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (dataQuery == null) {
                            return;
                        }

                        if (queryDocumentSnapshots.isEmpty()) {
                            dataQuery.getFriend(null);
                            return;
                        }

                        User user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                        dataQuery.getFriend(user);
                    }
                });
    }


    public interface GetDataQuery {
        void getFriend(User user);
    }
}
