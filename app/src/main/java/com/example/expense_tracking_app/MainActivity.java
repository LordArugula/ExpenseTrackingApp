package com.example.expense_tracking_app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.expense_tracking_app.adapters.ViewPagerFragmentAdapter;
import com.example.expense_tracking_app.viewmodels.ExpenseViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private static void onConfigureTab(TabLayout.Tab tab, int position) {
        switch (position) {
            case 0:
                tab.setText("Expenses");
            case 1:
                tab.setText("Summary");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager);

        ViewPagerFragmentAdapter viewPagerAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager2, MainActivity::onConfigureTab)
                .attach();

        ExpenseViewModel expenseViewModel = new ViewModelProvider(this)
                .get(ExpenseViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                SortSideSheetDialogFragment sortDialog = new SortSideSheetDialogFragment();
                sortDialog.setCancelable(true);
                sortDialog.show(getSupportFragmentManager(), sortDialog.getTag());
                return true;
            case R.id.action_filter:
                FilterSideSheetDialogFragment filterDialog = new FilterSideSheetDialogFragment();
                filterDialog.setCancelable(true);
                filterDialog.show(getSupportFragmentManager(), filterDialog.getTag());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
