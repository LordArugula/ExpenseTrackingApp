package com.example.expense_tracking_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String SHARED_PREF_FILE = "com.expense_tracking_app";
    public static final String CUSTOM_CATEGORIES = "com.expense_tracking_app.custom_categories";

    private static final String EXPENSES = "com.expense_tracking_app.expenses";

    private final ActivityResultLauncher<Intent> expenseActivityResultLauncher
            = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onExpenseActivityResult);

    private final ActivityResultLauncher<Intent> filterActivityResultLauncher
            = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onFilterActivityResult);

    private TextView totalText;
    private TextView averageText;

    private SharedPreferences preferences;

    /**
     * Contains all of the user's expenses.
     */
    private List<Expense> backingExpenses;
    /**
     * A subset of backingExpenses and contains the expenses that the user can view.
     */
    private List<Expense> viewExpenses;
    /**
     * Maps an expense from viewExpenses to its index in backingExpenses.
     */
    private Map<Expense, Integer> viewItemsToBackingItems;

    private ExpenseCategories expenseCategories;

    private ExpenseAdapter expenseAdapter;

    private DateFilter dateFilter;
    private CategoryFilter categoryFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalText = findViewById(R.id.expense_total_text);
        averageText = findViewById(R.id.expense_average_text);

        View expenseFilters = findViewById(R.id.expense_filters_button);
        expenseFilters.setOnClickListener(view -> launchFilterActivity());

        FloatingActionButton addExpenseButton = findViewById(R.id.add_expense_button);
        addExpenseButton.setOnClickListener(View -> createExpenseEntry());

        preferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);

        initializeFilters();
        initializeExpenseCategories();
        initializeExpenses();
        initializeExpenseRecyclerView();

