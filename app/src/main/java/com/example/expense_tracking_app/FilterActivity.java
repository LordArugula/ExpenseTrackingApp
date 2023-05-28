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
        View view = binding.getRoot();
        setContentView(view);

        selectedCategories = new HashSet<>();

        fromDate = LocalDate.MIN;
        toDate = LocalDate.MAX;

        bindDateField(binding.fromDate, this::onSetFromDate);
        bindDateField(binding.toDate, this::onSetToDate);

        String[] categories = _expenseCategoryRepository.getAll();
        binding.categories.setSimpleItems(categories);
        binding.categories.setOnItemClickListener(this::onSelectCategory);

        binding.cancelButton.setOnClickListener(this::onClickCancelButton);
        binding.clearButton.setOnClickListener(this::onClickClearButton);
        binding.saveButton.setOnClickListener(this::onClickSaveButton);
    }

    private void onSelectCategory(AdapterView<?> adapterView, View view, int position, long l) {
        String category = (String) adapterView.getItemAtPosition(position);
        binding.categories.setText("");

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

    private void bindDateField(View dateField, DialogInterface.OnClickListener onClickListener) {
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
                binding.toDate.setText(toDate.format(dateFormat));
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
                toDate = LocalDate.MIN;
                binding.toDate.setText(toDate.format(dateFormat));
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