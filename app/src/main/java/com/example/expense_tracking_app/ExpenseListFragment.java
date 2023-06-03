package com.example.expense_tracking_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense_tracking_app.adapters.ExpenseAdapter;
import com.example.expense_tracking_app.filters.CategoryFilter;
import com.example.expense_tracking_app.filters.DateFilter;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.services.ExpenseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A fragment representing a list of Expenses.
 */
@AndroidEntryPoint
public class ExpenseListFragment extends Fragment {

    @Inject
    public ExpenseRepository _expenseRepository;
    private ExpenseAdapter _expenseAdapter;

    private DateFilter _dateFilter;
    private CategoryFilter _categoryFilter;

    private final ActivityResultLauncher<Intent> expenseActivityResultLauncher
            = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onExpenseActivityResult);
    private final ActivityResultLauncher<Intent> filterActivityResultLauncher
            = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onFilterActivityResult);

    public ExpenseListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);

        // Set the adapter
        _expenseAdapter = new ExpenseAdapter(this::onClickExpenseItem);
        _expenseAdapter.submitList(_expenseRepository.getAll());

        _dateFilter = new DateFilter();
        _categoryFilter = new CategoryFilter();

        RecyclerView recyclerView = view.findViewById(R.id.expenses_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(_expenseAdapter);

        ImageButton addButton = view.findViewById(R.id.add_expense_fab);
        addButton.setOnClickListener(this::onClickAddExpenseButton);

        return view;
    }

    private void onExpenseActivityResult(@NonNull ActivityResult result) {
        int resultCode = result.getResultCode();

        switch (resultCode) {
            case Activity.RESULT_OK:
                List<Expense> expenses = _expenseRepository.getAll().stream()
                        .filter(expense -> _dateFilter.matches(expense))
                        .filter(expense -> _categoryFilter.matches(expense))
                        .collect(Collectors.toList());

                _expenseAdapter.submitList(expenses);
                break;
            case Activity.RESULT_CANCELED:
            default:
                break;
        }
    }

    private void onFilterActivityResult(@NonNull ActivityResult result) {
        int resultCode = result.getResultCode();

        switch (resultCode) {
            case Activity.RESULT_OK:
                Intent intent = result.getData();
                long fromEpochDay = intent.getLongExtra(getString(R.string.EXTRA_FILTER_FROM_DATE), LocalDate.MIN.toEpochDay());
                LocalDate from = LocalDate.ofEpochDay(fromEpochDay);
                _dateFilter.setStart(from);

                long toEpochDay = intent.getLongExtra(getString(R.string.EXTRA_FILTER_TO_DATE), LocalDate.MAX.toEpochDay());
                LocalDate to = LocalDate.ofEpochDay(toEpochDay);
                _dateFilter.setEnd(to);

                String[] categories = intent.getStringArrayExtra(getString(R.string.EXTRA_FILTER_CATEGORIES));
                _categoryFilter.clear();
                _categoryFilter.includeCategories(categories);

                List<Expense> expenses = _expenseRepository.getAll().stream()
                        .filter(expense -> _dateFilter.matches(expense))
                        .filter(expense -> _categoryFilter.matches(expense))
                        .collect(Collectors.toList());
                _expenseAdapter.submitList(expenses);
                break;
            case Activity.RESULT_CANCELED:
            default:
                break;
        }
    }

    private void onClickAddExpenseButton(View view) {
        Intent intent = new Intent(this.getContext(), ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.EDIT, ExpenseActivity.EDIT_OPTION_NEW);
        expenseActivityResultLauncher.launch(intent);
    }

    private void onClickExpenseItem(Expense expense, int position) {
        Intent intent = new Intent(this.getContext(), ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.EDIT, ExpenseActivity.EDIT_OPTION_EXISTING);
        intent.putExtra(ExpenseActivity.EXTRA_EXPENSE, expense);
        expenseActivityResultLauncher.launch(intent);
    }
}
