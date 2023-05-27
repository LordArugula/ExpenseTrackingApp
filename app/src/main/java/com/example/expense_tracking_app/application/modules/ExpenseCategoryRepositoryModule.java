package com.example.expense_tracking_app.application.modules;

import com.example.expense_tracking_app.services.ExpenseCategoryRepository;
import com.example.expense_tracking_app.services.ExpenseRepository;
import com.example.expense_tracking_app.services.SharedPrefsExpenseCategoryRepository;
import com.example.expense_tracking_app.services.SharedPrefsExpenseRepository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

@Module
@InstallIn(ActivityComponent.class)
public abstract class ExpenseCategoryRepositoryModule {

    @Binds
    public abstract ExpenseCategoryRepository providesExpenseCategoryRepository(SharedPrefsExpenseCategoryRepository expenseCategoryRepository);
}
