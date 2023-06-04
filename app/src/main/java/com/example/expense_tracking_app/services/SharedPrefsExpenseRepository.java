package com.example.expense_tracking_app.services;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.expense_tracking_app.models.Expense;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class SharedPrefsExpenseRepository implements ExpenseRepository {
    private static final String EXPENSES_KEY = "SHARED_PREF_EXPENSES";
    private static final String EXPENSES_DEFAULT = "";
    private static final Type expensesListType = new TypeToken<List<Expense>>() {
    }.getType();

    private final SharedPreferences _sharedPreferences;
    private final Gson _gson;

    @Inject
    public SharedPrefsExpenseRepository(SharedPreferences sharedPreferences, Gson gson) {
        _sharedPreferences = sharedPreferences;
        _gson = gson;
    }

    @Override
    public List<Expense> getAll() {
        String expensesJsonString = _sharedPreferences.getString(EXPENSES_KEY, EXPENSES_DEFAULT);
        if (!expensesJsonString.equals(EXPENSES_DEFAULT)) {
            return _gson.fromJson(expensesJsonString, expensesListType);
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

        writeToSharedPreferences(expenses);
    }

    @Override
    public void addRange(List<Expense> expenses) {
        List<Expense> _expenses = getAll();

        int id = getNewId(_expenses);
        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);
            expense.setId(id);
            _expenses.add(expense);
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

        Expense old = null;
        for (int i = 0; i < expenses.size(); i++) {
            Expense _expense = expenses.get(i);
            if (_expense.getId() == id) {
                expenses.set(i, expense);
                old = _expense;

                writeToSharedPreferences(expenses);
                return Optional.of(old);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Expense> remove(int id) {
        List<Expense> expenses = getAll();

        Expense old = null;
        for (int i = 0; i < expenses.size(); i++) {
            Expense _expense = expenses.get(i);
            if (_expense.getId() == id) {
                expenses.remove(i);
                old = _expense;

                writeToSharedPreferences(expenses);
                return Optional.of(old);
            }
        }

        return Optional.empty();
    }

    public boolean contains(int id) {
        return getAll().stream()
                .anyMatch(expense -> expense.getId() == id);
    }

    public void clear() {
        _sharedPreferences.edit()
                .remove(EXPENSES_KEY)
                .commit();
    }

    private void writeToSharedPreferences(List<Expense> expenses) {
        String expensesJsonString = _gson.toJson(expenses);
        _sharedPreferences.edit()
                .putString(EXPENSES_KEY, expensesJsonString)
                .commit();
    }
}
