package com.example.expense_tracking_app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.expense_tracking_app.adapters.ViewPagerFragmentAdapter;
import com.example.expense_tracking_app.services.ExpenseCategoryRepository;
import com.example.expense_tracking_app.services.ExpenseRepository;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import javax.inject.Inject;

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
    }
}
