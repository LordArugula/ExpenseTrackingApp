package com.example.expense_tracking_app.sorters;

import com.example.expense_tracking_app.models.Expense;

import java.util.Comparator;

public interface ExpenseComparator {
    final int ASCENDING = 0;
    final int DESCENDING = 1;

    Comparator<Expense> getComparator();
    int getOrder();
    void setOrder(int order);
}
