package com.hanabi.todoapp.models;

import com.hanabi.todoapp.dao.Database;

public class Friend {

    public static final String NAME_COLL = "friend";
    public static final String NAME_COLL_LIST_FRIENDS = "friends";

    public static final int FRIEND_STATUS_Y = 1;
    public static final int FRIEND_STATUS_Q = 0;

    private String userFriendId;
    private int status;


    public String getUserFriendId() {
        return userFriendId;
    }

    public void setUserFriendId(String userFriendId) {
        this.userFriendId = userFriendId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
