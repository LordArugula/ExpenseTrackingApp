package com.example.expense_tracking_app.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.expense_tracking_app.models.Expense;

import java.util.Objects;

public class ExpenseDiffUtil extends DiffUtil.ItemCallback<Expense> {
    @Override
    public boolean areItemsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
        return Objects.equals(oldItem.getName(), newItem.getName())
                && oldItem.getCost() == newItem.getCost()
                && Objects.equals(oldItem.getCategory(), newItem.getCategory())
                && Objects.equals(oldItem.getDate(), newItem.getDate());
    }
}
