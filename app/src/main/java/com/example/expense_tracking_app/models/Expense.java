package com.example.expense_tracking_app.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "expenses_table")
public class Expense {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private LocalDate date;
    private double cost;
    private String reason;
    private String notes;
    private String category;

    public Expense(int id) {
        this.id = id;
    }

    @Ignore
    public Expense(int id, String name, LocalDate date, double cost, String reason, String notes, String category) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.cost = cost;
        this.reason = reason;
        this.notes = notes;
        this.category = category;
    }

    public int getId() {
        return id;
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
