package com.example.hawker_vendor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PagerFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ViewPager2 viewPager;

    public PagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PagerFragment.
     */
    public static PagerFragment newInstance() {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pager, container, false);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        viewPager = view.findViewById(R.id.view_pager);

        PagerAdapter pagerAdapter = new PagerAdapter(getActivity());
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(pagerAdapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.page_orders);

        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        ((MainActivity2) getContext()).setAppBarTitle(item.getTitle());

        switch (item.getItemId()) {
            case R.id.page_orders:
                viewPager.setCurrentItem(0);
                return true;

            case R.id.page_manage:
                viewPager.setCurrentItem(1);
                return true;

            case R.id.page_settings:
                viewPager.setCurrentItem(2);
                return true;
        }

        return false;
    }
}