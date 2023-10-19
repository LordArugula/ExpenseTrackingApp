package com.example.expense_tracking_app.services;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.expense_tracking_app.data_access.ExpenseDao;
import com.example.expense_tracking_app.data_access.ExpenseRoomDatabase;
import com.example.expense_tracking_app.models.Expense;

import java.util.List;

public class RoomExpenseRepository implements ExpenseRepository {

    private final ExpenseDao expenseDao;
    private final LiveData<List<Expense>> allExpenses;

    public RoomExpenseRepository(Context application) {
        ExpenseRoomDatabase db = ExpenseRoomDatabase.getDatabase(application);
        expenseDao = db.getExpenseDao();
        allExpenses = expenseDao.getAll();
    }

    public LiveData<List<Expense>> getAll() {
        return allExpenses;
    }

    public void insert(Expense expense) {
        ExpenseRoomDatabase.databaseWriteExecutor.execute(() -> expenseDao.insert(expense));
    }

    public void update(Expense expense) {
        ExpenseRoomDatabase.databaseWriteExecutor.execute(() -> expenseDao.update(expense));
    }

    public void deleteById(int id) {
        ExpenseRoomDatabase.databaseWriteExecutor.execute(() -> expenseDao.delete(id));
    }

    public void deleteAll() {
        ExpenseRoomDatabase.databaseWriteExecutor.execute(expenseDao::deleteAll);
    }

    public LiveData<List<String>> getCategories() {
        return expenseDao.getCategories();
    }

    public LiveData<Expense> getById(int id) {
        return expenseDao.getById(id);
    }
}
