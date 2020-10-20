package com.hanabi.todoapp.models;

import com.hanabi.todoapp.dao.Database;

import java.util.List;

public class Friend {

    public static final String NAME_COLL = "friend";

    public static final int FRIEND_STATUS_Y = 1;
    public static final int FRIEND_STATUS_Q = 0;

    private String friendId = String.valueOf(System.currentTimeMillis());
    private List<String> userIds;
    private int status;
    private String createdBy = Database.getFirebaseUser().getUid();

    public Friend() {
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