//        addRandomExpenses(15);
    }

    private void addRandomExpenses(int count) {
        java.util.Random random = new java.util.Random(0);
        String[] names = new String[]{
                "Stuff", "Things", "Hello", "Foo", "Bar", "Java", "Android",
                "Cringe", "World", "Money", "Stinky", "Blue", "Red", "Green", "Yellow"
        };

        List<String> categories = expenseCategories.getCategories();
        for (int i = 0; i < count; i++) {
            String name = names[random.nextInt(names.length)];
            LocalDate date = LocalDate.of(2023, 1 + random.nextInt(12), 1 + random.nextInt(28));
            double cost = (double) random.nextInt(25000) / (double) 100;

            Expense expense = new Expense(name, date, cost, categories.get(random.nextInt(categories.size())));
            addExpense(expense);
        }
        writeExpensesToSharedPrefs();
    }

    private void initializeFilters() {
        this.dateFilter = new DateFilter(false);
        this.categoryFilter = new CategoryFilter(false);
    }

    /**
     * Initializes the expenses list RecyclerView.
     */
    private void initializeExpenseRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.expenses_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expenseAdapter);
    }

    /**
     * Loads expense entries from shared preferences.
     */
    private void initializeExpenses() {
        backingExpenses = new ArrayList<>();
        viewExpenses = new ArrayList<>();
        viewItemsToBackingItems = new HashMap<>();
        expenseAdapter = new ExpenseAdapter(viewExpenses, this::launchExpenseActivity);

        // load expenses from preferences
        String expensesJson = preferences.getString(EXPENSES, null);
        if (expensesJson != null) {
            List<Expense> expenses = JsonUtils.expensesFromJson(expensesJson);
            addExpenses(expenses);
            writeExpensesToSharedPrefs();
        }
    }

    /**
     * Loads expense categories from shared preferences.
     */
    private void initializeExpenseCategories() {
        expenseCategories = new ExpenseCategoriesBuilder()
                .withDefaultCategoriesFromResources(getResources())
                .withCategoriesFromSharedPrefs(preferences, CUSTOM_CATEGORIES)
                .build();
    }

    private void launchExpenseActivity(Expense expense, int index) {
        Intent intent = new Intent(this, ExpenseActivity.class);

        intent.putExtra(getString(R.string.EXTRA_EXPENSE_NAME), expense.getName());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_DATE), expense.getDate().toEpochDay());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_COST), expense.getCost());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_CATEGORY), expense.getCategory());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_REASON), expense.getReason());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_NOTES), expense.getNotes());
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_INDEX), index);

        expenseActivityResultLauncher.launch(intent);
    }

    private void onExpenseActivityResult(ActivityResult result) {
        Intent intent = result.getData();
        switch (result.getResultCode()) {
            case RESULT_OK:
                assert intent != null;
                boolean shouldDelete = intent.getBooleanExtra(getString(R.string.EXTRA_EXPENSE_DELETE), false);
                if (shouldDelete) {
                    int index = intent.getIntExtra(getString(R.string.EXTRA_EXPENSE_INDEX), ExpenseActivity.EXPENSE_ERROR);
                    removeExpense(index);
                    writeExpensesToSharedPrefs();
                } else {
                    insertOrUpdateExpense(intent);
                    writeExpensesToSharedPrefs();
                    writeCategoriesToSharedPrefs();
                }
                break;
            case RESULT_CANCELED:
                break;
        }
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

    @SuppressLint("NotifyDataSetChanged")
    private void onFilterActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Intent intent = result.getData();
            assert intent != null;

            boolean filterByDate = intent.getBooleanExtra(getString(R.string.EXTRA_FILTER_BY_DATE), false);
            boolean filterByCategory = intent.getBooleanExtra(getString(R.string.EXTRA_FILTER_BY_CATEGORY), false);

            if (filterByDate) {
                long dateStart = intent.getLongExtra(getString(R.string.EXTRA_FILTER_DATE_START), 0);
                long dateEnd = intent.getLongExtra(getString(R.string.EXTRA_FILTER_DATE_END), 0);
                dateFilter.setDateRange(LocalDate.ofEpochDay(dateStart), LocalDate.ofEpochDay(dateEnd));
            } else {
                dateFilter.setEnabled(false);
            }

            if (filterByCategory) {
                String category = intent.getStringExtra(getString(R.string.EXTRA_FILTER_CATEGORY));
                if (category.contentEquals(getString(R.string.filter_category_all))) {
                    categoryFilter.setEnabled(false);
                } else {
                    categoryFilter.setCategory(category);
                }
            } else {
                categoryFilter.setEnabled(false);
            }

            viewExpenses.clear();
            for (int i = 0; i < backingExpenses.size(); i++) {
                Expense expense = backingExpenses.get(i);
                if (matchesFilters(expense)) {
                    viewExpenses.add(expense);
                }
            }

            viewExpenses.sort(Expense::compareTo);
            expenseAdapter.notifyDataSetChanged();
            updateSummary();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addExpenses(List<Expense> expenses) {
        boolean isViewDirty = false;
        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);

            // add to backing list
            backingExpenses.add(expense);
            expenseCategories.addCategory(expense.getCategory());

            // add to view list
            if (matchesFilters(expense)) {
                viewExpenses.add(expense);
                viewItemsToBackingItems.put(expense, i);

                isViewDirty = true;
            }
        }

        // update view if necessary
        if (isViewDirty) {
            viewExpenses.sort(Expense::compareTo);

            expenseAdapter.notifyDataSetChanged();
            updateSummary();
        }
    }

    /**
     * Adds an expense.
     *
     * @param expense The expense.
     */
    private void addExpense(Expense expense) {
        int index = backingExpenses.size();

        // add to backing list
        backingExpenses.add(expense);
        expenseCategories.addCategory(expense.getCategory());

        // add to view list
        if (matchesFilters(expense)) {
            viewItemsToBackingItems.put(expense, index);

            viewExpenses.add(expense);
            viewExpenses.sort(Expense::compareTo);

            // find index of inserted expense to update adapter
            for (int insertionIndex = 0; insertionIndex < viewExpenses.size(); insertionIndex++) {
                Expense _expense = viewExpenses.get(insertionIndex);
                if (expense.equals(_expense)) {
                    expenseAdapter.notifyItemInserted(insertionIndex);
                    break;
                }
            }

            updateSummary();
        }
    }

    /**
     * Removes an expense at the given index.
     *
     * @param index the index of the expense to remove.
     */
    private void removeExpense(int index) {
        // remove from view items
        Expense removedExpense = viewExpenses.remove(index);

        // remove from backing items
        int backingIndex = viewItemsToBackingItems.get(removedExpense);
        backingExpenses.remove(backingIndex);
        viewItemsToBackingItems.remove(removedExpense);

        // update indices
        for (int i = backingIndex; i < backingExpenses.size(); i++) {
            Expense expense = backingExpenses.get(i);
            viewItemsToBackingItems.replace(expense, i);
        }

        // update view
        expenseAdapter.notifyItemRemoved(index);
        updateSummary();
    }

    /**
     * Updates the expense at the index with the given expense.
     *
     * @param index   the index of the expense to update.
     * @param expense the updated values for the expense.
     */
    private void updateExpense(int index, Expense expense) {
        Expense oldExpense = viewExpenses.get(index);

        // the expense no longer matches the filter and should not be viewed
        if (!matchesFilters(expense)) {
            // remove it from the view items
            viewExpenses.remove(oldExpense);
            // update backing items
            backingExpenses.set(index, expense);

            expenseAdapter.notifyItemRemoved(index);
        } else {
            // update view items
            viewExpenses.set(index, expense);
            viewExpenses.sort(Expense::compareTo);

            // update backing items
            int backingIndex = viewItemsToBackingItems.remove(oldExpense);
            backingExpenses.set(backingIndex, expense);
            viewItemsToBackingItems.put(expense, index);

            // update view
            for (int i = 0; i < viewExpenses.size(); i++) {
                Expense _expense = viewExpenses.get(i);
                if (expense.equals(_expense)) {
                    // update position in view
                    expenseAdapter.notifyItemMoved(index, i);
                    // update item view
                    expenseAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }

        updateSummary();
    }

    /**
     * Updates the total and average cost text fields.
     */
    private void updateSummary() {
        double total = viewExpenses.stream()
                .mapToDouble(Expense::getCost)
                .sum();

        NumberFormat format = NumberFormat.getCurrencyInstance();
        totalText.setText(format.format(total));

        double average = viewExpenses.size() == 0 ? 0 : total / viewExpenses.size();
        averageText.setText(format.format(average));
    }

    private void createExpenseEntry() {
        Expense expense = new Expense("", LocalDate.now(), 0, expenseCategories.getDefaultCategory());
        launchExpenseActivity(expense, ExpenseActivity.EXPENSE_NEW);
    }

    private void writeExpensesToSharedPrefs() {
        SharedPreferences.Editor editor = preferences.edit();
        String expensesJson = JsonUtils.expensesToJson(backingExpenses);
        editor.putString(EXPENSES, expensesJson);
        editor.apply();
    }

    private void writeCategoriesToSharedPrefs() {
        SharedPreferences.Editor editor = preferences.edit();
        String customCategoriesJson = JsonUtils.categoriesToJson(expenseCategories.getCustomCategories());
        editor.putString(EXPENSES, customCategoriesJson);
        editor.apply();
        editor.putString(CUSTOM_CATEGORIES, customCategoriesJson);
    }

    private boolean matchesFilters(Expense expense) {
        return dateFilter.matches(expense) && categoryFilter.matches(expense);
    }

    private void insertOrUpdateExpense(Intent data) {
        int index = data.getIntExtra(getString(R.string.EXTRA_EXPENSE_INDEX), ExpenseActivity.EXPENSE_ERROR);
        assert index != ExpenseActivity.EXPENSE_ERROR;

        String name = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_NAME));
        long dateLong = data.getLongExtra(getString(R.string.EXTRA_EXPENSE_DATE), 0);
        double cost = data.getDoubleExtra(getString(R.string.EXTRA_EXPENSE_COST), 0);
        String category = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_CATEGORY));
        String reason = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_REASON));
        String notes = data.getStringExtra(getString(R.string.EXTRA_EXPENSE_NOTES));

        Expense expense = new Expense(name, LocalDate.ofEpochDay(dateLong), cost, category);
        expense.setReason(reason);
        expense.setNotes(notes);

        if (index == ExpenseActivity.EXPENSE_NEW) {
            addExpense(expense);
            writeExpensesToSharedPrefs();
            writeCategoriesToSharedPrefs();
        } else {
            updateExpense(index, expense);
            writeExpensesToSharedPrefs();
            writeCategoriesToSharedPrefs();
        }
    }

}