package com.example.expense_tracking_app.application.modules;

import com.example.expense_tracking_app.services.ExpenseCategoryRepository;
import com.example.expense_tracking_app.services.SharedPrefsExpenseCategoryRepository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@Module
@InstallIn(ViewModelComponent.class)
public abstract class ExpenseCategoryRepositoryModule {

    @Binds
    public abstract ExpenseCategoryRepository providesExpenseCategoryRepository(SharedPrefsExpenseCategoryRepository expenseCategoryRepository);
}
