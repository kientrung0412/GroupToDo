package com.hanabi.todoapp.dao;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class Database {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public static FirebaseFirestore getDb() {
        return db;
    }

    public static FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

}
