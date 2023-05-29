package com.example.expense_tracking_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expense_tracking_app.adapters.ExpenseAdapter;
import com.example.expense_tracking_app.databinding.ActivityMainBinding;
import com.example.expense_tracking_app.filters.CategoryFilter;
import com.example.expense_tracking_app.filters.DateFilter;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.services.ExpenseCategoryRepository;
import com.example.expense_tracking_app.services.ExpenseRepository;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private final ActivityResultLauncher<Intent> expenseActivityResultLauncher
            = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onExpenseActivityResult);
    private final ActivityResultLauncher<Intent> filterActivityResultLauncher
            = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onFilterActivityResult);

    @Inject
    public ExpenseRepository _expenseRepository;

    @Inject
    public ExpenseCategoryRepository _expenseCategoryRepository;

    private ExpenseAdapter _expenseAdapter;

    private ActivityMainBinding binding;

    private DateFilter dateFilter;
    private CategoryFilter categoryFilter;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dateFilter = new DateFilter();
        categoryFilter = new CategoryFilter();

        List<Expense> expenses = _expenseRepository.getAll();
        _expenseAdapter = new ExpenseAdapter(this::onSelectExpense);
        _expenseAdapter.submitList(expenses);

        updateStatistics(expenses);

        binding.expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.expensesRecyclerView.setAdapter(_expenseAdapter);

        binding.addExpenseButton.setOnClickListener(View -> onClickCreateExpense());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                launchFilterActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addRandomExpenses(int count) {
        java.util.Random random = new java.util.Random(0);
        String[] names = new String[]{
                "Stuff", "Things", "Hello", "Foo", "Bar", "Java", "Android",
                "Cringe", "World", "Money", "Stinky", "Blue", "Red", "Green", "Yellow"
        };

        String[] categories = _expenseCategoryRepository.getAll();
        List<Expense> expenses = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String name = names[random.nextInt(names.length)];
            LocalDate date = LocalDate.of(2023, 1 + random.nextInt(12), 1 + random.nextInt(28));
            double cost = (double) random.nextInt(25000) / (double) 100;

            Expense expense = new Expense(name, date, cost, categories[random.nextInt(categories.length)], "", "");
            expenses.add(expense);
        }
        _expenseRepository.addRange(expenses);
    }

    private void onSelectExpense(Expense expense, int position) {
        Intent intent = new Intent(this, ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.EXTRA_EDIT_OPTION, ExpenseActivity.EDIT_OPTION_EXISTING);
        intent.putExtra(ExpenseActivity.EXTRA_EXPENSE, expense);
        expenseActivityResultLauncher.launch(intent);
    }

    private void onClickCreateExpense() {
        Intent intent = new Intent(this, ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.EXTRA_EDIT_OPTION, ExpenseActivity.EDIT_OPTION_NEW);
        expenseActivityResultLauncher.launch(intent);
    }

    private void launchFilterActivity() {
        Intent intent = new Intent(this, FilterActivity.class);

        intent.putExtra(getString(R.string.EXTRA_FILTER_FROM_DATE), dateFilter.getStart().toEpochDay());
        intent.putExtra(getString(R.string.EXTRA_FILTER_TO_DATE), dateFilter.getEnd().toEpochDay());
        intent.putExtra(getString(R.string.EXTRA_FILTER_CATEGORIES), categoryFilter.getCategories());

        filterActivityResultLauncher.launch(intent);
    }

    private void onExpenseActivityResult(@NonNull ActivityResult result) {
        int resultCode = result.getResultCode();

        switch (resultCode) {
            case RESULT_OK:
                List<Expense> expenses = _expenseRepository.getAll().stream()
                        .filter(expense -> dateFilter.matches(expense))
                        .filter(expense -> categoryFilter.matches(expense))
                        .collect(Collectors.toList());

                _expenseAdapter.submitList(expenses);
                updateStatistics(expenses);
                break;
            case RESULT_CANCELED:
            default:
                break;
        }
    }

    private void onFilterActivityResult(@NonNull ActivityResult result) {
        int resultCode = result.getResultCode();

        switch (resultCode) {
            case RESULT_OK:
                Intent intent = result.getData();
                long fromEpochDay = intent.getLongExtra(getString(R.string.EXTRA_FILTER_FROM_DATE), LocalDate.MIN.toEpochDay());
                LocalDate from = LocalDate.ofEpochDay(fromEpochDay);
                dateFilter.setStart(from);

                long toEpochDay = intent.getLongExtra(getString(R.string.EXTRA_FILTER_TO_DATE), LocalDate.MAX.toEpochDay());
                LocalDate to = LocalDate.ofEpochDay(toEpochDay);
                dateFilter.setEnd(to);

                String[] categories = intent.getStringArrayExtra(getString(R.string.EXTRA_FILTER_CATEGORIES));
                categoryFilter.clear();
                categoryFilter.includeCategories(categories);

                List<Expense> expenses = _expenseRepository.getAll().stream()
                        .filter(expense -> dateFilter.matches(expense))
                        .filter(expense -> categoryFilter.matches(expense))
                        .collect(Collectors.toList());
                _expenseAdapter.submitList(expenses);
                updateStatistics(expenses);
                break;
            case RESULT_CANCELED:
            default:
                break;
        }
    }

    private void updateStatistics(List<Expense> expenses) {
        double total = expenses
                .stream()
                .mapToDouble(Expense::getCost)
                .sum();
        double average = expenses.size() == 0 ? 0 : total / expenses.size();

        binding.total.setText(currencyFormat.format(total));
        binding.average.setText(currencyFormat.format(average));
    }
}
