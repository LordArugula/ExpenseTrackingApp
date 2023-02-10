package com.example.expense_tracking_app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExpenseCategories {

    private Set<String> defaultCategories;
    private List<String> customCategories;
    private String _default;

    public ExpenseCategories(String[] expenseCategories, String _default) {
        defaultCategories = new HashSet<>(Arrays.asList(expenseCategories));
        customCategories = new ArrayList<>();
        this._default = _default;
    }

    public void addCategory(String name) {
        customCategories.add(name);
    }

    public void removeCategory(String name) {
        customCategories.remove(name);
    }

    public void removeCategory(int position) {
        customCategories.remove(position);
    }

    public void renameCategory(String oldName, String newName) {
        int position = customCategories.indexOf(oldName);
        renameCategory(position, newName);
    }

    private void renameCategory(int position, String name) {
        customCategories.set(position, name);
    }

    public String getDefault() {
        return _default;
    }

    public void setaDefault(String aDefault) {
        this._default = aDefault;
    }
}
