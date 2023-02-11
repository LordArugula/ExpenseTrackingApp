package com.example.expense_tracking_app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ExpenseCategories {

    private final Set<String> defaultCategories;
    private final Set<String> customCategories;
    private String _default;

    public ExpenseCategories(String[] expenseCategories, String _default) {
        defaultCategories = new TreeSet<>(Arrays.asList(expenseCategories));
        customCategories = new TreeSet<>();
        this._default = _default;
    }

    public void addCategory(String name) {
        if (defaultCategories.contains(name) || customCategories.contains(name)) {
            return;
        }

        customCategories.add(name);
    }

    public void removeCategory(String name) {
        customCategories.remove(name);
    }

    public void renameCategory(String oldName, String newName) {
        customCategories.remove(oldName);
        customCategories.add(newName);
    }

    public String getDefault() {
        return _default;
    }

    public void setDefault(String aDefault) {
        this._default = aDefault;
    }

    public List<String> getCategories() {
        ArrayList<String> categories = new ArrayList<>(defaultCategories.size() + customCategories.size());
        categories.addAll(defaultCategories);
        categories.addAll(customCategories);
        return categories;
    }

    public void addCategories(List<String> customCategories) {
        this.customCategories.addAll(customCategories);
    }

    public ArrayList<String> getCustomCategories() {
        return new ArrayList<>(customCategories);
    }
}
