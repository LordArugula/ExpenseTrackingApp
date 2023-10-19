package com.example.expense_tracking_app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
import com.example.expense_tracking_app.filters.ExpenseQueryState;
import com.example.expense_tracking_app.viewmodels.ExpenseViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.sidesheet.SideSheetDialog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class FilterSideSheetDialogFragment extends DialogFragment {
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    private ExpenseViewModel expenseViewModel;

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

        expenseViewModel = new ViewModelProvider(getActivity())
                .get(ExpenseViewModel.class);

        Toolbar toolbar = binding.toolbar;
        toolbar.setTitle(R.string.filter_title);
        toolbar.inflateMenu(R.menu.side_sheet_menu);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        selectedCategories = new TreeSet<>();

        ExpenseQueryState queryState = expenseViewModel.getQueryState().getValue();

        fromDate = queryState.getFromDateFilter();
        toDate = queryState.getToDateFilter();

        bindDateField(binding.fromDate, fromDate, this::onSetFromDate);
        bindDateField(binding.toDate, toDate, this::onSetToDate);

        for (String category : queryState.getCategoryFilters()) {
            if (!selectedCategories.add(category)) {
                continue;
            }

            Chip chip = createCategoryChip(category);
            binding.categoryChipGroup.addView(chip);
        }

        expenseViewModel.getCategories().observe(getActivity(), this::onCategoriesChanged);
        binding.categories.setOnItemClickListener(this::onCategorySelected);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        expenseViewModel.getCategories().removeObserver(this::onCategoriesChanged);
    }

    @NonNull
    private Chip createCategoryChip(String category) {
        Chip chip = new Chip(getContext());
        chip.setText(category);
        chip.setCloseIconVisible(true);

        chip.setOnCloseIconClickListener(this::onRemoveCategory);
        return chip;
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

            ExpenseQueryState expenseQueryState = expenseViewModel.getQueryState().getValue();
            expenseQueryState.setFromDateFilter(fromDate);
            expenseQueryState.setToDateFilter(toDate);
            expenseViewModel.setQueryState(expenseQueryState);
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

            ExpenseQueryState expenseQueryState = expenseViewModel.getQueryState().getValue();
            expenseQueryState.setFromDateFilter(fromDate);
            expenseQueryState.setToDateFilter(toDate);
            expenseViewModel.setQueryState(expenseQueryState);
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

        if (!selectedCategories.add(category)) {
            return;
        }

        Chip chip = createCategoryChip(category);
        binding.categoryChipGroup.addView(chip);

        ExpenseQueryState queryState = expenseViewModel.getQueryState().getValue();
        queryState.setCategoryFilters(selectedCategories);
        expenseViewModel.setQueryState(queryState);
    }

    private void onRemoveCategory(View view) {
        binding.categoryChipGroup.removeView(view);
        Chip chipView = (Chip) view;
        selectedCategories.remove(chipView.getText().toString());

        ExpenseQueryState expenseQueryState = expenseViewModel.getQueryState().getValue();
        expenseQueryState.setCategoryFilters(selectedCategories);
        expenseViewModel.setQueryState(expenseQueryState);
    }

    private void onCategoriesChanged(List<String> categories) {
        Set<String> defaultCategories = new TreeSet<>(Arrays.asList(getResources().getStringArray(R.array.expense_categories)));

        List<String> customCategories = new TreeSet<>(categories).stream()
                .filter(category -> !defaultCategories.contains(category))
                .collect(Collectors.toList());

        List<String> allCategories = new ArrayList<>(defaultCategories);
        allCategories.addAll(customCategories);

        binding.categories.setSimpleItems(allCategories.toArray(new String[]{}));
    }
}
