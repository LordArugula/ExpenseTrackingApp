package com.example.expense_tracking_app.data_access;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expense_tracking_app.models.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    void insert(Expense expense);

    @Update
    void update(Expense expense);

    @Query("DELETE FROM expenses_table WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM expenses_table")
    void deleteAll();

    @Query("SELECT * FROM expenses_table ORDER BY date DESC")
    LiveData<List<Expense>> getAll();

    @Query("SELECT DISTINCT(category) FROM expenses_table")
    LiveData<List<String>> getCategories();

    @Query("SELECT * FROM expenses_table WHERE id = :id")
    LiveData<Expense> getById(int id);
}
