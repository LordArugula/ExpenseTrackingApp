package com.example.expense_tracking_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense_tracking_app.R;
import com.example.expense_tracking_app.databinding.ExpenseBinding;
import com.example.expense_tracking_app.models.Expense;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

public class ExpenseAdapter extends ListAdapter<Expense, ExpenseAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ExpenseBinding binding;

        private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ExpenseBinding.bind(itemView);
        }

        public void bind(Expense expense) {
            binding.name.setText(expense.getName());
            binding.category.setText(expense.getCategory());
            binding.date.setText(dateFormat.format(expense.getDate()));
            binding.cost.setText(currencyFormat.format(expense.getCost()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Expense expense, int position);
    }

    private final OnItemClickListener _onItemClickListener;

    public ExpenseAdapter(OnItemClickListener onItemClickListener) {
        this(new ExpenseDiffUtil(), onItemClickListener);
    }

    protected ExpenseAdapter(@NonNull DiffUtil.ItemCallback<Expense> diffCallback, OnItemClickListener onItemClickListener) {
        super(diffCallback);
        _onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = getItem(position);
        holder.bind(expense);
        holder.itemView.setOnClickListener(view -> this._onItemClickListener.onItemClick(expense, position));
    }
}
