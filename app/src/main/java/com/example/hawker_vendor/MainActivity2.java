package com.example.hawker_vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity2 extends AppCompatActivity implements NavigationHost, WidgetManager, MaterialToolbar.OnMenuItemClickListener {

    private static final String TAG = "Main2";
    private static final int JOB_ID = 0;

    private MaterialToolbar appBar;
    private FirebaseAuth auth;
    private JobScheduler jobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        auth = FirebaseAuth.getInstance();
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        appBar = findViewById(R.id.top_app_bar);
        appBar.setOnMenuItemClickListener(this);

        if (auth.getCurrentUser() == null) {
            signOut();
        }

        scheduleJob();

        navigateTo(PagerFragment.newInstance(), false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            navigateActivity();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.dialog_logout_title)
                        .setMessage(R.string.dialog_logout_message)
                        .setPositiveButton(R.string.dialog_logout_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                signOut();
                            }
                        })
                        .setNegativeButton(R.string.dialog_logout_no, null)
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

    @Override
    public void setMenuItemVisible(int id, boolean isVisible) {
        appBar.getMenu().findItem(id).setVisible(isVisible);
    }

    private void scheduleJob() {
        ComponentName notificationJobService = new ComponentName(getPackageName(), NotificationJobService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, notificationJobService);
        JobInfo jobInfo = builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        jobScheduler.schedule(jobInfo);
    }

    private void signOut() {
        jobScheduler.cancelAll();
        auth.signOut();
        navigateActivity();
    }
}