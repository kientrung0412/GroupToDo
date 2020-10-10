package com.hanabi.todoapp.models;

import android.os.SystemClock;

import java.util.Date;

public class Todo {

    public static final int TODO_STATUS_NEW = 1;
    public static final int TODO_STATUS_FAILED = 0;
    public static final int TODO_STATUS_DONE = 2;

    public static final String TODO_COLL_MY_TODO = "my_todo";
    public static final String TODO_COLL_GROUP_TODO = "group_todo";
    public static final String TODO_COLL = "todo";

    public static final int LOOP_DAYS = 1;
    public static final int LOOP_MONTHS = 2;
    public static final int LOOP_YEAS = 3;
//    public static final int LOOP_DAYS = 4;

    private long id = System.currentTimeMillis();
    private String content;
    private int status;
    private Boolean isLoop = false;
    private int typeLoop;
    private Date createdAt = new Date();
    private Date promptDate = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Boolean getLoop() {
        return isLoop;
    }

    public void setLoop(Boolean loop) {
        isLoop = loop;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getPromptDate() {
        return promptDate;
    }

    public void setPromptDate(Date promptDate) {
        this.promptDate = promptDate;
    }

    public int getTypeLoop() {
        return typeLoop;
    }

    public void setTypeLoop(int typeLoop) {
        this.typeLoop = typeLoop;
    }

    public void toEquals(Todo originalTodo) {
        setStatus(originalTodo.getStatus());
        setId(originalTodo.getId());
        setContent(originalTodo.getContent());
    }
}
