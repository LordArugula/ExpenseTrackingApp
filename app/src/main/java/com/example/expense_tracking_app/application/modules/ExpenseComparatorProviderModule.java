package com.example.expense_tracking_app.application.modules;

import com.example.expense_tracking_app.sorters.DateExpenseComparator;
import com.example.expense_tracking_app.sorters.ExpenseComparator;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@Module
@InstallIn(ViewModelComponent.class)
public abstract class ExpenseComparatorProviderModule {
    @Provides
    public static ExpenseComparator providesExpenseComparator() {
        return new DateExpenseComparator(ExpenseComparator.DESCENDING);
    }
}
