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
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.FirebaseDatabase;

public class OrdersAdapter extends FirebaseRecyclerAdapter<Order, OrdersAdapter.OrderViewHolder> {

    private static final String TAG = "OrdersAdapter";
    private FirebaseHandler handler;
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
            case 1:
                holder.tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorPositive));
                break;

            case -1:
                holder.tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorNegative));
                break;
        }

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
                                handler.setOrderCompletion(model.getId(), 1);
                            }
                        })
                        .setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                handler.setOrderCompletion(model.getId(), -1);
                            }
                        })
                        .setNeutralButton(R.string.cancel, null)
                        .show();

                return true;
            }
        });
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        handler = FirebaseHandler.getInstance();
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
