package com.example.expense_tracking_app.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expense_tracking_app.filters.CategoryFilter;
import com.example.expense_tracking_app.filters.DateFilter;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.services.ExpenseRepository;
import com.example.expense_tracking_app.sorters.ExpenseComparator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ExpenseListViewModel extends ViewModel {
    private final ExpenseRepository expenseRepository;

    private final MutableLiveData<List<Expense>> expenses;

    private final MutableLiveData<DateFilter> _dateFilter;
    private final MutableLiveData<CategoryFilter> _categoryFilter;

    private final MutableLiveData<ExpenseComparator> _comparator;

    @Inject
    public ExpenseListViewModel(ExpenseRepository expenseRepository, ExpenseComparator expenseComparator) {
        this.expenseRepository = expenseRepository;

        _dateFilter = new MutableLiveData<>(new DateFilter());
        _categoryFilter = new MutableLiveData<>(new CategoryFilter());
         _comparator = new MutableLiveData<>(expenseComparator);

        expenses = new MutableLiveData<>();
        updateViewExpenses();
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

        expenses.postValue(expenseRepository.getAll());
    }

    public void removeExpense(int expenseId) {
        Optional<Expense> remove = expenseRepository.remove(expenseId);
        if (remove.isPresent()) {
            expenses.postValue(expenseRepository.getAll());
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

        updateViewExpenses();
    }

    public String[] getCategories() {
        return expenseRepository.getCategories();
    }

    public LiveData<DateFilter> getDateFilter() {
        return _dateFilter;
    }

    public void setDateFilter(LocalDate from, LocalDate to) {
        DateFilter dateFilter = _dateFilter.getValue();
        dateFilter.setStart(from);
        dateFilter.setEnd(to);
        _dateFilter.postValue(dateFilter);

        updateViewExpenses(dateFilter, _categoryFilter.getValue(), _comparator.getValue());
    }

    public LiveData<CategoryFilter> getCategoryFilter() {
        return _categoryFilter;
    }

    public void setCategoryFilter(Collection<String> categories) {
        CategoryFilter categoryFilter = _categoryFilter.getValue();
        categoryFilter.clear();
        categoryFilter.includeCategories(categories);
        _categoryFilter.postValue(categoryFilter);

        updateViewExpenses(_dateFilter.getValue(), categoryFilter, _comparator.getValue());
    }

    public LiveData<ExpenseComparator> getComparator() {
        return _comparator;
    }

    public void setComparator(ExpenseComparator comparator) {
        _comparator.postValue(comparator);

        updateViewExpenses(_dateFilter.getValue(), _categoryFilter.getValue(), comparator);
    }

    private void updateViewExpenses(DateFilter dateFilter, CategoryFilter categoryFilter, ExpenseComparator comparator) {
        List<Expense> filteredExpenses = expenseRepository.getAll()
                .stream()
                .filter(dateFilter::matches)
                .filter(categoryFilter::matches)
                .sorted(comparator.getComparator())
                .collect(Collectors.toList());

        expenses.postValue(Collections.unmodifiableList(filteredExpenses));
    }

    private void updateViewExpenses() {
        updateViewExpenses(_dateFilter.getValue(), _categoryFilter.getValue(), _comparator.getValue());
    }
}
