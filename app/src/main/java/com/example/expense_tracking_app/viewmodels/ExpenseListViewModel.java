package com.example.expense_tracking_app.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expense_tracking_app.filters.CategoryFilter;
import com.example.expense_tracking_app.filters.DateFilter;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.services.ExpenseCategoryRepository;
import com.example.expense_tracking_app.services.ExpenseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ExpenseListViewModel extends ViewModel {
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    private final MutableLiveData<List<Expense>> expenses;
    private final MutableLiveData<String[]> categories;

    private final MutableLiveData<DateFilter> _dateFilter;
    private final MutableLiveData<CategoryFilter> _categoryFilter;

    @Inject
    public ExpenseListViewModel(ExpenseRepository expenseRepository, ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;

        expenses = new MutableLiveData<>();
        expenses.setValue(expenseRepository.getAll());

        categories = new MutableLiveData<>();
        categories.setValue(expenseCategoryRepository.getAll());

        _dateFilter = new MutableLiveData<>(new DateFilter());
        _categoryFilter = new MutableLiveData<>(new CategoryFilter());
    }

    public LiveData<List<Expense>> getExpenses() {
        return expenses;
    }

    public void addOrUpdateExpense(Expense expense) {
        if (expenseRepository.contains(expense.getId())) {
            expenseRepository.update(expense.getId(), expense);
        } else {
            expenseRepository.add(expense);
        }

        expenseCategoryRepository.add(expense.getCategory());

        categories.postValue(expenseCategoryRepository.getAll());
        expenses.postValue(expenseRepository.getAll());
    }

    public void removeExpense(int expenseId) {
        Optional<Expense> remove = expenseRepository.remove(expenseId);
        if (remove.isPresent()) {
            expenses.setValue(expenseRepository.getAll());
        }
    }

    public void setFilters(LocalDate from, LocalDate to, String[] categories) {
        DateFilter dateFilter = _dateFilter.getValue();
        dateFilter.setStart(from);
        dateFilter.setEnd(to);
        _dateFilter.postValue(dateFilter);

        CategoryFilter categoryFilter = _categoryFilter.getValue();
        categoryFilter.clear();
        categoryFilter.includeCategories(categories);
        _categoryFilter.postValue(categoryFilter);

        List<Expense> filteredExpenses = expenses.getValue()
                .stream()
                .filter(dateFilter::matches)
                .filter(categoryFilter::matches)
                .collect(Collectors.toList());

        expenses.setValue(filteredExpenses);
    }

    public String[] getCategories() {
        return expenseCategoryRepository.getAll();
    }
}
