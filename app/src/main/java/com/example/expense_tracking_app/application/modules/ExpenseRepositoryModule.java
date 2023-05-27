package com.example.expense_tracking_app.application.modules;

import com.example.expense_tracking_app.services.ExpenseRepository;
import com.example.expense_tracking_app.services.SharedPrefsExpenseRepository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

@Module
@InstallIn(ActivityComponent.class)
public abstract class ExpenseRepositoryModule {

    @Binds
    public abstract ExpenseRepository providesExpenseRepository(SharedPrefsExpenseRepository expenseRepository);
}
