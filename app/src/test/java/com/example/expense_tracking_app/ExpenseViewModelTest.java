package com.example.expense_tracking_app;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.services.ExpenseRepository;
import com.example.expense_tracking_app.viewmodels.ExpenseViewModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ExpenseViewModelTest {
    @Test
    public void expenseRepository_GetAll_ReturnsALl() {
        List<Expense> expected = Arrays.asList(
                new Expense(0, "Name", LocalDate.now(), 1.23, "Test", "Notes", "Category"),
                new Expense(0, "Hello", LocalDate.now(), 4.20, "uwu", "Notes", "Test"),
                new Expense(0, "World", LocalDate.now(), 6.9, "owo", "Notes", "Foobar"),
                new Expense(0, "Test", LocalDate.now(), 420.69, "crimes", "Notes", "Category")
        );

        ExpenseRepository repositoryMock = mock(ExpenseRepository.class);
        when(repositoryMock.getAll())
                .thenReturn(new MutableLiveData<>(expected));

        ExpenseViewModel expenseViewModel = new ExpenseViewModel(repositoryMock);

        LiveData<List<Expense>> allExpenses = expenseViewModel.getAllExpenses();
        List<Expense> actual = allExpenses.getValue();
        Assert.assertSame(expected, actual);
    }
}