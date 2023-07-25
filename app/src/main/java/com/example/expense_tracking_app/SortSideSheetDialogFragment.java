package com.example.expense_tracking_app;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expense_tracking_app.databinding.SortSideSheetBinding;
import com.example.expense_tracking_app.filters.ExpenseQueryState;
import com.example.expense_tracking_app.filters.SortBy;
import com.example.expense_tracking_app.viewmodels.ExpenseViewModel;
import com.google.android.material.sidesheet.SideSheetDialog;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SortSideSheetDialogFragment extends DialogFragment {

    private ExpenseViewModel expenseListViewModel;

    private SortSideSheetBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sort_side_sheet, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = SortSideSheetBinding.bind(view);

        expenseListViewModel = new ViewModelProvider(getActivity())
                .get(ExpenseViewModel.class);

        Toolbar toolbar = binding.toolbar;
        toolbar.inflateMenu(R.menu.side_sheet_menu);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        expenseListViewModel.getQueryState().observe(getActivity(), queryState -> {
            switch (queryState.getSortBy()) {
                case Date:
                    binding.sortByRadioGroup.check(R.id.sort_date);
                    break;
                case Name:
                    binding.sortByRadioGroup.check(R.id.sort_name);
                    break;
                case Cost:
                    binding.sortByRadioGroup.check(R.id.sort_cost);
                    break;
                case Category:
                    binding.sortByRadioGroup.check(R.id.sort_category);
                    break;
            }

            expenseListViewModel.getQueryState().removeObservers(getActivity());
        });
        binding.sortByRadioGroup.setOnCheckedChangeListener(this::onSortOptionChanged);
    }

    private void onSortOptionChanged(RadioGroup radioGroup, int id) {
        ExpenseQueryState queryState = expenseListViewModel.getQueryState().getValue();
        switch (id) {
            case R.id.sort_category:
                queryState.setSortBy(SortBy.Category);
                break;
            case R.id.sort_cost:
                queryState.setSortBy(SortBy.Cost);
                break;
            case R.id.sort_date:
                queryState.setSortBy(SortBy.Date);
                break;
            case R.id.sort_name:
                queryState.setSortBy(SortBy.Name);
                break;
        }
        expenseListViewModel.setQueryState(queryState);
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
}
