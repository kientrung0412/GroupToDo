package com.hanabi.todoapp.models;

import android.provider.Settings;

import java.util.Date;

public class Message {
    public static final String NAME_COLL = "message";
    public static final String NAME_COLL_CHILDREN = "messages";

    private String messageId = String.valueOf(System.currentTimeMillis());
    private String messageContent;
    private String createdBy;
    private Date createdAt = new Date();

    public Message() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
