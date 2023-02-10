package com.example.expense_tracking_app;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ExpenseAdapter expenseAdapter;

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

    private List<Expense> expenses;

    private ExpenseCategories expenseCategories;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        switch (result.getResultCode()) {
            case RESULT_OK:
                insertOrUpdateExpense(result.getData());
                break;
            case RESULT_CANCELED:
                // todo
                break;
        }
    });

    private void insertOrUpdateExpense(Intent data) {

        int id = data.getIntExtra(getString(R.string.EXTRA_EXPENSE_ID), -1);

        String name = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_NAME));
        String dateString = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_DATE));
        double cost = data.getDoubleExtra(getString(R.string.EXTRA_EXPENSE_COST), 0);
        String category = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_CATEGORY));
        String reason = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_REASON));
        String notes = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_NOTES));

        Date date = new Date();
        try {
            android.icu.text.DateFormat dateFormat = SimpleDateFormat.getDateInstance(android.icu.text.DateFormat.SHORT);
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        Expense expense = new Expense(name, date, cost, category);
        expense.setReason(reason);
        expense.setNotes(notes);

        if (id == -1) {
            id = expenses.size();
            expenses.add(expense);
            expenseAdapter.notifyItemInserted(id);
        } else {
            expenses.set(id, expense);
            expenseAdapter.notifyItemChanged(id);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expenses = new ArrayList<>();

        String[] expenseCategoriesArray = getResources().getStringArray(R.array.expense_categories);
        expenseCategories = new ExpenseCategories(expenseCategoriesArray, getString(R.string.expense_category_default));

        RecyclerView recyclerView = findViewById(R.id.expenses_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        expenseAdapter = new ExpenseAdapter(expenses, this::onItemClick);
        recyclerView.setAdapter(expenseAdapter);
    }

    public void onAddExpenseCallback(View view) {
        Expense expense = new Expense("", new Date(System.currentTimeMillis()), 0, expenseCategories.getDefault());
        launchExpenseActivity(expense, -1);
    }

    private void onItemClick(Expense expense, int position) {
        launchExpenseActivity(expense, position);
    }

    private void launchExpenseActivity(Expense expense, int id) {
        Intent intent = new Intent(this, ExpenseActivity.class);

        intent.putExtra(getString(R.string.EXTRA_EXPENSE_NAME), expense.getName());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_DATE), DateFormat.format("MM/dd/yyyy", expense.getDate()));
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_COST), expense.getCost());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_CATEGORY), expense.getCategory());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_REASON), expense.getReason());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_NOTES), expense.getNotes());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_ID), id);
        activityResultLauncher.launch(intent);
    }

}