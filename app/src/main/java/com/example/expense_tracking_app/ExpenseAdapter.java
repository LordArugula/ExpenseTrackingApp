package com.example.expense_tracking_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private final ExpenseAdapter.onItemClickListener onItemClickListener;

    private final List<Expense> items;

    public ExpenseAdapter(List<Expense> expenses, onItemClickListener onItemClickListener) {
        this.items = expenses;
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener {
        void onItemClick(Expense expense, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView category;
        private final TextView date;
        private final TextView cost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.expense_name_edittext);
            category = itemView.findViewById(R.id.expense_category_edittext);
            date = itemView.findViewById(R.id.expense_date_edittext);
            cost = itemView.findViewById(R.id.expense_cost_edittext);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setCategory(String category) {
            this.category.setText(category);
        }

        public void setDate(LocalDate date) {
            this.date.setText(date.format(DateTimeFormatter.ofPattern(itemView.getResources().getString(R.string.date_format_mmddyyyy))));
        }

        public void setCost(double cost) {
            NumberFormat format = NumberFormat.getCurrencyInstance();
            this.cost.setText(format.format(cost));
        }
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
        Expense expense = items.get(position);
        holder.setName(expense.getName());
        holder.setCategory(expense.getCategory());
        holder.setDate(expense.getDate());
        holder.setCost(expense.getCost());
        holder.itemView.setOnClickListener(view -> this.onItemClickListener.onItemClick(expense, position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
