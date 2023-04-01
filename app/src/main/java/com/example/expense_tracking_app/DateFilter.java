package com.example.expense_tracking_app;

import java.time.LocalDate;

public class DateFilter implements ExpenseFilter {
    private LocalDate start;
    private LocalDate end;
    private boolean enabled;

    public DateFilter(boolean enabled) {
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

        LocalDate date = expense.getDate();
        return !date.isBefore(start) && !date.isAfter(end);
    }

    public LocalDate getStartDate() {
        return start;
    }

    public LocalDate getEndDate() {
        return end;
    }

    public void setDateRange(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
        setEnabled(true);
    }
}
