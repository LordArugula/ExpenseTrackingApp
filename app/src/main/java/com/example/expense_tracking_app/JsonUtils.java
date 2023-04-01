package com.example.expense_tracking_app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public final class JsonUtils {
    private final static Gson instance = new Gson();
    private final static Type expensesListType = new TypeToken<List<Expense>>() {}
            .getType();
    private final static Type stringListType = new TypeToken<List<String>>() {}
            .getType();

    private JsonUtils() { }

    public static Gson GsonInstance() {
        return instance;
    }

    public static List<Expense> expensesFromJson(String expensesJson) {
        return instance.fromJson(expensesJson, expensesListType);
    }

    public static String expensesToJson(List<Expense> expenses) {
        return instance.toJson(expenses);
    }

    public static List<String> categoriesFromJson(String categoriesJson) {
        return instance.fromJson(categoriesJson, stringListType);
    }

    public static String categoriesToJson(List<String> categories) {
        return instance.toJson(categories);
    }
}
