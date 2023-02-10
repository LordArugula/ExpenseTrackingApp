package com.example.expense_tracking_app;

import androidx.annotation.NonNull;

public class Cost {
    private int cents;

    public Cost(int cents) {
        this.cents = cents;
    }


    public void setValue(int cents) {
        this.cents = cents;
    }

    public int getCents() {
        return cents;
    }

    public int getDollars() {
        return cents / 100;
    }

    public int getCentsOnly() {
        return cents % 100;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%d.%d", getDollars(), getCentsOnly());
    }
}
