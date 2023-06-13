package com.example.expense_tracking_app.sorters;

import com.example.expense_tracking_app.models.Expense;

import java.util.Comparator;

public class DateExpenseComparator implements ExpenseComparator {
    private int order;

    public DateExpenseComparator() {
        this(ExpenseComparator.ASCENDING);
    }

    public DateExpenseComparator(int order) {
        this.order = order;
    }

    @Override
    public Comparator<Expense> getComparator() {
        switch (order) {
            case ASCENDING:
                return Comparator.comparing(Expense::getDate);
            case DESCENDING:
            default:
                return Comparator.comparing(Expense::getDate).reversed();
        }
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }
}
