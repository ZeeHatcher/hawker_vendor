package com.example.hawker_vendor;

import androidx.fragment.app.Fragment;

public interface NavigationHost {
    void navigateActivity();
    void navigateTo(Fragment fragment, boolean addToBackStack);
    void pop();
}