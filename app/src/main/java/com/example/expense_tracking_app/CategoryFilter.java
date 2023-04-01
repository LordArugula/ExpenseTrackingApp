package com.example.expense_tracking_app;

public class CategoryFilter implements ExpenseFilter {
    private String category;
    private boolean enabled;

    public CategoryFilter(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean matches(Expense expense) {
        if (!enabled) {
            return true;
        }
        return category.contentEquals(expense.getCategory());
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.isEmpty()) {
            return;
        }
        this.category = category;
        setEnabled(true);
    }
}
