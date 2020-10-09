package com.hanabi.todoapp.models;

import android.os.SystemClock;

public class Todo {

    public static final int TODO_STATUS_NEW = 1;
    public static final int TODO_STATUS_FAILED = 0;
    public static final int TODO_STATUS_DONE = 2;
    public static final int TODO_STATUS_DISABLED = -1;

    public static final String TODO_COLL_MY_TODO = "my_todo";
    public static final String TODO_COLL_GROUP_TODO = "group_todo";
    public static final String TODO_COLL = "todo";

    private long id = System.currentTimeMillis();
    private String content;
    private int status;

    public Todo() {
    }

    public Todo(long id, String content, int status) {
        this.id = id;
        this.content = content;
        this.status = status;
    }

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
}
