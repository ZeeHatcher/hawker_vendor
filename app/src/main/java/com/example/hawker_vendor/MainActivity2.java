package com.example.hawker_vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity2 extends AppCompatActivity implements NavigationHost, MaterialToolbar.OnMenuItemClickListener {

    private static final String TAG = "Main2";
    private MaterialToolbar appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        appBar = findViewById(R.id.top_app_bar);
        appBar.setOnMenuItemClickListener(this);

        navigateTo(PagerFragment.newInstance(), false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setAppBarTitle(CharSequence title) {
        appBar.setTitle(title);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick:" + item.getTitle());

        switch (item.getItemId()) {
            case R.id.logout:
                break;

            case R.id.close:
                break;
        }
        return false;
    }

    @Override
    public void navigateActivity() {

    }

    @Override
    public void navigateTo(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public void pop() {
        getSupportFragmentManager().popBackStack();
    }
}