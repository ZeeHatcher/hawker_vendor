package com.example.hawker_vendor;

import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {

    private static final String TAG = "Stats";
    private FirebaseAuth auth;
    private FirestoreHandler firestoreHandler;
    private LineChart chart;
    private TextView tvTotalRevenue, tvDetailedRevenue, tvTotalSold, tvDetailedSold, tvTotalOrders, tvDetailedOrders;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatsFragment.
     */
    public static StatsFragment newInstance() {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firestoreHandler = FirestoreHandler.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        chart = view.findViewById(R.id.line_chart);
        tvDetailedOrders = view.findViewById(R.id.detailed_orders);
        tvDetailedRevenue = view.findViewById(R.id.detailed_revenue);
        tvDetailedSold = view.findViewById(R.id.detailed_sold);
        tvTotalOrders = view.findViewById(R.id.total_orders);
        tvTotalRevenue = view.findViewById(R.id.total_revenue);
        tvTotalSold = view.findViewById(R.id.total_sold);

        // Chart Style
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        firestoreHandler.refHawker(auth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        if (value != null && value.exists()) {
                            if (value.get("stats") != null) {
                                Map<String, Object> stats = (Map<String, Object>) value.get("stats");
                                Map<String, Float> itemsRevenue = (Map<String, Float>) stats.get("itemsRevenue");
                                Map<String, Float> itemsSold = (Map<String, Float>) stats.get("itemsSold");

                                tvTotalRevenue.setText(String.format("RM%.2f", (double) stats.get("totalRevenue")));
                                String detailedRevenue = "";
                                for (Map.Entry<String, Float> entry : itemsRevenue.entrySet()) {
                                    detailedRevenue += String.format("%s: RM%.2f\n", entry.getKey(), entry.getValue());
                                }
                                tvDetailedRevenue.setText(detailedRevenue);

                                tvTotalSold.setText(String.format("Total Items Sold: %.0f", (double) stats.get("totalSold")));
                                String detailedSold = "";
                                for (Map.Entry<String, Float> entry : itemsSold.entrySet()) {
                                    detailedSold += String.format("%s: %.0f\n", entry.getKey(), entry.getValue());
                                }
                                tvDetailedSold.setText(detailedSold);

                                double completeOrders = (double) stats.get("countOrderComplete");
                                double incompleteOrders = (double) stats.get("countOrderIncomplete");
                                tvTotalOrders.setText(String.format("Total Orders: %.0f", completeOrders + incompleteOrders));
                                tvDetailedOrders.setText(String.format("Completed: %.0f\nNot Completed: %.0f", completeOrders, incompleteOrders));
                            } else {
                                tvTotalRevenue.setText("RM0.00");
                                tvTotalSold.setText("Total Items Sold: 0");
                                tvTotalOrders.setText("Total Order: 0");
                                tvDetailedSold.setText("-\n");
                                tvDetailedOrders.setText("-\n");
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });

        firestoreHandler.refHawkerStats(auth.getCurrentUser().getUid())
                .orderBy("createdAt")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        if (querySnapshot != null) {
                            ArrayList<Entry> values = new ArrayList<>();

                            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                            for (int i = 0; i < documents.size(); i++) {
                                DocumentSnapshot doc = documents.get(i);

                                values.add(new Entry(i,  doc.get("totalRevenue", Float.class)));
                            }

                            if (!values.isEmpty()) {
                                setData(values);
                                chart.animateX(250);
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    private void setData(List<Entry> values) {
        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "Revenue");

            set1.setDrawIcons(false);

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(ContextCompat.getColor(getContext(), R.color.colorTextSecondary));
            set1.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorTextSecondary));

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            set1.setDrawValues(false);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            chart.setData(data);
        }

        set1.notifyDataSetChanged();
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
    }

}