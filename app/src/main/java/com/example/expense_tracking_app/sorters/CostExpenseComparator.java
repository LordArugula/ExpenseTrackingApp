package com.example.expense_tracking_app.sorters;

import com.example.expense_tracking_app.models.Expense;

import java.util.Comparator;

public class CostExpenseComparator implements ExpenseComparator {
    private int order;

    public CostExpenseComparator() {
        this(ExpenseComparator.ASCENDING);
    }

    public CostExpenseComparator(int order) {
        this.order = order;
    }

    @Override
    public Comparator<Expense> getComparator() {
        switch (order) {
            case ASCENDING:
                return Comparator.comparing(Expense::getCost).reversed();
            case DESCENDING:
            default:
                return Comparator.comparing(Expense::getCost);
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
