package com.example.hawker_vendor;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "Orders";
    private FirebaseAuth auth;
    private FirebaseHandler firebaseHandler;
    private FirestoreHandler firestoreHandler;
    private OrdersAdapter adapter;

    public OrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrdersFragment.
     */
    public static OrdersFragment newInstance() {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firebaseHandler = FirebaseHandler.getInstance();
        firestoreHandler = FirestoreHandler.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        Button buttonClose = view.findViewById(R.id.button_close);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        Query query = firebaseHandler.getOrders(auth.getCurrentUser().getUid());

        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(query, new SnapshotParser<Order>() {
                    @NonNull
                    @Override
                    public Order parseSnapshot(@NonNull DataSnapshot snapshot) {
                        Order order = snapshot.getValue(Order.class);
                        order.setId(snapshot.getKey());

                        return order;
                    }
                })
                .build();

        adapter = new OrdersAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        buttonClose.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        adapter.stopListening();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_close:
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.dialog_close_title)
                        .setMessage(R.string.dialog_close_message)
                        .setPositiveButton(R.string.dialog_close_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((SubFragment) getParentFragment()).setStallOpen(false);

                                saveStats();
                                restock();
                            }
                        })
                        .setNegativeButton(R.string.dialog_close_no, null)
                        .show();

                break;
        }
    }

    private void saveStats() {
        firebaseHandler.getOrders(auth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        /*
                            Save data:
                                - Total revenue
                                    - Item revenue
                                - Total items sold
                                    - Item quantity
                                - Total orders
                                    - Completed
                                    - Not completed/Declined
                         */
                        int countComplete = 0;
                        int countIncomplete = 0;
                        int totalSold = 0;
                        float totalRevenue = 0f;
                        Map<String, Float> itemsRevenue = new HashMap<>();
                        Map<String, Integer> itemsSold = new HashMap<>();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            Order order = s.getValue(Order.class);

                            if (order.getCompletion() == 0) {
                                float revenue = order.getTotal();
                                int qty = order.getItemQty();
                                String itemName = order.getItemName();

//                                if (!itemRevenue.containsKey(itemName)) {
//                                    itemRevenue.put(itemName, 0f);
//                                }
//                                if (!itemSold.containsKey(itemName)) {
//                                    itemSold.put(itemName, 0);
//                                }

                                totalRevenue += revenue;
                                itemsRevenue.put(itemName, itemsRevenue.getOrDefault(itemName, 0f) + revenue);

                                itemsSold.put(itemName, itemsSold.getOrDefault(itemName, 0) + qty);
                                totalSold += qty;

                                countComplete++;
                            } else {
                                countIncomplete++;
                            }
                        }

                        Map<String, Object> docData = new HashMap<>();
                        docData.put("countOrderComplete", countComplete);
                        docData.put("countOrderIncomplete", countIncomplete);
                        docData.put("totalRevenue", totalRevenue);
                        docData.put("itemsRevenue", itemsRevenue);
                        docData.put("totalSold", totalSold);
                        docData.put("itemsSold", itemsSold);
                        docData.put("createdAt", new Timestamp(Calendar.getInstance().getTime()));

                        firestoreHandler.addStat(auth.getCurrentUser().getUid(), docData)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "addStat:fail", e);
                                    }
                                });

                        deleteOrders();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void deleteOrders() {
        firebaseHandler.getOrders(auth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot s : snapshot.getChildren()) {
                            Log.d(TAG, "onDataChange:" + s.getKey());

                            s.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled", error.toException());
                    }
                });
    }

    private void restock() {
        firestoreHandler.getHawkerItems(auth.getCurrentUser().getUid())
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> dataDoc = new HashMap<>();
                            dataDoc.put("currentStock", snapshot.get("dailyStock", Integer.class));

                            snapshot.getReference()
                                    .update(dataDoc);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "get hawker items:fail", e);
                    }
                });
    }

}