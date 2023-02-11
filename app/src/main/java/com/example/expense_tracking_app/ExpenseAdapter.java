package com.example.expense_tracking_app;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private final ExpenseAdapter.onItemClickListener onItemClickListener;

    private final List<Expense> backingItems;
    private final List<Expense> viewItems;
    private final HashMap<Expense, Integer> viewItemsToBackingItems;

    private final CategoryFilter categoryFilter;
    private final DateFilter dateFilter;

    public ExpenseAdapter(List<Expense> expenses, CategoryFilter categoryFilter, DateFilter dateFilter, onItemClickListener onItemClickListener) {
        this.backingItems = expenses;
        this.categoryFilter = categoryFilter;
        this.dateFilter = dateFilter;

        this.onItemClickListener = onItemClickListener;

        this.viewItems = new ArrayList<>();
        this.viewItemsToBackingItems = new HashMap<>();
        rebuildViewItems();
    }

    private void rebuildViewItems() {
        viewItems.clear();
        viewItemsToBackingItems.clear();

        for (int i = 0; i < backingItems.size(); i++) {
            Expense expense = backingItems.get(i);
            if (matchesFilter(expense)) {
                viewItemsToBackingItems.put(expense, viewItems.size());
                viewItems.add(expense);
            }
        }
        viewItems.sort((a, b) -> {
            int dateComparison = a.getDate().compareTo(b.getDate());
            if (dateComparison == 0) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
            return dateComparison;
        });
        notifyDataSetChanged();
    }

    public void addItem(Expense expense) {
        backingItems.add(expense);

        if (matchesFilter(expense)) {
            rebuildViewItems();
        }
    }

    private boolean matchesFilter(Expense expense) {
        return categoryFilter.filter(expense) && dateFilter.filter(expense);
    }

    public void updateItem(int position, Expense expense) {
        Expense previous = backingItems.get(position);

        backingItems.set(position, expense);
        if (matchesFilter(expense) || viewItemsToBackingItems.containsKey(previous)) {
            rebuildViewItems();
        }
    }

    public void removeItem(int position) {
        Expense expense = backingItems.get(position);
        backingItems.remove(position);

        if (!viewItemsToBackingItems.containsKey(expense)) {
            return;
        }

        rebuildViewItems();
    }

    public void updateFilters() {
        rebuildViewItems();
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

            name = itemView.findViewById(R.id.expense_name);
            category = itemView.findViewById(R.id.expense_category);
            date = itemView.findViewById(R.id.expense_date);
            cost = itemView.findViewById(R.id.expense_cost);
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
        Expense expense = viewItems.get(position);
        holder.setName(expense.getName());
        holder.setCategory(expense.getCategory());
        holder.setDate(expense.getDate());
        holder.setCost(expense.getCost());
        holder.itemView.setOnClickListener(view -> {
            this.onItemClickListener.onItemClick(expense, viewItemsToBackingItems.get(expense));
        });
    }

    @Override
    public int getItemCount() {
        return viewItems.size();
    }
}
