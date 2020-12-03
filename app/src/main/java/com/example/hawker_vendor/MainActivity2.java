package com.example.hawker_vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ViewPager2 viewPager;
    MaterialToolbar appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        appBar = findViewById(R.id.top_app_bar);
        viewPager = findViewById(R.id.view_pager);

        PagerAdapter pagerAdapter = new PagerAdapter(this);
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(pagerAdapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.page_orders);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        appBar.setTitle(item.getTitle());

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