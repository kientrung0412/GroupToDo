package com.hanabi.todoapp.utils;

import com.hanabi.todoapp.models.Todo;

import java.util.ArrayList;

public class ManagerRemind {

    private static ArrayList<Todo> todos = new ArrayList<>();

    public static ArrayList<Todo> getTodos() {
        return todos;
    }

    public static void setTodos(ArrayList<Todo> todos) {
        ManagerRemind.todos = todos;
    }
}
