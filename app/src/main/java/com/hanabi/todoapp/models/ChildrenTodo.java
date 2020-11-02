package com.hanabi.todoapp.models;

import java.util.HashMap;
import java.util.Map;

public class ChildrenTodo {
    private boolean isDone;
    private String content;

    public ChildrenTodo(boolean isDone, String content) {
        this.isDone = isDone;
        this.content = content;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("isDone", isDone);
        result.put("content", content);
        return result;
    }
}
