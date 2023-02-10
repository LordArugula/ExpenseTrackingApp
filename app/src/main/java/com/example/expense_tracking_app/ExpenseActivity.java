package com.example.expense_tracking_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.text.ParseException;

public class ExpenseActivity extends AppCompatActivity {
    private static final String TAG = ExpenseActivity.class.getSimpleName();
    private static final int NEW_EXPENSE = -1;

    private EditText nameText;
    private EditText dateText;
    private EditText costText;
    private TextView costSymbolText;
    private EditText categoryText;
    private EditText reasonText;
    private EditText notesText;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        nameText = findViewById(R.id.expense_name);
        dateText = findViewById(R.id.expense_date);
        costText = findViewById(R.id.expense_cost);
        costSymbolText = findViewById(R.id.expense_cost_symbol);
        categoryText = findViewById(R.id.expense_category);
        reasonText = findViewById(R.id.expense_reason);
        notesText = findViewById(R.id.expense_notes);

        Intent intent = getIntent();

        id = intent.getIntExtra(getString(R.string.EXTRA_EXPENSE_ID), NEW_EXPENSE);
        String name = intent.getStringExtra(getString(R.string.EXTRA_EXPENSE_NAME));
        String date = intent.getStringExtra(getString(R.string.EXTRA_EXPENSE_DATE));
        double cost = intent.getDoubleExtra(getString(R.string.EXTRA_EXPENSE_COST), 0);
        String category = intent.getStringExtra(getString(R.string.EXTRA_EXPENSE_CATEGORY));
        String reason = intent.getStringExtra(getString(R.string.EXTRA_EXPENSE_REASON));
        String notes = intent.getStringExtra(getString(R.string.EXTRA_EXPENSE_NOTES));

        nameText.setText(name);
        dateText.setText(date);
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String costString = format.format(cost);
        costText.setText(costString.substring(1));
        costSymbolText.setText(costString.substring(0, 1));
        categoryText.setText(category);
        reasonText.setText(reason);
        notesText.setText(notes);
    }

    public void cancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void save(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        if (nameText.getText() != null) {
            intent.putExtra(getString(R.string.EXTRA_EXPENSE_NAME), nameText.getText().toString());
        }
        if (dateText.getText() != null) {
            intent.putExtra(getString(R.string.EXTRA_EXPENSE_DATE), dateText.getText().toString());
        }

        if (costText.getText() != null && costSymbolText.getText() != null) {
            double cost = 0;
            NumberFormat format = NumberFormat.getCurrencyInstance();
            try {
                String costString = costSymbolText.getText().toString() + costText.getText().toString();
                cost = (long) format.parse(costString);
            } catch (ParseException | NullPointerException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            intent.putExtra(getString(R.string.EXTRA_EXPENSE_COST), cost);
        }

        if (categoryText.getText() != null) {
            intent.putExtra(getString(R.string.EXTRA_EXPENSE_CATEGORY), categoryText.getText().toString());
        }

        if (reasonText.getText() != null) {
            intent.putExtra(getString(R.string.EXTRA_EXPENSE_REASON), reasonText.getText().toString());
        }

        if (notesText.getText() != null) {
            intent.putExtra(getString(R.string.EXTRA_EXPENSE_NOTES), notesText.getText().toString());
        }

        intent.putExtra(getString(R.string.EXTRA_EXPENSE_ID), id);

        setResult(RESULT_OK, intent);
        finish();
    }
}