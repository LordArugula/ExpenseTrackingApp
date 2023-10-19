package com.example.expense_tracking_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expense_tracking_app.adapters.ExpenseAdapter;
import com.example.expense_tracking_app.databinding.FragmentExpenseListBinding;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.viewmodels.ExpenseViewModel;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A fragment representing a list of Expenses.
 */
@AndroidEntryPoint
public class ExpenseListFragment extends Fragment {

    private ExpenseAdapter _expenseAdapter;

    public ExpenseListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);
        com.example.expense_tracking_app.databinding.FragmentExpenseListBinding binding = FragmentExpenseListBinding.bind(view);

        // Set the adapter
        _expenseAdapter = new ExpenseAdapter(this::onClickExpenseItem);

        ExpenseViewModel expenseViewModel = new ViewModelProvider(getActivity())
                .get(ExpenseViewModel.class);

        expenseViewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenses -> {
            _expenseAdapter.submitList(expenses);
        });

        binding.expensesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.expensesRecyclerView.setAdapter(_expenseAdapter);

        binding.addExpenseFab.setOnClickListener(this::onClickAddExpenseButton);
        float thresholdRadius = getResources().getDimension(R.dimen.drag_threshold);
        DragManipulator.manipulate(binding.addExpenseFab, thresholdRadius, getResources().getDimension(R.dimen.margin_large));

        return view;
    }

    private void onClickAddExpenseButton(View view) {
        Intent intent = new Intent(getContext(), ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.EDIT, ExpenseActivity.EDIT_OPTION_NEW);
        startActivity(intent);
    }

    private void onClickExpenseItem(Expense expense, int position) {
        Intent intent = new Intent(getContext(), ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.EDIT, ExpenseActivity.EDIT_OPTION_EXISTING);
        intent.putExtra(ExpenseActivity.EXTRA_EXPENSE_ID, expense.getId());
        startActivity(intent);
    }
}
