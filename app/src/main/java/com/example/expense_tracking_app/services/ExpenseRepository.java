package com.example.expense_tracking_app.services;

import com.example.expense_tracking_app.models.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository {
    List<Expense> getAll();

    Optional<Expense> getById(int id);

    void add(Expense expense);

    void addRange(List<Expense> expenses);

    Optional<Expense> update(int id, Expense expense);

    Optional<Expense> remove(int id);

    void clear();

    boolean contains(int id);
}
