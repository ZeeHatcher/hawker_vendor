package com.example.hawker_vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements NavigationHost {

    private static String TAG = "Main";
    private static String KEY_FRAGMENT = "fragment";

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Fragment setupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            setupFragment = getSupportFragmentManager().getFragment(savedInstanceState, KEY_FRAGMENT);
        }

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkHawkerIsSetup();
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

    public void checkHawkerIsSetup() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Check if hawker has already setup
            db.collection("hawkers")
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document.exists()) {
                                    // Go to main app if already setup
                                    Log.d(TAG, "get hawker:exist");

                                    goToApp();
                                } else {
                                    // Go to setup page
                                    Log.d(TAG, "get hawker:not exist");

                                    setupFragment = (setupFragment == null) ? SetupFragment.newInstance() : setupFragment;
                                    navigateTo(setupFragment, false);
                                }
                            } else {
                                Log.w(TAG, "get hawker:failure", task.getException());
                            }
                        }
                    });
        } else {
            navigateTo(LoginFragment.newInstance(), false);
        }
    }

    public void goToApp() {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, KEY_FRAGMENT, setupFragment);
    }
}