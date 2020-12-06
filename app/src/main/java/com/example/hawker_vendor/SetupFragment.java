package com.example.hawker_vendor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "Setup";
    private static final int REQUEST_CODE = 1;

    private FirebaseAuth auth;
    private TextInputEditText etName, etStore;
    private TextInputLayout tlName, tlStore;
    private String storeId;

    public SetupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SetupFragment.
     */
    public static SetupFragment newInstance() {
        SetupFragment fragment = new SetupFragment();
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
        View view = inflater.inflate(R.layout.fragment_setup, container, false);

        etName = view.findViewById(R.id.stall_name_edit_text);
        etStore = view.findViewById(R.id.store_edit_text);

        tlName = view.findViewById(R.id.stall_name_input_text);
        tlStore = view.findViewById(R.id.store_input_text);

        Button buttonConfirm = view.findViewById(R.id.button_confirm);

        etStore.setOnClickListener(this);
        buttonConfirm.setOnClickListener(this);

        return view;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.store_edit_text:
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;

            case R.id.button_confirm:
                boolean isValid = true;

                tlName.setError(null);
                tlStore.setError(null);

                if (etName.getText().toString().isEmpty()) {
                    tlName.setError(getString(R.string.error_stall_empty));
                    isValid = false;
                }

                if (storeId == null) {
                    tlStore.setError(getString(R.string.error_store_empty));
                    isValid = false;
                }

                if (!isValid) return;

                Map<String, Object> docData = new HashMap<>();
                docData.put("storeId", storeId);
                docData.put("name", etName.getText().toString());
                docData.put("isOpen", false);

                FirestoreHandler.getInstance()
                        .setHawker(auth.getCurrentUser().getUid(), docData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ((NavigationHost) getContext()).navigateActivity();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "set hawker:failure", e);
                            }
                        });

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult:success");

            storeId = data.getStringExtra(MapsActivity.KEY_ID);
            etStore.setText(data.getStringExtra(MapsActivity.KEY_NAME));
        }
    }
}