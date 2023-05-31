package com.example.expense_tracking_app;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.example.expense_tracking_app.databinding.ActivitySummaryBinding;
import com.example.expense_tracking_app.models.Expense;
import com.example.expense_tracking_app.services.ExpenseRepository;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SummaryActivity extends AppCompatActivity {

    private static final float SELECTED_SEGMENT_OFFSET = 25;
    @Inject
    public ExpenseRepository expenseRepository;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    private ActivitySummaryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySummaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<Expense> expenses = expenseRepository.getAll();
        Map<String, Double> categoryCostMap = new HashMap<>();
        for (Expense expense : expenses) {
            if (categoryCostMap.containsKey(expense.getCategory())) {
                Double cost = categoryCostMap.get(expense.getCategory());
                categoryCostMap.put(expense.getCategory(), cost + expense.getCost());
            } else {
                categoryCostMap.put(expense.getCategory(), expense.getCost());
            }
        }

        int i = 0;
        float step = 360f / categoryCostMap.size();
        for (Map.Entry<String, Double> entry : categoryCostMap.entrySet()) {
            String category = entry.getKey();
            Double cost = entry.getValue();
            Segment segment = new Segment(String.format("%s\n%s", category, currencyFormat.format(cost)), cost);

            float[] hsv = new float[]{step * i++, 0.8f, 0.9f};
            int color = Color.HSVToColor(hsv);
            SegmentFormatter segmentFormatter = new SegmentFormatter(color, color);
            binding.pieChart.addSegment(segment, segmentFormatter);
        }
    }
}