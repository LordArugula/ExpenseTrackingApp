package com.example.expense_tracking_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expense_tracking_app.databinding.ActivityExpenseBinding;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.services.ExpenseCategoryRepository;
import com.example.expense_tracking_app.services.ExpenseRepository;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ExpenseActivity extends AppCompatActivity {
    public static final String EXTRA_EXPENSE = "EXTRA_EXPENSE";

    public static final String EXTRA_EDIT_OPTION = "EXPENSE_EDIT";
    public static final int EDIT_OPTION_NEW = 0;
    public static final int EDIT_OPTION_EXISTING = 1;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    @Inject
    public ExpenseRepository expenseRepository;

    @Inject
    public ExpenseCategoryRepository expenseCategoryRepository;

    private ActivityExpenseBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        int editOption = intent.getIntExtra(EXTRA_EDIT_OPTION, EDIT_OPTION_NEW);

        switch (editOption) {
            case EDIT_OPTION_EXISTING:
                Expense expense = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ? intent.getParcelableExtra(EXTRA_EXPENSE, Expense.class)
                        : intent.getParcelableExtra(EXTRA_EXPENSE);

                populateFormFrom(expense);
                break;
            case EDIT_OPTION_NEW:
            default:
                populateFormWithDefault();
                break;
        }
    }

    private void populateFormWithDefault() {
        binding.name.requestFocus();

        binding.date.setText(LocalDate.now().format(dateFormat));
        initializeDatePicker();

        binding.category.setText(R.string.expense_category_default);
        binding.category.setSimpleItems(expenseCategoryRepository.getAll());

        initializeButtons(null);
    }

    private void populateFormFrom(Expense expense) {
        binding.name.setText(expense.getName());

        binding.date.setText(expense.getDate().format(dateFormat));
        initializeDatePicker();

        String currencyString = currencyFormat.format(expense.getCost());
        binding.currency.setText(currencyString.subSequence(0, 1));
        binding.cost.setText(currencyString.subSequence(1, currencyString.length()));

        binding.reason.setText(expense.getReason());
        binding.notes.setText(expense.getNotes());

        binding.category.setText(expense.getCategory());
        binding.category.setSimpleItems(expenseCategoryRepository.getAll());

        initializeButtons(expense);
    }

    private void initializeButtons(Expense expense) {
        if (expense == null) {
            binding.deleteButton.setVisibility(View.INVISIBLE);
            binding.cancelButton.setOnClickListener(view -> cancelChanges());
            binding.saveButton.setOnClickListener(view -> saveChanges(-1));
        } else {
            binding.deleteButton.setOnClickListener(view -> deleteExpense(expense.getId()));
            binding.cancelButton.setOnClickListener(view -> cancelChanges());
            binding.saveButton.setOnClickListener(view -> saveChanges(expense.getId()));
        }
    }

    /**
     * Creates the DatePickerDialog.
     */
    private void initializeDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);

        datePickerDialog.setOnDateSetListener(this::onDateSet);

        binding.date.setOnClickListener(view -> datePickerDialog.show());
        binding.date.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                datePickerDialog.show();
            }
        });
    }

    private void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // the DatePicker gives months indexed starting at 0, so offset by one.
        LocalDate date = LocalDate.of(year, month + 1, day);
        binding.date.setText(date.format(dateFormat));
    }

    /**
     * Deletes the expenses and returns to the MainActivity.
     */
    private void deleteExpense(int id) {
        expenseRepository.remove(id);

        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Returns to the MainActivity without saving any changes.
     */
    private void cancelChanges() {
        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    /**
     * Saves any changes to the expense and returns to the MainActivity.
     */
    private void saveChanges(int id) {
        String name = binding.name.getText().toString();
        LocalDate date = LocalDate.parse(binding.date.getText().toString(), dateFormat);

        double cost = 0;
        if (!binding.cost.getText().toString().isEmpty()) {
            cost = Double.parseDouble(binding.cost.getText().toString());
        }

        String category = binding.category.getText().toString();
        String reason = binding.reason.getText().toString();
        String notes = binding.notes.getText().toString();

        boolean hasErrors = false;
        if (name.isEmpty()) {
            binding.name.setError("Name is required.");
            hasErrors = true;
        }

        if (hasErrors) {
            return;
        }

        Expense expense = new Expense(name, date, cost, category, reason, notes);
        if (expenseRepository.contains(id)) {
            expenseRepository.update(id, expense);
        } else {
            expenseRepository.add(expense);
        }

        expenseCategoryRepository.add(category);

        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }
}