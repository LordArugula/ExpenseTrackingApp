package com.example.expense_tracking_app.services;

import java.util.Collection;

public interface ExpenseCategoryRepository {
    String[] getAll();
    Collection<String> getDefault();
    Collection<String> getCustom();
    void add(String category);
    boolean remove(String category);
    boolean rename(String oldCategory, String newCategory);
    void clearCustom();
}
