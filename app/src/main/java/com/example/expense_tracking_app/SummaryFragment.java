package com.example.expense_tracking_app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.services.ExpenseRepository;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SummaryFragment extends Fragment {

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    @Inject
    public ExpenseRepository _expenseRepository;

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        List<Expense> expenses = _expenseRepository.getAll();
        Map<String, Double> categoryCostMap = new HashMap<>();
        for (Expense expense : expenses) {
            if (categoryCostMap.containsKey(expense.getCategory())) {
                Double cost = categoryCostMap.get(expense.getCategory());
                categoryCostMap.put(expense.getCategory(), cost + expense.getCost());
            } else {
                categoryCostMap.put(expense.getCategory(), expense.getCost());
            }
        }

        PieChart pieChart = view.findViewById(R.id.pie_chart);
        pieChart.getBackgroundPaint().setColor(Color.TRANSPARENT);

        int i = 0;
        float step = 360f / categoryCostMap.size();
        for (Map.Entry<String, Double> entry : categoryCostMap.entrySet()) {
            String category = entry.getKey();
            Double cost = entry.getValue();
            Segment segment = new Segment(String.format("%s\n%s", category, currencyFormat.format(cost)), cost);

            float[] hsv = new float[]{step * i++, 0.8f, 0.9f};
            int color = Color.HSVToColor(hsv);
            SegmentFormatter segmentFormatter = new SegmentFormatter(color, color);
            pieChart.addSegment(segment, segmentFormatter);
        }

        return view;
    }
}