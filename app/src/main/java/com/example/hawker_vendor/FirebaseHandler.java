package com.example.hawker_vendor;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FirebaseHandler {

    public static final FirebaseHandler instance = new FirebaseHandler();

    private static final String KEY_ORDERS = "orders";
    private static final String KEY_HAWKER_ID = "hawkerId";
    private static final String KEY_COMPLETION = "completion";

    public static FirebaseHandler getInstance() {
        return instance;
    }

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    public FirebaseHandler() {
    }

    public Query getOrders(String uid) {
        return db.getReference()
                .child(KEY_ORDERS)
                .orderByChild(KEY_HAWKER_ID)
                .equalTo(uid);
    }

    public void setOrderCompletion(String id, int c) {
        db.getReference()
                .child(KEY_ORDERS)
                .child(id)
                .child(KEY_COMPLETION)
                .setValue(c);
    }
}
