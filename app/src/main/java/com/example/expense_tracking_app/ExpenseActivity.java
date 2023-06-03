package com.example.expense_tracking_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expense_tracking_app.databinding.ActivityExpenseBinding;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.services.ExpenseCategoryRepository;
import com.example.expense_tracking_app.services.ExpenseRepository;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ExpenseActivity extends AppCompatActivity {
    public static final String EXTRA_EXPENSE = "EXTRA_EXPENSE";

    public static final String EDIT = "EXPENSE_EDIT";
    public static final int EDIT_OPTION_NEW = 0;
    public static final int EDIT_OPTION_EXISTING = 1;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    @Inject
    public ExpenseRepository _expenseRepository;

    @Inject
    public ExpenseCategoryRepository _expenseCategoryRepository;

    private ActivityExpenseBinding _binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityExpenseBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        Intent intent = getIntent();
        int editOption = intent.getIntExtra(EDIT, EDIT_OPTION_NEW);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        switch (editOption) {
            case EDIT_OPTION_EXISTING:
                supportActionBar.setTitle(R.string.expense_title_edit);

                Expense expense = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ? intent.getParcelableExtra(EXTRA_EXPENSE, Expense.class)
                        : intent.getParcelableExtra(EXTRA_EXPENSE);

                populateFormFrom(expense);
                break;
            case EDIT_OPTION_NEW:
            default:
                supportActionBar.setTitle(R.string.expense_title_create);

                populateFormWithNewExpense();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelChanges();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateFormWithNewExpense() {
        _binding.name.requestFocus();

        _binding.date.setText(LocalDate.now().format(dateFormat));
        initializeDatePicker(LocalDate.now());

        _binding.category.setText(R.string.expense_category_default);
        _binding.category.setSimpleItems(_expenseCategoryRepository.getAll());

        String currencyString = currencyFormat.format(0);
        _binding.currency.setText(currencyString.subSequence(0, 1));

        initializeButtons(null);
    }

    private void populateFormFrom(Expense expense) {
        _binding.name.setText(expense.getName());

        _binding.date.setText(expense.getDate().format(dateFormat));
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
        _binding.category.setSimpleItems(_expenseCategoryRepository.getAll());

        initializeButtons(expense);
    }

    private void initializeButtons(Expense expense) {
        if (expense == null) {
            _binding.deleteButton.setVisibility(View.INVISIBLE);
            _binding.cancelButton.setOnClickListener(view -> cancelChanges());
            _binding.saveButton.setOnClickListener(view -> saveChanges(-1));
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
        _expenseRepository.remove(id);

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
        String name = _binding.name.getText().toString();
        LocalDate date = LocalDate.parse(_binding.date.getText().toString(), dateFormat);

        double cost = 0;
        if (!_binding.cost.getText().toString().isEmpty()) {
            cost = Double.parseDouble(_binding.cost.getText().toString());
        }

        String category = _binding.category.getText().toString();
        String reason = _binding.reason.getText().toString();
        String notes = _binding.notes.getText().toString();

        boolean hasErrors = false;
        if (name.isEmpty()) {
            _binding.name.setError("Name is required.");
            hasErrors = true;
        }

        if (hasErrors) {
            return;
        }

        Expense expense = new Expense(name, date, cost, category, reason, notes);
        if (_expenseRepository.contains(id)) {
            _expenseRepository.update(id, expense);
        } else {
            _expenseRepository.add(expense);
        }

        _expenseCategoryRepository.add(category);

        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }
}