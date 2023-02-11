package com.example.expense_tracking_app;

import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ExpenseAdapter expenseAdapter;

    private View expenseFilters;

    private TextView totalText;
    private TextView averageText;

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
    private CategoryFilter categoryFilter;
    private DateFilter dateFilter;

    private final ActivityResultLauncher<Intent> expenseActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onExpenseActivityResult);

    private final ActivityResultLauncher<Intent> filterActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onFilterActivityResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expenses = new ArrayList<>();

        String[] expenseCategoriesArray = getResources().getStringArray(R.array.expense_categories);
        expenseCategories = new ExpenseCategories(expenseCategoriesArray, getString(R.string.expense_category_default));

        expenseFilters = findViewById(R.id.expense_filters);

        expenseFilters.setOnClickListener(view -> {
            launchFilterActivity();
//            View dialogView = getLayoutInflater().inflate(R.layout.expense_filter_dialog, null);
//            new MaterialAlertDialogBuilder(this)
//                    .setTitle(R.string.expense_filters)
//                    .setView(dialogView)
//                    .setPositiveButton("Save", (dialogInterface, i) -> {
//
//                        dialogView.findViewById(R.id.expense_)
//                        categoryFilter.setCategory("None");
//                        expenseAdapter.updateFilters();
//                        updateSummary();
//                    })
//                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
//                        // do nothing on cancel
//                    })
//                    .setNeutralButton("Clear", (dialogInterface, i) -> {
//                        categoryFilter.setEnabled(false);
//                        dateFilter.setEnabled(false);
//                        expenseAdapter.updateFilters();
//                        updateSummary();
//                    })
//                    .create()
//                    .show();
        });

        RecyclerView recyclerView = findViewById(R.id.expenses_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryFilter = new CategoryFilter();
        dateFilter = new DateFilter();
        expenseAdapter = new ExpenseAdapter(expenses, categoryFilter, dateFilter, this::onItemClick);
        recyclerView.setAdapter(expenseAdapter);

        totalText = findViewById(R.id.expense_total);
        averageText = findViewById(R.id.expense_average);
        updateSummary();
    }

    public void onAddExpenseCallback(View view) {
        Expense expense = new Expense("", LocalDate.now(), 0, expenseCategories.getDefault());
        launchExpenseActivity(expense, ExpenseActivity.EXPENSE_NEW);
    }

    private void onItemClick(Expense expense, int position) {
        launchExpenseActivity(expense, position);
    }

    private void launchExpenseActivity(Expense expense, int id) {
        Intent intent = new Intent(this, ExpenseActivity.class);

        intent.putExtra(getString(R.string.EXTRA_EXPENSE_NAME), expense.getName());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_DATE), expense.getDate().toEpochDay());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_COST), expense.getCost());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_CATEGORY), expense.getCategory());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_REASON), expense.getReason());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_NOTES), expense.getNotes());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_ID), id);

        intent.putStringArrayListExtra(getString(R.string.EXTRA_EXPENSE_CUSTOM_CATEGORIES), expenseCategories.getCustomCategories());

        expenseActivityResultLauncher.launch(intent);
    }

    private void onExpenseActivityResult(ActivityResult result) {
        switch (result.getResultCode()) {
            case RESULT_OK:
                assert result.getData() != null;
                insertOrUpdateExpense(result.getData());
                break;
            case RESULT_CANCELED:
                assert result.getData() != null;
                removeExpense(result.getData());
                break;
        }
    }

    private void insertOrUpdateExpense(Intent data) {
        int id = data.getIntExtra(getString(R.string.EXTRA_EXPENSE_ID), ExpenseActivity.EXPENSE_ERROR_ID);
        assert id != ExpenseActivity.EXPENSE_ERROR_ID;

        String name = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_NAME));
        long dateLong = data.getLongExtra(getString(R.string.EXTRA_EXPENSE_DATE), 0);
        double cost = data.getDoubleExtra(getString(R.string.EXTRA_EXPENSE_COST), 0);
        String category = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_CATEGORY));
        String reason = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_REASON));
        String notes = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_NOTES));

        ArrayList<String> customCategories = data.getStringArrayListExtra(getString(R.string.EXTRA_EXPENSE_CUSTOM_CATEGORIES));
        expenseCategories.addCategories(customCategories);

        Expense expense = new Expense(name, LocalDate.ofEpochDay(dateLong), cost, category);
        expense.setReason(reason);
        expense.setNotes(notes);

        if (id == ExpenseActivity.EXPENSE_NEW) {
            expenseAdapter.addItem(expense);
        } else {
            expenseAdapter.updateItem(id, expense);
        }
        updateSummary();
    }

    private void removeExpense(Intent data) {
        boolean delete = data.getBooleanExtra(getString(R.string.EXTRA_EXPENSE_DELETE), false);

        if (delete) {
            int id = data.getIntExtra(getString(R.string.EXTRA_EXPENSE_ID), ExpenseActivity.EXPENSE_ERROR_ID);
            assert id != ExpenseActivity.EXPENSE_ERROR_ID;

            expenseAdapter.removeItem(id);
            updateSummary();
        }
    }

    private void updateSummary() {
        double total = expenses.stream().mapToDouble(Expense::getCost).sum();
        NumberFormat format = NumberFormat.getCurrencyInstance();
        totalText.setText(format.format(total));

        double average;
        if (expenses.size() == 0) {
            average = 0;
        } else {
            average = total / expenses.size();
        }
        averageText.setText(format.format(average));
    }

    private void launchFilterActivity() {
        Intent intent = new Intent(this, FilterActivity.class);

        intent.putExtra(getString(R.string.EXTRA_FILTER_BY_DATE), dateFilter.isEnabled());
        if (dateFilter.isEnabled()) {
            intent.putExtra(getString(R.string.EXTRA_FILTER_DATE_START), dateFilter.getStartDate().toEpochDay());
            intent.putExtra(getString(R.string.EXTRA_FILTER_DATE_END), dateFilter.getEndDate().toEpochDay());
        }

        intent.putExtra(getString(R.string.EXTRA_FILTER_BY_CATEGORY), categoryFilter.isEnabled());
        if (categoryFilter.isEnabled()) {
            intent.putExtra(getString(R.string.EXTRA_FILTER_CATEGORY), categoryFilter.getCategory());
        }

        intent.putStringArrayListExtra(getString(R.string.EXTRA_EXPENSE_CUSTOM_CATEGORIES), expenseCategories.getCustomCategories());

        filterActivityResultLauncher.launch(intent);
    }

    private void onFilterActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            assert data != null;

            boolean filterByDate = data.getBooleanExtra(getString(R.string.EXTRA_FILTER_DATE_START), false);
            boolean filterByCategory = data.getBooleanExtra(getString(R.string.EXTRA_FILTER_BY_CATEGORY), false);

            if (filterByDate) {
                long dateStart = data.getLongExtra(getString(R.string.EXTRA_FILTER_DATE_START), 0);
                long dateEnd = data.getLongExtra(getString(R.string.EXTRA_FILTER_DATE_END), 0);
                dateFilter.setDateRange(LocalDate.ofEpochDay(dateStart), LocalDate.ofEpochDay(dateEnd));
            } else {
                dateFilter.setEnabled(false);
            }

            if (filterByCategory) {
                String category = data.getStringExtra(getString(R.string.EXTRA_FILTER_CATEGORY));
                categoryFilter.setCategory(category);
            } else {
                categoryFilter.setEnabled(false);
            }

            if (filterByDate || filterByCategory) {
                expenseAdapter.updateFilters();
                updateSummary();
            }
        }
    }
}