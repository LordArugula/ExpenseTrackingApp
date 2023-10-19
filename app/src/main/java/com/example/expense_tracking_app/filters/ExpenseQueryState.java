package com.example.expense_tracking_app.filters;

import com.example.expense_tracking_app.models.Expense;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class ExpenseQueryState {
    private final Set<String> categoryFilters;
    private LocalDate fromDateFilter;
    private LocalDate toDateFilter;

    private SortBy sortBy;

    public ExpenseQueryState() {
        categoryFilters = new HashSet<>();
        fromDateFilter = LocalDate.MIN;
        toDateFilter = LocalDate.MAX;
        sortBy = SortBy.Date;
    }

    public String[] getCategoryFilters() {
        return categoryFilters.toArray(new String[]{});
    }

    public void setCategoryFilters(Collection<String> categoryFilters) {
        this.categoryFilters.clear();
        this.categoryFilters.addAll(categoryFilters);
    }

    public LocalDate getFromDateFilter() {
        return fromDateFilter;
    }

    public void setFromDateFilter(LocalDate fromDateFilter) {
        this.fromDateFilter = fromDateFilter;
    }

    public LocalDate getToDateFilter() {
        return toDateFilter;
    }

    public void setToDateFilter(LocalDate toDateFilter) {
        this.toDateFilter = toDateFilter;
    }

    public void resetFilters() {
        categoryFilters.clear();
        fromDateFilter = LocalDate.MIN;
        toDateFilter = LocalDate.MAX;
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy;
    }

    public boolean match(Expense expense) {
        if (categoryFilters.size() > 0 && !categoryFilters.contains(expense.getCategory())) {
            return false;
        }

        if (fromDateFilter.isAfter(expense.getDate())) {
            return false;
        }

        if (toDateFilter.isBefore(expense.getDate())) {
            return false;
        }

        return true;
    }

    public Comparator<? super Expense> getComparator() {
        switch (sortBy) {
            case Name:
                return Comparator.comparing(Expense::getName, String::compareToIgnoreCase);
            case Cost:
                return Comparator.comparing(Expense::getCost);
            case Category:
                return Comparator.comparing(Expense::getCategory, String::compareToIgnoreCase);
            default:
            case Date:
                return Comparator.comparing(Expense::getDate)
                        .reversed();
        }
    }
}
