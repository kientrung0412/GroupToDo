package com.hanabi.todoapp.models;

import java.util.Date;
import java.util.List;

public class RoomChat {

    public static final String NAME_COLL = "room";

    private String roomId = String.valueOf(System.currentTimeMillis());
    private String roomName;
    private String createdBy;
    private List<String> usersId;
    private String roomImageUrl;
    private boolean isGroup = false;

    public RoomChat() {
    }

    public static String getNameColl() {
        return NAME_COLL;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<String> getUsersId() {
        return usersId;
    }

    public void setUsersId(List<String> usersId) {
        this.usersId = usersId;
    }

    public String getRoomImageUrl() {
        return roomImageUrl;
    }

    public void setRoomImageUrl(String roomImageUrl) {
        this.roomImageUrl = roomImageUrl;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }
}
