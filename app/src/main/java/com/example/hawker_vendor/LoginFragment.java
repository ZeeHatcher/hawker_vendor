package com.example.hawker_vendor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private static String TAG = "Login";

    private FirebaseAuth auth;
    FirebaseFirestore db;
    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tlEmail, tlPassword;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etEmail = view.findViewById(R.id.email_edit_text);
        etPassword = view.findViewById(R.id.password_edit_text);

        tlEmail = view.findViewById(R.id.email_input_text);
        tlPassword = view.findViewById(R.id.password_input_text);

        Button buttonLogin = view.findViewById(R.id.button_login);
        Button buttonRegister = view.findViewById(R.id.button_register);

        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                boolean isValid = true;

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (email.isEmpty()) {
                    tlEmail.setError(getString(R.string.error_email_empty));
                    isValid = false;
                }

                if (password.isEmpty()) {
                    tlPassword.setError(getString(R.string.error_password_empty));
                    isValid = false;
                }

                if (!isValid) return;

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmailAndPassword:success");

                                    ((MainActivity) getActivity()).checkHawkerIsSetup();
                                } else {
                                    Log.w(TAG, "signInWithEmailAndPassword:failure", task.getException());

                                    Toast.makeText(getContext(), "Login failed.", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });

                break;

            case R.id.button_register:
                ((NavigationHost) getActivity()).navigateTo(RegisterFragment.newInstance(), true);
                break;
        }
    }
}