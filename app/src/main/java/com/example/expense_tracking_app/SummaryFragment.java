package com.example.expense_tracking_app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.viewmodels.ExpenseListViewModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SummaryFragment extends Fragment {

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    private ExpenseListViewModel expenseListViewModel;
    private PieChart pieChart;

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        expenseListViewModel = new ViewModelProvider(getActivity())
                .get(ExpenseListViewModel.class);

        expenseListViewModel.getExpenses()
                .observe(getViewLifecycleOwner(), this::onExpensesChanged);

        pieChart = view.findViewById(R.id.pie_chart);
        pieChart.getBackgroundPaint().setColor(Color.TRANSPARENT);

        return view;
    }

    private void onExpensesChanged(List<Expense> expenses) {
        drawPieChart(expenses);
    }

    private void drawPieChart(List<Expense> expenses) {
        pieChart.getRegistry().clear();
        Map<String, Double> categoryCostMap = createCategoryCostMap(expenses);
        createPieChartSections(categoryCostMap);
    }

    @NonNull
    private static Map<String, Double> createCategoryCostMap(List<Expense> expenses) {
        Map<String, Double> categoryCostMap = new TreeMap<>();

        for (Expense expense : expenses) {
            if (categoryCostMap.containsKey(expense.getCategory())) {
                Double cost = categoryCostMap.get(expense.getCategory());
                categoryCostMap.put(expense.getCategory(), cost + expense.getCost());
            } else {
                categoryCostMap.put(expense.getCategory(), expense.getCost());
            }
        }

        return categoryCostMap;
    }

    private void createPieChartSections(Map<String, Double> categoryCostMap) {
        float step = 360f / categoryCostMap.size();
        int i = 0;

        for (Map.Entry<String, Double> entry : categoryCostMap.entrySet()) {
            String category = entry.getKey();
            Double cost = entry.getValue();
            Segment segment = new Segment(String.format("%s\n%s", category, currencyFormat.format(cost)), cost);

            float[] hsv = new float[]{step * i, 0.8f, 0.9f};
            int color = Color.HSVToColor(hsv);
            SegmentFormatter segmentFormatter = new SegmentFormatter(color, color);
            pieChart.addSegment(segment, segmentFormatter);
            i++;
        }

        pieChart.redraw();
    }
}