package com.example.hawker_vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity2 extends AppCompatActivity implements NavigationHost, MaterialToolbar.OnMenuItemClickListener {

    private static final String TAG = "Main2";

    private MaterialToolbar appBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        auth = FirebaseAuth.getInstance();

        appBar = findViewById(R.id.top_app_bar);
        appBar.setOnMenuItemClickListener(this);

        appBar.getMenu().findItem(R.id.close).setVisible(false);

        navigateTo(PagerFragment.newInstance(), false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick:" + item.getTitle());

        switch (item.getItemId()) {
            case R.id.logout:
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.dialog_logout_title)
                        .setMessage(R.string.dialog_logout_message)
                        .setPositiveButton(R.string.dialog_logout_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                auth.signOut();
                                navigateActivity();
                            }
                        })
                        .setNegativeButton(R.string.dialog_logout_no, null)
                        .show();

                break;

            case R.id.close:
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.dialog_close_title)
                        .setMessage(R.string.dialog_close_message)
                        .setPositiveButton(R.string.dialog_close_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setNegativeButton(R.string.dialog_close_no, null)
                        .show();

                break;
        }

        return true;
    }

    @Override
    public void navigateActivity() {
        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
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

    public void setAppBarTitle(CharSequence title) {
        appBar.setTitle(title);
    }
}