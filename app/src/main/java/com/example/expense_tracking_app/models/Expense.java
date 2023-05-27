package com.example.expense_tracking_app.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDate;

public class Expense implements Parcelable, Comparable<Expense> {
    private int id;
    private String name;
    private LocalDate date;
    private double cost;
    private String reason;
    private String notes;
    private String category;

    public Expense(String name, LocalDate date, double cost, String category, String reason, String notes) {
        this.name = name;
        this.date = date;
        this.cost = cost;
        this.category = category;
        this.reason = reason;
        this.notes = notes;
    }

    protected Expense(@NonNull Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.date = LocalDate.ofEpochDay(in.readLong());
        this.cost = in.readDouble();
        this.reason = in.readString();
        this.notes = in.readString();
        this.category = in.readString();
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

    @Override
    public int compareTo(@NonNull Expense expense) {
        int comparison = date.compareTo(expense.getDate());
        if (comparison == 0) {
            return name.compareToIgnoreCase(expense.getName());
        }
        return comparison;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeLong(date.toEpochDay());
        parcel.writeDouble(cost);
        parcel.writeString(reason);
        parcel.writeString(notes);
        parcel.writeString(category);
    }

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @NonNull
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @NonNull
        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };
}
