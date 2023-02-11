package com.example.expense_tracking_app;

public interface ExpenseFilter {
    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean filter(Expense expense);
}
