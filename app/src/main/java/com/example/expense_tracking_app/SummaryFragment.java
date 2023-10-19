package com.example.expense_tracking_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Orientation;
import com.example.expense_tracking_app.databinding.FragmentSummaryBinding;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.viewmodels.ExpenseViewModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SummaryFragment extends Fragment {

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private FragmentSummaryBinding binding;
    private Pie pieChart;

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        binding = FragmentSummaryBinding.bind(view);

        ExpenseViewModel expenseViewModel = new ViewModelProvider(getActivity())
                .get(ExpenseViewModel.class);

        expenseViewModel.getAllExpenses()
                .observe(getViewLifecycleOwner(), this::onExpensesChanged);

        pieChart = AnyChart.pie();
        pieChart.legend()
                .position(Orientation.BOTTOM)
                .itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE)
                .align(Align.RIGHT);

        binding.pieChart.setChart(pieChart);

        return view;
    }

    private void onExpensesChanged(List<Expense> expenses) {
        drawPieChart(expenses);
        double total = expenses.stream()
                .mapToDouble(Expense::getCost)
                .sum();

        double average = expenses.size() > 0 ? total / expenses.size() : 0;

        binding.totalText.setText(currencyFormat.format(total));
        binding.averageText.setText(currencyFormat.format(average));
    }

    private void drawPieChart(List<Expense> expenses) {
        List<DataEntry> pieChartData = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getCost)))
                .entrySet()
                .stream().map(entry -> new ValueDataEntry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        APIlib.getInstance().setActiveAnyChartView(binding.pieChart);
        pieChart.data(pieChartData);
    }
}