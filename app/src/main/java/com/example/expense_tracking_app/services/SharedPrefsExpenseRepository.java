package com.example.expense_tracking_app.services;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.expense_tracking_app.models.Expense;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class SharedPrefsExpenseRepository implements ExpenseRepository {
    private static final String EXPENSES_KEY = "SHARED_PREF_EXPENSES";
    private static final String EXPENSES_DEFAULT = "";
    private static final Type expensesListType = new TypeToken<List<Expense>>() {
    }.getType();

    private final SharedPreferences _sharedPreferences;
    private final Gson _gson;

    private final MutableLiveData<List<Expense>> allExpenses;

    @Inject
    public SharedPrefsExpenseRepository(SharedPreferences sharedPreferences, Gson gson) {
        _sharedPreferences = sharedPreferences;
        _gson = gson;

        String expensesJsonString = _sharedPreferences.getString(EXPENSES_KEY, EXPENSES_DEFAULT);
        List<Expense> expenses = !expensesJsonString.equals(EXPENSES_DEFAULT)
                ? _gson.fromJson(expensesJsonString, expensesListType)
                : new ArrayList<>();
        allExpenses = new MutableLiveData<>(expenses);
    }

    @Override
    public LiveData<Expense> getById(int id) {
        return allExpenses.getValue().stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .map(MutableLiveData::new)
                .orElse(new MutableLiveData<>(null));
    }

    @Override
    public LiveData<List<Expense>> getAll() {
        return allExpenses;
    }

    @Override
    public void insert(Expense expense) {
        List<Expense> expenses = allExpenses.getValue();

        if (expense.getId() == 0) {
            int id = expenses.stream().max(Comparator.comparingInt(Expense::getId))
                    .map(x -> x.getId() + 1)
                    .orElse(1);
            Expense insert = new Expense(id, expense.getName(), expense.getDate(), expense.getCost(), expense.getReason(), expense.getNotes(), expense.getCategory());
            expenses.add(insert);
        } else {
            expenses.add(expense);
        }

        String expensesJson = _gson.toJson(expenses);
        _sharedPreferences.edit()
                .putString(EXPENSES_KEY, expensesJson)
                .apply();

        allExpenses.postValue(expenses);
    }

    @Override
    public void update(Expense expense) {
        List<Expense> expenses = allExpenses.getValue();
        for (int i = 0; i < expenses.size(); i++) {
            Expense current = expenses.get(i);
            if (current.getId() == expense.getId()) {
                expenses.set(i, expense);

                String expensesJson = _gson.toJson(expenses);
                _sharedPreferences.edit()
                        .putString(EXPENSES_KEY, expensesJson)
                        .apply();
                allExpenses.postValue(expenses);
                return;
            }
        }
    }

    @Override
    public void deleteById(int id) {
        List<Expense> expenses = allExpenses.getValue();
        for (int i = 0; i < expenses.size(); i++) {
            Expense current = expenses.get(i);
            if (current.getId() == id) {
                expenses.remove(i);

                String expensesJson = _gson.toJson(expenses);
                _sharedPreferences.edit()
                        .putString(EXPENSES_KEY, expensesJson)
                        .apply();
                allExpenses.postValue(expenses);
                return;
            }
        }
    }

    @Override
    public void deleteAll() {
        List<Expense> expenses = allExpenses.getValue();
        expenses.clear();

        String expensesJson = _gson.toJson(expenses);
        _sharedPreferences.edit()
                .putString(EXPENSES_KEY, expensesJson)
                .apply();
        allExpenses.postValue(expenses);
    }

    @Override
    public LiveData<List<String>> getCategories() {
        return new MutableLiveData<>(allExpenses.getValue()
                .stream()
                .map(Expense::getCategory)
                .distinct()
                .collect(Collectors.toList()));
    }
}
