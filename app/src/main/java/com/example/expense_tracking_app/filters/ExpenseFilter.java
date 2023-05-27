package com.example.expense_tracking_app.filters;

import com.example.expense_tracking_app.models.Expense;

public interface ExpenseFilter {
    boolean matches(Expense expense);
    void clear();
}
