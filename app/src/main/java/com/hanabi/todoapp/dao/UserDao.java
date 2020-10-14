package com.hanabi.todoapp.dao;

import com.google.firebase.auth.FirebaseUser;
import com.hanabi.todoapp.models.User;

public class UserDao {

    public void createUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.toUser(firebaseUser);

        Database.getDb().collection(User.NAME_COLL).document(user.getUid()).set(user);
    }


}
