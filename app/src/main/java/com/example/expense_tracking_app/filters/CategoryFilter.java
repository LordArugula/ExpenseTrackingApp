package com.example.expense_tracking_app.filters;

import com.example.expense_tracking_app.models.Expense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CategoryFilter implements ExpenseFilter {
    private final List<String> categories;

    public CategoryFilter() {
        categories = new ArrayList<>();
    }

    @Override
    public boolean matches(Expense expense) {
        if (categories.isEmpty()) {
            return true;
        }

        return categories.contains(expense.getCategory());
    }

    @Override
    public void clear() {
        categories.clear();
    }

    public void includeCategories(Collection<String> categories) {
        this.categories.addAll(categories);
    }

    public void includeCategories(String[] categories) {
        Collections.addAll(this.categories, categories);
    }

    public String[] getCategories() {
        return categories.toArray(new String[0]);
    }
}
