package com.hanabi.todoapp.models;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

public class User implements Serializable {

    public final static String NAME_COLL = "users";

    private String uid;
    private String displayName;
    private String email;
    private String photoUrl;

    public User() {
    }

    public User(String uid, String displayName, String email, String photoUrl) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void toUser(FirebaseUser firebaseUser) {
        setUid(firebaseUser.getUid());
        setDisplayName(firebaseUser.getDisplayName());
        setEmail(firebaseUser.getEmail());
        setPhotoUrl(firebaseUser.getPhotoUrl().toString());
    }
}
