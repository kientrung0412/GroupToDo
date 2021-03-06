package com.hanabi.todoapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Todo implements Serializable {

    public static final int TODO_STATUS_NEW = 1;
    public static final int TODO_STATUS_DONE = 2;
    public static final int TODO_STATUS_ALL = 3;

    public static final int BOOKMARK_TRUE = 1;
    public static final int BOOKMARK_NONE = -1;
    public static final int BOOKMARK_FALSE = 0;

    public static final String TODO_COLL_MY_TODO = "my_todo";
    public static final String TODO_COLL = "todo";

    private long id = System.currentTimeMillis();
    private String content;
    private int status;
    private Date createdAt = new Date();
    private Boolean isLoop = false;
    private Date remindDate = null;
    private Date completedDate = null;
    private Boolean bookmark = false;
    private ArrayList<Map<String, Object>> childrenTodo = null;
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

    public Date getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(Date remindDate) {
        this.remindDate = remindDate;
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

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public void setLoop(Boolean loop) {
        isLoop = loop;
    }

    public Boolean getBookmark() {
        return bookmark;
    }

    public void setBookmark(Boolean bookmark) {
        this.bookmark = bookmark;
    }

    public ArrayList<Map<String, Object>> getChildrenTodo() {
        return childrenTodo;
    }

    public void setChildrenTodo(ArrayList<Map<String, Object>> childrenTodo) {
        this.childrenTodo = childrenTodo;
    }

    public void toEquals(Todo originalTodo) {
        setStatus(originalTodo.getStatus());
        setId(originalTodo.getId());
        setContent(originalTodo.getContent());
        setCreatedAt(originalTodo.getCreatedAt());
        setLoop(originalTodo.getLoop());
        setRemindDate(originalTodo.getRemindDate());
        setCompletedDate(originalTodo.getCompletedDate());
        setBookmark(originalTodo.getBookmark());
        setChildrenTodo(originalTodo.childrenTodo);
        setLoopTodoMap(originalTodo.loopTodoMap);
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", isLoop=" + isLoop +
                ", remindDate=" + remindDate +
                ", completedDate=" + completedDate +
                ", bookmark=" + bookmark +
                ", childrenTodo=" + childrenTodo +
                ", loopTodoMap=" + loopTodoMap +
                '}';
    }
}
