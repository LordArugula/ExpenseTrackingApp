package com.example.expense_tracking_app.services;

import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.example.expense_tracking_app.R;
import com.example.expense_tracking_app.models.Expense;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class SharedPrefsExpenseRepository implements ExpenseRepository {
    private static final String EXPENSES_KEY = "SHARED_PREF_EXPENSES";
    private static final String EXPENSES_DEFAULT = "";
    private static final Type expensesListType = new TypeToken<List<Expense>>() {
    }.getType();

    private final SharedPreferences _sharedPreferences;
    private final Gson _gson;

    private final List<Expense> expenses;
    private final Set<String> defaultCategories;
    private final Set<String> customCategories;

    @Inject
    public SharedPrefsExpenseRepository(Resources resources, SharedPreferences sharedPreferences, Gson gson) {
        _sharedPreferences = sharedPreferences;
        _gson = gson;

        expenses = new ArrayList<>();

        String[] defaultCategories = resources.getStringArray(R.array.expense_categories);
        this.defaultCategories = Collections.unmodifiableSet(new TreeSet<>(Arrays.asList(defaultCategories)));

        customCategories = new TreeSet<>();
    }

    @Override
    public List<Expense> getAll() {
        if (!expenses.isEmpty()) {
            return expenses;
        }

        String expensesJsonString = _sharedPreferences.getString(EXPENSES_KEY, EXPENSES_DEFAULT);
        if (!expensesJsonString.equals(EXPENSES_DEFAULT)) {
            List<Expense> expenses = _gson.fromJson(expensesJsonString, expensesListType);

            customCategories.clear();
            List<String> customCategories = expenses.stream().map(Expense::getCategory)
                    .distinct()
                    .filter(category -> !defaultCategories.contains(category))
                    .collect(Collectors.toList());
            this.customCategories.addAll(customCategories);

            return expenses;
        }

        return new ArrayList<>();
    }

    @Override
    public Optional<Expense> getById(int id) {
        return getAll().stream()
                .filter(expense -> expense.getId() == id)
                .findFirst();
    }

    @Override
    public void add(@NonNull Expense expense) {
        List<Expense> expenses = getAll();

        int id = getNewId(expenses);
        expense.setId(id);

        expenses.add(expense);
        addCategory(expense.getCategory());

        writeToSharedPreferences(expenses);
    }

    private void addCategory(String category) {
        if (defaultCategories.contains(category)) {
            return;
        }

        customCategories.add(category);
    }

    @Override
    public void addRange(List<Expense> expenses) {
        List<Expense> _expenses = getAll();

        int id = getNewId(_expenses);
        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);
            expense.setId(id);
            _expenses.add(expense);
            addCategory(expense.getCategory());
            id++;
        }

        writeToSharedPreferences(_expenses);
    }

    private int getNewId(@NonNull List<Expense> expenses) {
        Optional<Expense> highestId = expenses.stream()
                .max(Comparator.comparingInt(Expense::getId));

        return highestId.map(Expense::getId)
                .orElse(0) + 1;
    }

    @Override
    public Optional<Expense> update(int id, Expense expense) {
        List<Expense> expenses = getAll();

        for (int i = 0; i < expenses.size(); i++) {
            Expense _expense = expenses.get(i);
            if (_expense.getId() == id) {
                expenses.set(i, expense);
                addCategory(expense.getCategory());

                writeToSharedPreferences(expenses);
                return Optional.of(_expense);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Expense> remove(int id) {
        List<Expense> expenses = getAll();

        for (int i = 0; i < expenses.size(); i++) {
            Expense _expense = expenses.get(i);
            if (_expense.getId() == id) {
                expenses.remove(i);

                writeToSharedPreferences(expenses);
                return Optional.of(_expense);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean contains(int id) {
        return getAll().stream()
                .anyMatch(expense -> expense.getId() == id);
    }

    @Override
    public void clear() {
        _sharedPreferences.edit()
                .remove(EXPENSES_KEY)
                .apply();
    }

    public Set<String> getDefaultCategories() {
        return Collections.unmodifiableSet(defaultCategories);
    }

    public Set<String> getCustomCategories() {
        return Collections.unmodifiableSet(customCategories);
    }

    @Override
    public String[] getCategories() {
        List<String> categories = new ArrayList<>(defaultCategories.size() + customCategories.size());
        categories.addAll(defaultCategories);
        categories.addAll(customCategories);
        return categories.toArray(new String[0]);
    }

    private void writeToSharedPreferences(List<Expense> expenses) {
        String expensesJsonString = _gson.toJson(expenses);
        _sharedPreferences.edit()
                .putString(EXPENSES_KEY, expensesJsonString)
                .apply();
    }
}
