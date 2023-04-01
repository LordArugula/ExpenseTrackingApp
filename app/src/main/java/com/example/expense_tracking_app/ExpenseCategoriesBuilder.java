package com.example.expense_tracking_app;

import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpenseCategoriesBuilder {
    private final List<String> defaultCategories;
    private final List<String> customCategories;
    private String defaultCategory;

    public ExpenseCategoriesBuilder() {
        defaultCategories = new ArrayList<>();
        customCategories = new ArrayList<>();
    }

    public ExpenseCategoriesBuilder withDefaultCategoriesFromResources(Resources resources) {
        defaultCategories.clear();
        defaultCategories.addAll(Arrays.asList(resources.getStringArray(R.array.expense_categories)));
        defaultCategory = resources.getString(R.string.expense_category_default);
        return this;
    }

    public ExpenseCategoriesBuilder withCategoriesFromSharedPrefs(SharedPreferences sharedPrefs, String file) {
        customCategories.clear();
        String customCategoriesJson = sharedPrefs.getString(file, null);
        if (customCategoriesJson != null) {
            customCategories.addAll(JsonUtils.categoriesFromJson(customCategoriesJson));
        }
        return this;
    }

    public ExpenseCategories build() {
        return new ExpenseCategories(defaultCategories, customCategories, defaultCategory);
    }
}
