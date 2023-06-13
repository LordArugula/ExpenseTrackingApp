package com.example.expense_tracking_app;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expense_tracking_app.databinding.SortSideSheetBinding;
import com.example.expense_tracking_app.sorters.ExpenseComparator;
import com.example.expense_tracking_app.viewmodels.ExpenseListViewModel;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.sidesheet.SideSheetDialog;

import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SortSideSheetDialogFragment extends DialogFragment {

    @Inject
    public Map<String, ExpenseComparator> expenseComparators;

    private ExpenseListViewModel expenseListViewModel;

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
                .get(ExpenseListViewModel.class);

        Toolbar toolbar = binding.toolbar;
        toolbar.setTitle(R.string.sort_title);
        toolbar.inflateMenu(R.menu.side_sheet_menu);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        buildRadioGroup();
    }

    private void buildRadioGroup() {
        RadioGroup radioGroup = binding.sortByRadioGroup;

        ExpenseComparator currentComparator = expenseListViewModel.getComparator().getValue();
        for (Map.Entry<String, ExpenseComparator> entry : expenseComparators.entrySet()) {
            String name = entry.getKey();
            ExpenseComparator comparator = entry.getValue();

            RadioButton radioButton = new MaterialRadioButton(getContext());
            radioButton.setText(name);
            radioGroup.addView(radioButton);

            if (currentComparator.getClass() == comparator.getClass()
                    && currentComparator.getOrder() == comparator.getOrder()) {
                radioGroup.check(radioButton.getId());
            }
        }

        radioGroup.setOnCheckedChangeListener(this::onSortButtonChecked);
    }

    private void onSortButtonChecked(RadioGroup radioGroup, int id) {
        RadioButton radioButton = radioGroup.findViewById(id);
        String name = radioButton.getText().toString();
        onSelectComparator(name);
    }

    private void onSelectComparator(String key) {
        ExpenseComparator comparator = expenseComparators.get(key);
        expenseListViewModel.setComparator(comparator);
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
