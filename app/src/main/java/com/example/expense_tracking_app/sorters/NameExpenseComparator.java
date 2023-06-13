package com.example.expense_tracking_app.sorters;

import com.example.expense_tracking_app.models.Expense;

import java.util.Comparator;

public class NameExpenseComparator implements ExpenseComparator {
    private int order;

    public NameExpenseComparator() {
        this(ExpenseComparator.ASCENDING);
    }

    public NameExpenseComparator(int order) {
        this.order = order;
    }

    @Override
    public Comparator<Expense> getComparator() {
        switch (order) {
            case ASCENDING:
                return Comparator.comparing(Expense::getName, String::compareToIgnoreCase).reversed();
            case DESCENDING:
            default:
                return Comparator.comparing(Expense::getName, String::compareToIgnoreCase);
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
