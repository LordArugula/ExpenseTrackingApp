package com.example.expense_tracking_app.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.example.expense_tracking_app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

public class SharedPrefsExpenseCategoryRepository implements ExpenseCategoryRepository {
    private static final String CUSTOM_CATEGORIES_KEY = "SHARED_PREF_CATEGORIES";

    private final SharedPreferences _sharedPreferences;

    private final Set<String> _defaultCategories;
    private final Set<String> _customCategories;

    @Inject
    public SharedPrefsExpenseCategoryRepository(Resources resources, SharedPreferences sharedPreferences) {
        _sharedPreferences = sharedPreferences;

        String[] defaultCategories = resources.getStringArray(R.array.expense_categories);
        _defaultCategories = new TreeSet<>(Arrays.asList(defaultCategories));
        _customCategories = _sharedPreferences.getStringSet(CUSTOM_CATEGORIES_KEY, new TreeSet<>());
    }

    @Override
    public String[] getAll() {
        Collection<String> defaultCategories = getDefault();
        Collection<String> customCategories = getCustom();

        int capacity = defaultCategories.size() + customCategories.size();
        ArrayList<String> categories = new ArrayList<>(capacity);

        categories.addAll(defaultCategories);
        categories.addAll(customCategories);

        return categories.toArray(new String[0]);
    }

    @Override
    public Collection<String> getDefault() {
        return Collections.unmodifiableCollection(_defaultCategories);
    }

    @Override
    public Collection<String> getCustom() {
        return Collections.unmodifiableCollection(_customCategories);
    }

    @Override
    public void add(String category) {
        if (_defaultCategories.contains(category)) {
            return;
        }

        _customCategories.add(category);

        writeToSharedPreferences();
    }

    @Override
    public boolean remove(String category) {
        boolean removed = _customCategories.remove(category);

        writeToSharedPreferences();
        return removed;
    }

    @Override
    public boolean rename(String oldCategory, String newCategory) {
        boolean removed = _customCategories.remove(oldCategory);
        add(newCategory);
        return removed;
    }

    @Override
    public void clearCustom() {
        _customCategories.clear();
        _sharedPreferences.edit()
                .remove(CUSTOM_CATEGORIES_KEY)
                .commit();
    }

    private void writeToSharedPreferences() {
        _sharedPreferences.edit()
                .putStringSet(CUSTOM_CATEGORIES_KEY, _customCategories)
                .commit();
    }
}
