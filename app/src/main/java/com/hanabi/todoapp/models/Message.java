package com.hanabi.todoapp.models;

import java.util.Date;

public class Message {

    private String content;
    private String userId;
    private Date createdAt = new Date();

    public Message() {
    }

    public Message(String content, String userId, Date createdAt) {
        this.content = content;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
