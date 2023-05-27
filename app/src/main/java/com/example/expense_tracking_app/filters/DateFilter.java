package com.example.expense_tracking_app.filters;

import com.example.expense_tracking_app.models.Expense;

import java.time.LocalDate;

public class DateFilter implements ExpenseFilter {
    private LocalDate start;
    private LocalDate end;

    public DateFilter() {
        this(LocalDate.MIN, LocalDate.MAX);
    }

    public DateFilter(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean matches(Expense expense) {
        LocalDate date = expense.getDate();
        return !(date.isBefore(start) || date.isAfter(end));
    }

    @Override
    public void clear() {
        start = LocalDate.MIN;
        end = LocalDate.MAX;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }
}
