package com.example.expense_tracking_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExpenseActivity extends AppCompatActivity {
    /**
     * The id used for new expense entries.
     */
    public static final int EXPENSE_NEW = -1;
    /**
     * The id used for an error.
     */
    public static final int EXPENSE_ERROR = -2;

    private static final String TAG = ExpenseActivity.class.getSimpleName();

    private EditText nameText;
    private TextView dateText;
    private EditText costText;
    private TextView costSymbolText;
    private AutoCompleteTextView categoryText;
    private EditText reasonText;
    private EditText notesText;
    private int expenseIndex;

    private ExpenseCategories expenseCategories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        nameText = findViewById(R.id.expense_name_edittext);

        dateText = findViewById(R.id.expense_date_edittext);
        initializeDatePicker();

        costText = findViewById(R.id.expense_cost_edittext);
        costSymbolText = findViewById(R.id.expense_cost_symbol);
        categoryText = findViewById(R.id.expense_category_edittext);
        reasonText = findViewById(R.id.expense_reason_edittext);
        notesText = findViewById(R.id.expense_notes_edittext);

        Intent intent = getIntent();
        expenseIndex = intent.getIntExtra(getString(R.string.EXTRA_EXPENSE_INDEX), -2);
        assert expenseIndex != EXPENSE_ERROR;

        populateFieldsFromIntent(intent);

        SharedPreferences preferences = getSharedPreferences(MainActivity.SHARED_PREF_FILE, MODE_PRIVATE);
        expenseCategories = new ExpenseCategoriesBuilder()
                .withDefaultCategoriesFromResources(getResources())
                .withCategoriesFromSharedPrefs(preferences, MainActivity.CUSTOM_CATEGORIES)
                .build();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, expenseCategories.getCategories());
        categoryText.setAdapter(adapter);

        Button deleteButton = findViewById(R.id.delete_expense_button);
        if (expenseIndex == EXPENSE_NEW) {
            deleteButton.setVisibility(View.INVISIBLE);
        }
        deleteButton.setOnClickListener(view -> deleteExpense());

        Button cancelButton = findViewById(R.id.cancel_expense_button);
        cancelButton.setOnClickListener(view -> cancelChanges());

        Button saveButton = findViewById(R.id.save_expense_button);
        saveButton.setOnClickListener(view -> saveChanges());
    }

    /**
     * Populates the activity's fields from data passed from the intent.
     */
    private void populateFieldsFromIntent(Intent intent) {
        String name = intent.getStringExtra(getString(R.string.EXTRA_EXPENSE_NAME));
        long date = intent.getLongExtra(getString(R.string.EXTRA_EXPENSE_DATE), 0);
        double cost = intent.getDoubleExtra(getString(R.string.EXTRA_EXPENSE_COST), 0);
        String category = intent.getStringExtra(getString(R.string.EXTRA_EXPENSE_CATEGORY));
        String reason = intent.getStringExtra(getString(R.string.EXTRA_EXPENSE_REASON));
        String notes = intent.getStringExtra(getString(R.string.EXTRA_EXPENSE_NOTES));

        nameText.setText(name);
        dateText.setText(LocalDate.ofEpochDay(date).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String costString = format.format(cost);
        costText.setText(costString.substring(1));
        costSymbolText.setText(costString.substring(0, 1));
        categoryText.setText(category);
        reasonText.setText(reason);
        notesText.setText(notes);
    }

    /**
     * Creates the DatePickerDialog.
     */
    private void initializeDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);

        datePickerDialog.setOnDateSetListener((datePicker, year, month, day) -> {
            // the DatePicker gives months indexed starting at 0, so offset by one.
            LocalDate date = LocalDate.of(year, month + 1, day);
            dateText.setText(date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        });

        dateText.setOnClickListener(view -> datePickerDialog.show());
        dateText.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                datePickerDialog.show();
            }
        });
    }

    /**
     * Deletes the expenses and returns to the MainActivity.
     */
    private void deleteExpense() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_DELETE), true);
        intent.putExtra(getString(R.string.EXTRA_EXPENSE_INDEX), expenseIndex);

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
    private void saveChanges() {
        Intent intent = new Intent(this, MainActivity.class);

        if (nameText.getText() != null) {
            intent.putExtra(getString(R.string.EXTRA_EXPENSE_NAME), nameText.getText().toString());
        }

        if (dateText.getText() != null) {
            LocalDate date = LocalDate.parse(dateText.getText().toString(), DateTimeFormatter.ofPattern(getString(R.string.date_format_mmddyyyy)));
            intent.putExtra(getString(R.string.EXTRA_EXPENSE_DATE), date.toEpochDay());
        }

        if (costText.getText() != null && costSymbolText.getText() != null) {
            double cost = 0;
            NumberFormat format = NumberFormat.getCurrencyInstance();
            try {
                String costString = costSymbolText.getText().toString() + costText.getText().toString();
                cost = format.parse(costString).doubleValue();
            } catch (ParseException | NullPointerException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            intent.putExtra(getString(R.string.EXTRA_EXPENSE_COST), cost);
        }

        if (categoryText.getText() != null) {
            intent.putExtra(getString(R.string.EXTRA_EXPENSE_CATEGORY), categoryText.getText().toString());
            expenseCategories.addCategory(categoryText.getText().toString());
        }

        if (reasonText.getText() != null) {
            intent.putExtra(getString(R.string.EXTRA_EXPENSE_REASON), reasonText.getText().toString());
        }

        if (notesText.getText() != null) {
            intent.putExtra(getString(R.string.EXTRA_EXPENSE_NOTES), notesText.getText().toString());
        }

        intent.putExtra(getString(R.string.EXTRA_EXPENSE_INDEX), expenseIndex);

        intent.putStringArrayListExtra(getString(R.string.EXTRA_EXPENSE_CUSTOM_CATEGORIES), expenseCategories.getCustomCategories());

        setResult(RESULT_OK, intent);
        finish();
    }
}