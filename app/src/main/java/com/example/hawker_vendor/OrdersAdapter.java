package com.example.hawker_vendor;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class OrdersAdapter extends FirebaseRecyclerAdapter<Order, OrdersAdapter.OrderViewHolder> {

    private static final String TAG = "OrdersAdapter";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseHandler firebaseHandler = FirebaseHandler.getInstance();
    private FirestoreHandler firestoreHandler = FirestoreHandler.getInstance();
    private Context context;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public OrdersAdapter(@NonNull FirebaseRecyclerOptions<Order> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull final Order model) {
        holder.tvItem.setText(model.getItemName());
        holder.tvTableNo.setText(model.getTableNo());
        holder.tvQty.setText(String.valueOf(model.getItemQty()));
        holder.tvPrice.setText(String.format("%.2f", model.getTotal()));

        switch (model.getCompletion()) {
            case -1:
                holder.tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary));

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Log.d(TAG, "order click: " + model.toString());

                        new MaterialAlertDialogBuilder(context)
                                .setTitle(R.string.dialog_completion_title)
                                .setMessage(R.string.dialog_completion_message)
                                .setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        firebaseHandler.setOrderCompletion(model.getId(), 0);
                                    }
                                })
                                .setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        firebaseHandler.setOrderCompletion(model.getId(), 1);
                                        firestoreHandler.incrementItemQuantity(auth.getCurrentUser().getUid(), model.getItemId(), model.getItemQty());
                                    }
                                })
                                .setNeutralButton(R.string.cancel, null)
                                .show();

                        return true;
                    }
                });
                break;

            case 0:
                holder.tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorPositive));
                holder.itemView.setOnLongClickListener(null);
                break;

            case 1:
                holder.tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorNegative));
                holder.itemView.setOnLongClickListener(null);
                break;
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_order, parent, false);

        return new OrderViewHolder(view);
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItem, tvTableNo, tvQty, tvPrice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItem = itemView.findViewById(R.id.item);
            tvTableNo = itemView.findViewById(R.id.table);
            tvQty = itemView.findViewById(R.id.qty);
            tvPrice = itemView.findViewById(R.id.price);
        }
    }
}
