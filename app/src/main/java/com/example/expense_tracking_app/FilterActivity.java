package com.example.expense_tracking_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class FilterActivity extends AppCompatActivity {
//
//    private static final String TAG = FilterActivity.class.getSimpleName();
//
//    private LocalDate start;
//    private LocalDate end;
//    private String category;
//
//    private MaterialDatePicker<Long> datePicker;
//    private MaterialDatePicker<Pair<Long, Long>> dateRangePicker;
//
//    private TextView dateRangeText;
//    private AutoCompleteTextView categoryText;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_filter);
//
//        Intent intent = getIntent();
//
//        dateRangeText = findViewById(R.id.filter_date_range);
//        boolean filterByDate = intent.getBooleanExtra(getString(R.string.EXTRA_FILTER_BY_DATE), false);
//        if (filterByDate) {
//            long startEpoch = intent.getLongExtra(getString(R.string.EXTRA_FILTER_DATE_START), 0);
//            long endEpoch = intent.getLongExtra(getString(R.string.EXTRA_FILTER_DATE_END), 0);
//
//            start = LocalDate.ofEpochDay(startEpoch);
//            end = LocalDate.ofEpochDay(endEpoch);
//
//            setDateText();
//        }
//
//        categoryText = findViewById(R.id.filter_category);
//        boolean filterByCategory = intent.getBooleanExtra(getString(R.string.EXTRA_FILTER_BY_CATEGORY), false);
//        if (filterByCategory) {
//            category = intent.getStringExtra(getString(R.string.EXTRA_FILTER_CATEGORY));
//            categoryText.setText(category);
//        } else {
//            category = getString(R.string.filter_category_all);
//            categoryText.setText(category);
//        }
//
//        ExpenseCategories expenseCategories = new ExpenseCategories(getResources());
//
//        List<String> customCategories = intent.getStringArrayListExtra(getString(R.string.EXTRA_EXPENSE_CUSTOM_CATEGORIES));
//        expenseCategories.addCategories(customCategories);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, expenseCategories.getCategories());
//        categoryText.setAdapter(adapter);
//    }
//
//    private void setDateText() {
//        String startDateText = start.format(DateTimeFormatter.ofPattern(getString(R.string.date_format_mmddyyyy)));
//        String endDateText = end.format(DateTimeFormatter.ofPattern(getString(R.string.date_format_mmddyyyy)));
//        String dateRange = startDateText + " - " + endDateText;
//        dateRangeText.setText(dateRange);
//    }
//
//    public void cancel(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//        setResult(RESULT_CANCELED, intent);
//        finish();
//    }
//
//    public void clear(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//
//        intent.putExtra(getString(R.string.EXTRA_FILTER_BY_DATE), false);
//        intent.putExtra(getString(R.string.EXTRA_FILTER_BY_CATEGORY), false);
//
//        setResult(RESULT_OK, intent);
//        finish();
//    }
//
//    public void save(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//
//        if (start == null || end == null) {
//            intent.putExtra(getString(R.string.EXTRA_FILTER_BY_DATE), false);
//        } else {
//            intent.putExtra(getString(R.string.EXTRA_FILTER_BY_DATE), true);
//            intent.putExtra(getString(R.string.EXTRA_FILTER_DATE_START), start.toEpochDay());
//            intent.putExtra(getString(R.string.EXTRA_FILTER_DATE_END), end.toEpochDay());
//        }
//
//        category = categoryText.getText().toString();
//        intent.putExtra(getString(R.string.EXTRA_FILTER_BY_CATEGORY), true);
//        intent.putExtra(getString(R.string.EXTRA_FILTER_CATEGORY), category);
//
//        setResult(RESULT_OK, intent);
//        finish();
//    }
//
//    private LocalDate getToday() {
//        return LocalDate.now().atStartOfDay().toLocalDate();
//    }
//
//    public void setDateToday(View view) {
//        LocalDate today = getToday();
//        start = today;
//        end = today;
//        setDateText();
//    }
//
//    public void setDateYesterday(View view) {
//        LocalDate yesterday = getToday().minusDays(1);
//        start = yesterday;
//        end = yesterday;
//        setDateText();
//    }
//
//    public void setDateSpecificDay(View view) {
//        if (datePicker == null) {
//            datePicker = MaterialDatePicker.Builder.datePicker()
//                    .build();
//            datePicker.addOnPositiveButtonClickListener(dateEpoch -> {
//                LocalDate date = LocalDate.ofEpochDay(Duration.ofMillis(dateEpoch).toDays());
//                start = date;
//                end = date;
//                setDateText();
//            });
//        }
//
//        datePicker.show(getSupportFragmentManager(), TAG);
//    }
//
//    public void setDateThisWeek(View view) {
//        LocalDate today = getToday();
//        TemporalField dayOfWeek = WeekFields.of(Locale.getDefault()).dayOfWeek();
//        LocalDate startOfWeek = today.with(dayOfWeek, 1);
//        LocalDate endOfWeek = today.with(dayOfWeek, 7);
//        start = startOfWeek;
//        end = endOfWeek;
//        setDateText();
//    }
//
//    public void setDateLastWeek(View view) {
//        LocalDate today = getToday();
//        TemporalField dayOfWeek = WeekFields.of(Locale.getDefault()).dayOfWeek();
//        LocalDate startOfWeek = today.minusWeeks(1).with(dayOfWeek, 1);
//        LocalDate endOfWeek = today.minusWeeks(1).with(dayOfWeek, 7);
//        start = startOfWeek;
//        end = endOfWeek;
//        setDateText();
//    }
//
//    public void setDateThisMonth(View view) {
//        LocalDate today = getToday();
//        start = today.with(TemporalAdjusters.firstDayOfMonth());
//        end = today.with(TemporalAdjusters.lastDayOfMonth());
//        setDateText();
//    }
//
//    public void setDateLastMonth(View view) {
//        LocalDate today = getToday();
//        start = today.with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1);
//        end = start.with(TemporalAdjusters.lastDayOfMonth());
//        setDateText();
//    }
//
//    public void setDateCustomRange(View view) {
//        if (dateRangePicker == null) {
//            dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
//                    .build();
//            dateRangePicker.addOnPositiveButtonClickListener(dateRange -> {
//                start = LocalDate.ofEpochDay(Duration.ofMillis(dateRange.first).toDays());
//                end = LocalDate.ofEpochDay(Duration.ofMillis(dateRange.second).toDays());
//                setDateText();
//            });
//        }
//
//        dateRangePicker.show(getSupportFragmentManager(), TAG);
//    }
}