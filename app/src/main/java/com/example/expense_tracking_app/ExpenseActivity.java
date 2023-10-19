package com.example.expense_tracking_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.expense_tracking_app.databinding.ActivityExpenseBinding;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.viewmodels.ExpenseViewModel;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ExpenseActivity extends AppCompatActivity {
    public static final String EXTRA_EXPENSE_ID = "EXTRA_EXPENSE_ID";

    public static final String EDIT = "EXPENSE_EDIT";
    public static final int EDIT_OPTION_NEW = 0;
    public static final int EDIT_OPTION_EXISTING = 1;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private ActivityExpenseBinding _binding;
    private ExpenseViewModel expenseViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityExpenseBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        Intent intent = getIntent();
        int editOption = intent.getIntExtra(EDIT, EDIT_OPTION_NEW);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        expenseViewModel = new ViewModelProvider(this)
                .get(ExpenseViewModel.class);

        switch (editOption) {
            case EDIT_OPTION_EXISTING:
                supportActionBar.setTitle(R.string.expense_title_edit);

                int expenseId = intent.getIntExtra(EXTRA_EXPENSE_ID, 0);

                expenseViewModel.getById(expenseId).observe(this, this::populateFormFromExpense);
                break;
            case EDIT_OPTION_NEW:
            default:
                supportActionBar.setTitle(R.string.expense_title_create);

                populateFormWithDefaultValues();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            cancelChanges();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateFormWithDefaultValues() {
        _binding.name.requestFocus();

        _binding.date.setText(LocalDate.now().format(dateFormat));
        initializeDatePicker(LocalDate.now());

        _binding.category.setText(R.string.expense_category_default);
        expenseViewModel.getCategories().observe(this, this::onCategoriesChanged);

        String currencyString = currencyFormat.format(0);
        _binding.currency.setText(currencyString.subSequence(0, 1));

        initializeButtons(null);
    }

    private void populateFormFromExpense(Expense expense) {
        if (expense == null) {
            populateFormWithDefaultValues();
            return;
        }
        _binding.name.setText(expense.getName());

        _binding.date.setText(dateFormat.format(expense.getDate()));
        initializeDatePicker(expense.getDate());

        double cost = expense.getCost();
        String currencyString = currencyFormat.format(cost);
        if (cost >= 0) {
            _binding.currency.setText(currencyString.subSequence(0, 1));
            _binding.cost.setText(currencyString.subSequence(1, currencyString.length()));
        } else {
            _binding.currency.setText(currencyString.subSequence(1, 2));
            String costText = String.format("%s%s", currencyString.charAt(0), currencyString.substring(2));
            _binding.cost.setText(costText);
        }
        _binding.reason.setText(expense.getReason());
        _binding.notes.setText(expense.getNotes());

        _binding.category.setText(expense.getCategory());
        expenseViewModel.getCategories().observe(this, this::onCategoriesChanged);
        initializeButtons(expense);
    }

    private void initializeButtons(Expense expense) {
        if (expense == null) {
            _binding.deleteButton.setVisibility(View.INVISIBLE);
            _binding.cancelButton.setOnClickListener(view -> cancelChanges());
            _binding.saveButton.setOnClickListener(view -> saveChanges(0));
        } else {
            _binding.deleteButton.setOnClickListener(view -> deleteExpense(expense.getId()));
            _binding.cancelButton.setOnClickListener(view -> cancelChanges());
            _binding.saveButton.setOnClickListener(view -> saveChanges(expense.getId()));
        }
    }

    /**
     * Creates the DatePickerDialog.
     */
    private void initializeDatePicker(LocalDate date) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);

        datePickerDialog.updateDate(date.getYear(), date.getMonthValue(), date.getMonthValue());
        datePickerDialog.setOnDateSetListener(this::onDateSet);

        _binding.date.setOnClickListener(view -> datePickerDialog.show());
        _binding.date.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                datePickerDialog.show();
            }
        });
    }

    private void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // the DatePicker gives months indexed starting at 0, so offset by one.
        LocalDate date = LocalDate.of(year, month + 1, day);
        _binding.date.setText(date.format(dateFormat));
    }

    /**
     * Deletes the expenses and returns to the MainActivity.
     */
    private void deleteExpense(int id) {
        expenseViewModel.deleteById(id);

        finish();
    }

    /**
     * Returns to the MainActivity without saving any changes.
     */
    private void cancelChanges() {
        finish();
    }

    /**
     * Saves any changes to the expense and returns to the MainActivity.
     */
    private void saveChanges(int id) {
        LocalDate date = LocalDate.parse(_binding.date.getText().toString(), dateFormat);

        double cost = 0;
        if (!TextUtils.isEmpty(_binding.cost.getText())) {
            try {
                Number parse = NumberFormat.getNumberInstance().parse(_binding.cost.getText().toString());
                cost = parse.doubleValue();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        String reason = _binding.reason.getText().toString();
        String notes = _binding.notes.getText().toString();

        boolean hasErrors = false;
        String name = _binding.name.getText().toString();
        if (TextUtils.isEmpty(_binding.name.getText())) {
            _binding.name.setError("Name is required.");
            hasErrors = true;
        }

        if (hasErrors) {
            return;
        }

        String category = _binding.category.getText().toString();
        if (TextUtils.isEmpty(_binding.category.getText())) {
            category = getString(R.string.expense_category_default);
        }

        Expense expense = new Expense(id, name, date, cost, reason, notes, category);
        if (id == 0) {
            expenseViewModel.insert(expense);
        } else {
            expenseViewModel.update(expense);
        }

        finish();
    }

    private void onCategoriesChanged(List<String> categories) {
        Set<String> defaultCategories = new TreeSet<>(Arrays.asList(getResources().getStringArray(R.array.expense_categories)));

        List<String> customCategories = new TreeSet<>(categories).stream()
                .filter(category -> !defaultCategories.contains(category))
                .collect(Collectors.toList());

        List<String> allCategories = new ArrayList<>(defaultCategories);
        allCategories.addAll(customCategories);

        _binding.category.setSimpleItems(allCategories.toArray(new String[]{}));
    }
}