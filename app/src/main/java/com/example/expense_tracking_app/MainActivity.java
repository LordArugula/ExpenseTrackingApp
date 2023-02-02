package com.example.expense_tracking_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    /*
     * The user needs to be able to enter and track their expenses by day, month and year.
     * Tracking includes the ability to see totals and averages by day, month and year for each
     *  category of expense.
     * Totals for day, month and year also needs to be shown for all categories combined.
     * Each expense needs to be entered and details about each expense should be captured.
     * Each expense should include:
     *       Expense Date
     *       Name of Expense
     *       Category
     *       Cost
     *       Reason
     *       Notes
     * You need to have a way for the user to create a new entry, update an existing entry and
     *  delete an entry.
     * The user should be able to select from already existing categories stored.
     * There should also be a way to enter a new expense category.
     * Your app design needs to contain at least two different screens. More screens will probably
     *  be needed to space out the features appropriately.
     * There is no need to save and retrieve data in this app version. We will learn about data
     *  later in the class.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}