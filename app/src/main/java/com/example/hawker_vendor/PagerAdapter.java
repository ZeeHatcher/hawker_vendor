package com.example.hawker_vendor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {

    private static int NUM_FRAGMENTS = 3;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ManageFragment();

            case 2:
                return new SettingsFragment();

            default:
                return new OrdersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_FRAGMENTS;
    }
}
