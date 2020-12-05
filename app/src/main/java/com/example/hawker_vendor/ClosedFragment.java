package com.example.hawker_vendor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClosedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClosedFragment extends Fragment implements View.OnClickListener {

    public ClosedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ClosedFragment.
     */
    public static ClosedFragment newInstance() {
        ClosedFragment fragment = new ClosedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_closed, container, false);

        Button buttonOpen = view.findViewById(R.id.button_open);

        buttonOpen.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_open:
                ((SubFragment) getParentFragment()).replaceFragment(OrdersFragment.newInstance());

                break;
        }
    }
}