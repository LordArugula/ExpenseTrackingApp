package com.example.expense_tracking_app.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.expense_tracking_app.filters.ExpenseQueryState;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.services.ExpenseRepository;
import com.example.expense_tracking_app.services.RoomExpenseRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ExpenseViewModel extends ViewModel {

    private final ExpenseRepository expenseRepository;
    private final LiveData<List<Expense>> allExpenses;
    private final MutableLiveData<ExpenseQueryState> queryState;

    @Inject
    public ExpenseViewModel(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
        queryState = new MutableLiveData<>(new ExpenseQueryState());
        allExpenses = Transformations.switchMap(queryState, _queryState -> Transformations.map(expenseRepository.getAll(),
                input -> input.stream()
                        .filter(_queryState::match)
                        .sorted(_queryState.getComparator())
                        .collect(Collectors.toList()))
        );
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    public void insert(Expense expense) {
        expenseRepository.insert(expense);
    }

    public void update(Expense expense) {
        expenseRepository.update(expense);
    }

    public void deleteById(int id) {
        expenseRepository.deleteById(id);
    }

    public void deleteAll(Expense expense) {
        expenseRepository.deleteAll();
    }

    public void filter(ExpenseQueryState filterState) {
        this.queryState.postValue(filterState);
    }

    public LiveData<ExpenseQueryState> getQueryState() {
        return queryState;
    }

    public void setQueryState(ExpenseQueryState queryState) {
        this.queryState.postValue(queryState);
    }

    public LiveData<List<String>> getCategories() {
        return expenseRepository.getCategories();
    }

    public LiveData<Expense> getById(int id) {
        return expenseRepository.getById(id);
    }
}
