package com.hanabi.todoapp.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.models.Todo;
import com.hanabi.todoapp.models.User;

public class UserDao {

    private CollectionReference reference = Database.getDb().collection(User.NAME_COLL);
    private GetDataQuery dataQuery;

    public void setDataQuery(GetDataQuery dataQuery) {
        this.dataQuery = dataQuery;
    }

    public void createUser(User firebaseUser) {
        reference
                .document(firebaseUser.getUid())
                .set(firebaseUser);
    }


    public void searchFriendByEmail(String email) {
        reference
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) {
                        dataQuery.getFriend(null);
                        return;
                    }
                    User user = snapshot.getDocuments().get(0).toObject(User.class);
                    dataQuery.getFriend(user);
                });
    }


    public interface GetDataQuery {
        void getFriend(User user);
    }
}
