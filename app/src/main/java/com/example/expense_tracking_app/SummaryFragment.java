package com.example.expense_tracking_app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.example.expense_tracking_app.databinding.FragmentSummaryBinding;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.viewmodels.ExpenseListViewModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SummaryFragment extends Fragment {

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private ExpenseListViewModel expenseListViewModel;

    private FragmentSummaryBinding binding;

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        binding = FragmentSummaryBinding.bind(view);

        expenseListViewModel = new ViewModelProvider(getActivity())
                .get(ExpenseListViewModel.class);

        expenseListViewModel.getExpenses()
                .observe(getViewLifecycleOwner(), this::onExpensesChanged);

        binding.pieChart.getBackgroundPaint().setColor(Color.TRANSPARENT);

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
        binding.pieChart.getRegistry().clear();
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
            binding.pieChart.addSegment(segment, segmentFormatter);
            i++;
        }

        binding.pieChart.redraw();
    }
}