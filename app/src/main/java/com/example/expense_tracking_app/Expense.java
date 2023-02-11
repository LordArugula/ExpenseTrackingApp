package com.example.expense_tracking_app;

import java.time.LocalDate;

public class Expense {
    private String name;
    private LocalDate date;
    private double cost;
    private String reason;
    private String notes;
    private String category;

    public Expense(String name, LocalDate date, double cost) {
        this.name = name;
        this.date = date;
        this.cost = cost;
    }

    public Expense(String name, LocalDate date, double cost, String category) {
        this(name, date, cost);
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
