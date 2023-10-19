package com.example.expense_tracking_app.application.modules;

import android.content.Context;

import com.example.expense_tracking_app.services.ExpenseRepository;
import com.example.expense_tracking_app.services.RoomExpenseRepository;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Module
@InstallIn(ViewModelComponent.class)
public class ExpenseRepositoryModule {
    @Provides
    public ExpenseRepository providesExpenseRepository(@ApplicationContext Context context) {
        return new RoomExpenseRepository(context);
    }
}
