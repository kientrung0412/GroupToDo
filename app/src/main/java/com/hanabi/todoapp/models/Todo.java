package com.hanabi.todoapp.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Todo {

    public static final int TODO_STATUS_NEW = 1;
    public static final int TODO_STATUS_DONE = 2;

    public static final String TODO_COLL_MY_TODO = "my_todo";
    public static final String TODO_COLL = "todo";


    private long id = System.currentTimeMillis();
    private String content;
    private int status;
    private Date createdAt = new Date();
    private Boolean isLoop = false;
    private Date promptDate = null;
    private Map<String, Object> loopTodoMap = new HashMap<>();

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

    public Map<String, Object> getLoopTodoMap() {
        return loopTodoMap;
    }

    public void setLoopTodoMap(Map<String, Object> loopTodoMap) {
        this.loopTodoMap = loopTodoMap;
    }

    public Boolean getLoop() {
        return isLoop;
    }

    public void setLoop(Boolean loop) {
        isLoop = loop;
    }

    public void toEquals(Todo originalTodo) {
        setStatus(originalTodo.getStatus());
        setId(originalTodo.getId());
        setContent(originalTodo.getContent());
        setCreatedAt(originalTodo.getCreatedAt());
        setLoop(originalTodo.getLoop());
        setPromptDate(originalTodo.getPromptDate());
        setLoopTodoMap(originalTodo.getLoopTodoMap());
    }
}
