package com.example.expense_tracking_app;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expense_tracking_app.databinding.ActivityFilterBinding;
import com.example.expense_tracking_app.services.ExpenseCategoryRepository;
import com.google.android.material.chip.Chip;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashSet;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FilterActivity extends AppCompatActivity {
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    @Inject
    public ExpenseCategoryRepository _expenseCategoryRepository;

    private LocalDate _fromDate;
    private LocalDate _toDate;
    private HashSet<String> _selectedCategories;

    private ActivityFilterBinding _binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityFilterBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setTitle(R.string.filter_title);

        Intent intent = getIntent();
        long fromDateEpochDay = intent.getLongExtra(getString(R.string.EXTRA_FILTER_FROM_DATE), LocalDate.MIN.toEpochDay());
        long toDateEpochDay = intent.getLongExtra(getString(R.string.EXTRA_FILTER_TO_DATE), LocalDate.MAX.toEpochDay());
        String[] filteredCategories = intent.getStringArrayExtra(getString(R.string.EXTRA_FILTER_CATEGORIES));

        _fromDate = LocalDate.ofEpochDay(fromDateEpochDay);
        _toDate = LocalDate.ofEpochDay(toDateEpochDay);
        this._selectedCategories = new HashSet<>();

        bindDateField(_binding.fromDate, _fromDate, this::onSetFromDate);
        bindDateField(_binding.toDate, _toDate, this::onSetToDate);
        if (filteredCategories != null) {
            for (String category : filteredCategories) {
                selectCategory(category);
            }
        }

        String[] categories = _expenseCategoryRepository.getAll();
        _binding.categories.setSimpleItems(categories);
        _binding.categories.setOnItemClickListener(this::onSelectCategory);

        _binding.cancelButton.setOnClickListener(this::onClickCancelButton);
        _binding.clearButton.setOnClickListener(this::onClickClearButton);
        _binding.saveButton.setOnClickListener(this::onClickSaveButton);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelFilterChanges();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putStringArray("SELECTED_CATEGORIES", _selectedCategories.toArray(new String[0]));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String[] categories = savedInstanceState.getStringArray("SELECTED_CATEGORIES");
        for (String category : categories) {
            selectCategory(category);
        }
    }

    private void onSelectCategory(AdapterView<?> adapterView, View view, int position, long l) {
        String category = (String) adapterView.getItemAtPosition(position);
        _binding.categories.setText("");

        selectCategory(category);
    }

    private void selectCategory(String category) {
        if (_selectedCategories.contains(category)) {
            return;
        }

        Chip chip = new Chip(FilterActivity.this);
        chip.setText(category);
        chip.setCloseIconVisible(true);
        _selectedCategories.add(category);

        chip.setOnCloseIconClickListener(this::onRemoveCategory);
        _binding.categoryChipGroup.addView(chip);
    }

    private void onRemoveCategory(View view) {
        _binding.categoryChipGroup.removeView(view);
        Chip chipView = (Chip) view;
        _selectedCategories.remove(chipView.getText().toString());
    }

    private void bindDateField(EditText dateField, LocalDate date, DialogInterface.OnClickListener onClickListener) {
        if (date.equals(LocalDate.MIN) || date.equals(LocalDate.MAX)) {
            dateField.setText("");
        } else {
            dateField.setText(date.format(dateFormat));
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(R.string.date_picker_dialog_positive), onClickListener);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.date_picker_dialog_negative), onClickListener);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, getString(R.string.date_picker_dialog_neutral), onClickListener);

        dateField.setOnClickListener(_view -> datePickerDialog.show());
        dateField.setOnFocusChangeListener((_view, hasFocus) -> {
            if (hasFocus) {
                datePickerDialog.show();
            }
        });
    }

    private void onSetFromDate(DialogInterface dialogInterface, int button) {
        switch (button) {
            case AlertDialog.BUTTON_POSITIVE:
                _fromDate = getDateFromDatePicker((DatePickerDialog) dialogInterface);
                _binding.fromDate.setText(_fromDate.format(dateFormat));
                break;
            case AlertDialog.BUTTON_NEUTRAL:
                _fromDate = LocalDate.MIN;
                _binding.fromDate.setText("");
                break;
            case AlertDialog.BUTTON_NEGATIVE:
            default:
                break;
        }

        if (_fromDate.isAfter(_toDate)) {
            _binding.fromDate.setError("This date cannot be after the second date.");
        } else {
            _binding.fromDate.setError(null);
            _binding.toDate.setError(null);
            _binding.fromDate.clearFocus();
        }
    }

    private void onSetToDate(DialogInterface dialogInterface, int button) {
        switch (button) {
            case AlertDialog.BUTTON_POSITIVE:
                _toDate = getDateFromDatePicker((DatePickerDialog) dialogInterface);
                _binding.toDate.setText(_toDate.format(dateFormat));
                break;
            case AlertDialog.BUTTON_NEUTRAL:
                _toDate = LocalDate.MAX;
                _binding.toDate.setText("");
                break;
            case AlertDialog.BUTTON_NEGATIVE:
            default:
                break;
        }

        if (_fromDate.isAfter(_toDate)) {
            _binding.toDate.setError("This date cannot be before the first date.");
        } else {
            _binding.fromDate.setError(null);
            _binding.toDate.setError(null);
            _binding.toDate.clearFocus();
        }
    }

    private LocalDate getDateFromDatePicker(DatePickerDialog datePickerDialog) {
        DatePicker datePicker = datePickerDialog.getDatePicker();
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        return LocalDate.of(year, month + 1, day);
    }

    private void onClickCancelButton(View view) {
        cancelFilterChanges();
    }

    private void cancelFilterChanges() {
        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void onClickSaveButton(View view) {
        if (_fromDate.isAfter(_toDate)) {
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(getString(R.string.EXTRA_FILTER_FROM_DATE), _fromDate.toEpochDay());
        intent.putExtra(getString(R.string.EXTRA_FILTER_TO_DATE), _toDate.toEpochDay());
        String[] categories = _selectedCategories.toArray(new String[0]);
        intent.putExtra(getString(R.string.EXTRA_FILTER_CATEGORIES), categories);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void onClickClearButton(View view) {
        _fromDate = LocalDate.MIN;
        _toDate = LocalDate.MAX;
        _selectedCategories.clear();

        _binding.categories.setText("");
        _binding.categoryChipGroup.removeAllViews();
        _binding.fromDate.setText("");
        _binding.fromDate.setError(null);
        _binding.toDate.setText("");
        _binding.toDate.setError(null);
    }
}