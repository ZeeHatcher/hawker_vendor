package com.example.hawker_vendor;

import android.content.Context;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private static String TAG = "Register";

    private FirebaseAuth auth;
    private Context context;
    private TextInputEditText etEmail, etPassword, etConfirm;
    private TextInputLayout tlEmail, tlPassword, tlConfirm;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RegisterFragment.
     */
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etEmail = view.findViewById(R.id.email_edit_text);
        etPassword = view.findViewById(R.id.password_edit_text);
        etConfirm = view.findViewById(R.id.confirm_edit_text);

        tlEmail = view.findViewById(R.id.email_input_text);
        tlPassword = view.findViewById(R.id.password_input_text);
        tlConfirm = view.findViewById(R.id.confirm_input_text);

        Button buttonCancel = view.findViewById(R.id.button_cancel);
        Button buttonRegister = view.findViewById(R.id.button_register);

        buttonCancel.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_cancel:
                ((NavigationHost) getActivity()).pop();
                break;

            case R.id.button_register:
                boolean isValid = true;

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirm = etConfirm.getText().toString();

                tlEmail.setError(null);
                tlPassword.setError(null);
                tlConfirm.setError(null);

                if (!isEmailValid(email)) {
                    tlEmail.setError(getString(R.string.error_email));
                    isValid = false;
                }

                if (password.length() < 8) {
                    tlPassword.setError(getString(R.string.error_password));
                    isValid = false;
                }

                if (!password.equals(confirm)) {
                    tlConfirm.setError(getString(R.string.error_confirm));
                    isValid = false;
                }

                if (!isValid) return;

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmailAndPassword:success");

                                    Toast.makeText(getContext(), "Successfully created a new account.", Toast.LENGTH_SHORT)
                                            .show();

                                    ((NavigationHost) getActivity()).pop();
                                } else {
                                    Log.w(TAG, "createUserWithEmailAndPassword:failure", task.getException());

                                    Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });

                break;
        }
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}