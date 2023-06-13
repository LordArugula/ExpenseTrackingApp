package com.example.expense_tracking_app.application.modules;

import android.content.res.Resources;

import com.example.expense_tracking_app.R;
import com.example.expense_tracking_app.sorters.CategoryExpenseComparator;
import com.example.expense_tracking_app.sorters.CostExpenseComparator;
import com.example.expense_tracking_app.sorters.DateExpenseComparator;
import com.example.expense_tracking_app.sorters.ExpenseComparator;
import com.example.expense_tracking_app.sorters.NameExpenseComparator;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

@Module
@InstallIn(ActivityComponent.class)
public abstract class ExpenseComparatorModule {
    @Provides
    public static Map<String, ExpenseComparator> providesExpenseComparatorMap(Resources resources) {
        Map<String, ExpenseComparator> expenseComparatorMap = new TreeMap<>();

        ExpenseComparator nameComparatorAsc = new NameExpenseComparator(ExpenseComparator.ASCENDING);
        expenseComparatorMap.put(resources.getString(R.string.sort_name_ascending), nameComparatorAsc);

        ExpenseComparator nameComparatorDesc = new NameExpenseComparator(ExpenseComparator.DESCENDING);
        expenseComparatorMap.put(resources.getString(R.string.sort_name_descending), nameComparatorDesc);

        ExpenseComparator dateComparatorAsc = new DateExpenseComparator(ExpenseComparator.ASCENDING);
        expenseComparatorMap.put(resources.getString(R.string.sort_date_ascending), dateComparatorAsc);

        ExpenseComparator dateComparatorDesc = new DateExpenseComparator(ExpenseComparator.DESCENDING);
        expenseComparatorMap.put(resources.getString(R.string.sort_date_descending), dateComparatorDesc);

        ExpenseComparator categoryComparatorAsc = new CategoryExpenseComparator(ExpenseComparator.ASCENDING);
        expenseComparatorMap.put(resources.getString(R.string.sort_category_ascending), categoryComparatorAsc);

        ExpenseComparator categoryComparatorDesc = new CategoryExpenseComparator(ExpenseComparator.DESCENDING);
        expenseComparatorMap.put(resources.getString(R.string.sort_category_descending), categoryComparatorDesc);

        ExpenseComparator costComparatorAsc = new CostExpenseComparator(ExpenseComparator.ASCENDING);
        expenseComparatorMap.put(resources.getString(R.string.sort_cost_ascending), costComparatorAsc);

        ExpenseComparator costComparatorDesc = new CostExpenseComparator(ExpenseComparator.DESCENDING);
        expenseComparatorMap.put(resources.getString(R.string.sort_cost_descending), costComparatorDesc);

        return Collections.unmodifiableMap(expenseComparatorMap);
    }
}
