package com.example.expense_tracking_app;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;

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
    @Inject
    public ExpenseCategoryRepository _expenseCategoryRepository;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    private LocalDate fromDate;
    private LocalDate toDate;
    private HashSet<String> selectedCategories;

    private ActivityFilterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent = getIntent();
        long fromDateEpochDay = intent.getLongExtra(getString(R.string.EXTRA_FILTER_FROM_DATE), LocalDate.MIN.toEpochDay());
        long toDateEpochDay = intent.getLongExtra(getString(R.string.EXTRA_FILTER_TO_DATE), LocalDate.MAX.toEpochDay());
        String[] filteredCategories = intent.getStringArrayExtra(getString(R.string.EXTRA_FILTER_CATEGORIES));

        fromDate = LocalDate.ofEpochDay(fromDateEpochDay);
        toDate = LocalDate.ofEpochDay(toDateEpochDay);
        this.selectedCategories = new HashSet<>();

        bindDateField(binding.fromDate, fromDate, this::onSetFromDate);
        bindDateField(binding.toDate, toDate, this::onSetToDate);
        if (filteredCategories != null) {
            for (String category : filteredCategories) {
                selectCategory(category);
            }
        }

        String[] categories = _expenseCategoryRepository.getAll();
        binding.categories.setSimpleItems(categories);
        binding.categories.setOnItemClickListener(this::onSelectCategory);

        binding.cancelButton.setOnClickListener(this::onClickCancelButton);
        binding.clearButton.setOnClickListener(this::onClickClearButton);
        binding.saveButton.setOnClickListener(this::onClickSaveButton);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putStringArray("SELECTED_CATEGORIES", selectedCategories.toArray(new String[0]));
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
        binding.categories.setText("");

        selectCategory(category);
    }

    private void selectCategory(String category) {
        if (selectedCategories.contains(category)) {
            return;
        }

        Chip chip = new Chip(FilterActivity.this);
        chip.setText(category);
        chip.setCloseIconVisible(true);
        selectedCategories.add(category);

        chip.setOnCloseIconClickListener(this::onRemoveCategory);
        binding.categoryChipGroup.addView(chip);
    }

    private void onRemoveCategory(View view) {
        binding.categoryChipGroup.removeView(view);
        Chip chipView = (Chip) view;
        selectedCategories.remove(chipView.getText().toString());
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
                fromDate = getDateFromDatePicker((DatePickerDialog) dialogInterface);
                binding.fromDate.setText(fromDate.format(dateFormat));
                break;
            case AlertDialog.BUTTON_NEUTRAL:
                fromDate = LocalDate.MIN;
                binding.fromDate.setText("");
                break;
            case AlertDialog.BUTTON_NEGATIVE:
            default:
                break;
        }

        if (fromDate.isAfter(toDate)) {
            binding.fromDate.setError("This date cannot be after the second date.");
        } else {
            binding.fromDate.setError(null);
            binding.toDate.setError(null);
            binding.fromDate.clearFocus();
        }
    }

    private void onSetToDate(DialogInterface dialogInterface, int button) {
        switch (button) {
            case AlertDialog.BUTTON_POSITIVE:
                toDate = getDateFromDatePicker((DatePickerDialog) dialogInterface);
                binding.toDate.setText(toDate.format(dateFormat));
                break;
            case AlertDialog.BUTTON_NEUTRAL:
                toDate = LocalDate.MAX;
                binding.toDate.setText("");
                break;
            case AlertDialog.BUTTON_NEGATIVE:
            default:
                break;
        }

        if (fromDate.isAfter(toDate)) {
            binding.toDate.setError("This date cannot be before the first date.");
        } else {
            binding.fromDate.setError(null);
            binding.toDate.setError(null);
            binding.toDate.clearFocus();
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
        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void onClickSaveButton(View view) {
        if (fromDate.isAfter(toDate)) {
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(getString(R.string.EXTRA_FILTER_FROM_DATE), fromDate.toEpochDay());
        intent.putExtra(getString(R.string.EXTRA_FILTER_TO_DATE), toDate.toEpochDay());
        String[] categories = selectedCategories.toArray(new String[0]);
        intent.putExtra(getString(R.string.EXTRA_FILTER_CATEGORIES), categories);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void onClickClearButton(View view) {
        fromDate = LocalDate.MIN;
        toDate = LocalDate.MAX;
        selectedCategories.clear();

        binding.categories.setText("");
        binding.categoryChipGroup.removeAllViews();
        binding.fromDate.setText("");
        binding.fromDate.setError(null);
        binding.toDate.setText("");
        binding.toDate.setError(null);
    }
}