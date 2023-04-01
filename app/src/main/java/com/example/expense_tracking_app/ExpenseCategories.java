package com.example.expense_tracking_app;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ExpenseCategories {
    private final Set<String> defaultCategories;
    private final Set<String> customCategories;
    private String defaultCategory;

    public ExpenseCategories(List<String> defaultCategories, List<String> customCategories, String defaultCategory) {
        this.defaultCategories = new TreeSet<>(defaultCategories);
        this.customCategories = new TreeSet<>(customCategories  );

        this.defaultCategory = defaultCategory;
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

    public String getDefaultCategory() {
        return defaultCategory;
    }

    public void setDefault(String aDefault) {
        this.defaultCategory = aDefault;
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

    public boolean isCustomCategory(String category) {
        return customCategories.contains(category);
    }
}