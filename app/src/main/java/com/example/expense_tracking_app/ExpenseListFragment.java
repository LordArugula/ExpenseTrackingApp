package com.example.expense_tracking_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense_tracking_app.adapters.ExpenseAdapter;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.viewmodels.ExpenseListViewModel;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A fragment representing a list of Expenses.
 */
@AndroidEntryPoint
public class ExpenseListFragment extends Fragment {

    private ExpenseListViewModel expenseListViewModel;

    private ExpenseAdapter _expenseAdapter;

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

        expenseListViewModel = new ViewModelProvider(getActivity())
                .get(ExpenseListViewModel.class);

        expenseListViewModel.getExpenses()
                .observe(getViewLifecycleOwner(), this::onExpensesChanged);

        RecyclerView recyclerView = view.findViewById(R.id.expenses_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(_expenseAdapter);

        ImageButton addButton = view.findViewById(R.id.add_expense_fab);
        addButton.setOnClickListener(this::onClickAddExpenseButton);
        DragManipulator.manipulate(addButton, 64, getResources().getDimension(R.dimen.fab_margin));

        return view;
    }

    private void onExpenseActivityResult(@NonNull ActivityResult result) {
        int resultCode = result.getResultCode();

        switch (resultCode) {
            case Activity.RESULT_OK:
                Intent intent = result.getData();

                int expenseId = intent.getIntExtra(ExpenseActivity.EXTRA_EXPENSE_ID, 0);
                if (expenseId != 0) {
                    expenseListViewModel.removeExpense(expenseId);
                } else {
                    Expense expense = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                            ? intent.getParcelableExtra(ExpenseActivity.EXTRA_EXPENSE, Expense.class)
                            : intent.getParcelableExtra(ExpenseActivity.EXTRA_EXPENSE);

                    expenseListViewModel.addOrUpdateExpense(expense);
                }
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

                long toEpochDay = intent.getLongExtra(getString(R.string.EXTRA_FILTER_TO_DATE), LocalDate.MAX.toEpochDay());
                LocalDate to = LocalDate.ofEpochDay(toEpochDay);

                String[] categories = intent.getStringArrayExtra(getString(R.string.EXTRA_FILTER_CATEGORIES));

                expenseListViewModel.setFilters(from, to, categories);
                break;
            case Activity.RESULT_CANCELED:
            default:
                break;
        }
    }

    private void onClickFilterButton(View view) {
        Intent intent = new Intent(getContext(), FilterActivity.class);
        // todo
        filterActivityResultLauncher.launch(intent);
    }

    private void onClickAddExpenseButton(View view) {
        Intent intent = new Intent(getContext(), ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.EDIT, ExpenseActivity.EDIT_OPTION_NEW);
        expenseActivityResultLauncher.launch(intent);
    }

    private void onClickExpenseItem(Expense expense, int position) {
        Intent intent = new Intent(getContext(), ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.EDIT, ExpenseActivity.EDIT_OPTION_EXISTING);
        intent.putExtra(ExpenseActivity.EXTRA_EXPENSE, expense);
        expenseActivityResultLauncher.launch(intent);
    }

    private void onExpensesChanged(List<Expense> expenses) {
        expenses.sort(Comparator.comparing(Expense::getDate)
                .reversed()
                .thenComparing((a, b) -> a.getName().compareToIgnoreCase(b.getName())));
        _expenseAdapter.submitList(expenses);
    }
}
