package com.example.expense_tracking_app.services;

import androidx.lifecycle.LiveData;

import com.example.expense_tracking_app.models.Expense;

import java.util.List;

public interface ExpenseRepository {
    LiveData<Expense> getById(int id);

    LiveData<List<Expense>> getAll();

    void insert(Expense expense);

    void update(Expense expense);

    void deleteById(int id);

    void deleteAll();

    LiveData<List<String>> getCategories();
}
