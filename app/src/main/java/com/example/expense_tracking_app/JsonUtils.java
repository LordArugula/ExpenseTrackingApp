package com.example.expense_tracking_app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public final class JsonUtils {
    private final static Gson instance = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toEpochDay()))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> LocalDate.ofEpochDay(json.getAsLong()))
            .create();
    private final static Type expensesListType = new TypeToken<List<Expense>>() {
    }
            .getType();
    private final static Type stringListType = new TypeToken<List<String>>() {
    }
            .getType();

    private JsonUtils() {
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
