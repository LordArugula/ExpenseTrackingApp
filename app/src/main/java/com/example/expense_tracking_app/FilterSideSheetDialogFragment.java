package com.example.expense_tracking_app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expense_tracking_app.databinding.FilterSideSheetBinding;
import com.example.expense_tracking_app.filters.CategoryFilter;
import com.example.expense_tracking_app.filters.DateFilter;
import com.example.expense_tracking_app.viewmodels.ExpenseListViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.sidesheet.SideSheetDialog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Set;
import java.util.TreeSet;

public class FilterSideSheetDialogFragment extends DialogFragment {
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    private ExpenseListViewModel expenseListViewModel;

    private FilterSideSheetBinding binding;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Set<String> selectedCategories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_side_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FilterSideSheetBinding.bind(view);

        expenseListViewModel = new ViewModelProvider(getActivity())
                .get(ExpenseListViewModel.class);

        Toolbar toolbar = binding.toolbar;
        toolbar.setTitle(R.string.filter_title);
        toolbar.inflateMenu(R.menu.side_sheet_menu);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        DateFilter dateFilter = expenseListViewModel.getDateFilter().getValue();
        fromDate = dateFilter.getStart();
        toDate = dateFilter.getEnd();

        bindDateField(binding.fromDate, fromDate, this::onSetFromDate);
        bindDateField(binding.toDate, toDate, this::onSetToDate);

        String[] allCategories = expenseListViewModel.getCategories();
        binding.categories.setSimpleItems(allCategories);
        binding.categories.setOnItemClickListener(this::onCategorySelected);

        CategoryFilter categoryFilter = expenseListViewModel.getCategoryFilter().getValue();
        String[] categories = categoryFilter.getCategories();
        selectedCategories = new TreeSet<>();
        for (String category : categories) {
            selectCategory(category);
        }
    }

    private boolean onMenuItemClicked(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_close:
                dismiss();
                return true;
            default:
                return false;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new SideSheetDialog(getContext());
    }

    private void bindDateField(EditText dateField, LocalDate date, DialogInterface.OnClickListener onClickListener) {
        if (date.equals(LocalDate.MIN) || date.equals(LocalDate.MAX)) {
            dateField.setText("");
        } else {
            dateField.setText(date.format(dateFormat));
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, getString(android.R.string.ok), onClickListener);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel), onClickListener);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, getString(R.string.clear), onClickListener);

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
                return;
        }

        if (fromDate.isAfter(toDate)) {
            binding.fromDate.setError("This date cannot be after the second date.");
        } else {
            binding.fromDate.setError(null);
            binding.toDate.setError(null);
            binding.fromDate.clearFocus();

            expenseListViewModel.setDateFilter(fromDate, toDate);
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
                return;
        }

        if (fromDate.isAfter(toDate)) {
            binding.toDate.setError("This date cannot be before the first date.");
        } else {
            binding.fromDate.setError(null);
            binding.toDate.setError(null);
            binding.toDate.clearFocus();

            expenseListViewModel.setDateFilter(fromDate, toDate);
        }
    }

    private LocalDate getDateFromDatePicker(DatePickerDialog datePickerDialog) {
        DatePicker datePicker = datePickerDialog.getDatePicker();
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        return LocalDate.of(year, month + 1, day);
    }

    private void onCategorySelected(AdapterView<?> adapterView, View view, int position, long l) {
        String category = (String) adapterView.getItemAtPosition(position);
        binding.categories.setText("");

        selectCategory(category);
    }

    private void selectCategory(String category) {
        if (!selectedCategories.add(category)) {
            return;
        }

        Chip chip = new Chip(getContext());
        chip.setText(category);
        chip.setCloseIconVisible(true);

        chip.setOnCloseIconClickListener(this::onRemoveCategory);
        binding.categoryChipGroup.addView(chip);
        expenseListViewModel.setCategoryFilter(selectedCategories);
    }

    private void onRemoveCategory(View view) {
        binding.categoryChipGroup.removeView(view);
        Chip chipView = (Chip) view;
        selectedCategories.remove(chipView.getText().toString());
        expenseListViewModel.setCategoryFilter(selectedCategories);
    }
}
