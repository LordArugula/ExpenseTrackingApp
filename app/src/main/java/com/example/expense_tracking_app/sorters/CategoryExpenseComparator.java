package com.example.expense_tracking_app.sorters;

import com.example.expense_tracking_app.models.Expense;

import java.util.Comparator;

public class CategoryExpenseComparator implements ExpenseComparator {
    private int order;

    public CategoryExpenseComparator() {
        this(ExpenseComparator.ASCENDING);
    }

    public CategoryExpenseComparator(int order) {
        this.order = order;
    }

    @Override
    public Comparator<Expense> getComparator() {
        switch (order) {
            case ASCENDING:
                return Comparator.comparing(Expense::getCategory, String::compareToIgnoreCase).reversed();
            case DESCENDING:
            default:
                return Comparator.comparing(Expense::getCategory, String::compareToIgnoreCase);
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
