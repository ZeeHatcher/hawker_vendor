package com.example.hawker_vendor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubFragment extends Fragment {

    private static final String TAG = "Sub";

    private FirebaseAuth auth;

    public SubFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SubFragment.
     */
    public static SubFragment newInstance() {
        SubFragment fragment = new SubFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub, container, false);

        FirestoreHandler.getInstance()
                .getHawker(auth.getCurrentUser().getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Log.w(TAG, "get hawker:success");

                        boolean isOpen = documentSnapshot.get("isOpen", Boolean.class);

                        replaceFragment(isOpen);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "get hawker:failure", e);
                    }
                });

        return view;
    }

    public void setStallOpen(final boolean isOpen) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("isOpen", isOpen);

        FirestoreHandler.getInstance()
                .updateHawker(auth.getCurrentUser().getUid(), docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        replaceFragment(isOpen);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "update hawker:failure", e);
                    }
                });
    }

    private void replaceFragment(boolean isOpen) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.subcontainer, (isOpen) ? OrdersFragment.newInstance() : ClosedFragment.newInstance())
                .commit();
    }
}