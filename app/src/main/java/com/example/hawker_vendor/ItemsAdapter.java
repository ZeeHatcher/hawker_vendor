package com.example.hawker_vendor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ItemsAdapter extends FirestoreRecyclerAdapter<Item, ItemsAdapter.ItemViewHolder> {

    private static final String TAG = "ItemsAdapter";

    private Context context;
    private FirebaseStorage storage;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ItemsAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        storage = FirebaseStorage.getInstance();
        context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull final Item model) {
        holder.name.setText(model.getName());
        holder.price.setText(String.format("RM%.2f", model.getPrice()));
        holder.dailyStock.setText("Daily Stock: " + model.getDailyStock());
        holder.currentStock.setText("Current Stock: " + model.getCurrentStock());

        StorageReference storageReference = storage.getReference().child(model.getImagePath());
        GlideApp.with(context)
                .load(storageReference)
                .into(holder.image);

        holder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick edit: " + model.toString());

                ((NavigationHost) context).navigateTo(FormFragment.newInstance(model), true);
            }
        });
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name, price, dailyStock, currentStock;
        public ImageView image;
        public ImageButton buttonEdit;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            dailyStock = itemView.findViewById(R.id.daily_stock);
            currentStock = itemView.findViewById(R.id.current_stock);
            image = itemView.findViewById(R.id.image);
            buttonEdit = itemView.findViewById(R.id.button_edit);
        }
    }
}
