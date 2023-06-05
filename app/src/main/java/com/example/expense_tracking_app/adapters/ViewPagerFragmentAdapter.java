package com.example.expense_tracking_app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.expense_tracking_app.ExpenseListFragment;
import com.example.expense_tracking_app.SummaryFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

    public ViewPagerFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ExpenseListFragment();
            case 1:
                return new SummaryFragment();
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}