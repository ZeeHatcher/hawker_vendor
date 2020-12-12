package com.example.hawker_vendor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class NotificationJobService extends JobService {

    private static final String CHANNEL_ID = "com.example.hawker_vendor.NOTIFICATION";
    private static final String TAG = "NotificationJobService";

    private ValueEventListener listener;
    private FirebaseHandler firebaseHandler;
    private FirebaseUser currentUser;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "service notification trigger");
                Intent intent = new Intent(NotificationJobService.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(NotificationJobService.this, 0, intent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationJobService.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_storefront)
                        .setContentTitle("Updated Orders")
                        .setContentText("There are new changes to the order list.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationJobService.this);
                notificationManager.notify(0, builder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        firebaseHandler.getOrders(currentUser.getUid())
                .addValueEventListener(listener);

        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseHandler = FirebaseHandler.getInstance();

        createNotificationChannel();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "service notification stop");
        firebaseHandler.getOrders(currentUser.getUid())
                .removeEventListener(listener);

        return true;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
