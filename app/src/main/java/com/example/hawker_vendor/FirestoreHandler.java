package com.example.hawker_vendor;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FirestoreHandler {

    public static final FirestoreHandler instance = new FirestoreHandler();

    private static final String COL_HAWKERS = "hawkers";
    private static final String COL_ITEMS = "items";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FirestoreHandler getInstance() {
        return instance;
    }

    private FirestoreHandler() {
    }

    public Query queryHawkerItems(String uid) {
        return db.collection(COL_HAWKERS)
                .document(uid)
                .collection(COL_ITEMS);
    }

    public Task<Void> setHawker(String uid, Map<String, Object> data) {
        return db.collection(COL_HAWKERS)
                .document(uid)
                .set(data);
    }

    public Task<DocumentSnapshot> getHawker(String uid) {
        return db.collection(COL_HAWKERS)
                .document(uid)
                .get();
    }

    public Task<Void> updateHawker(String uid, Map<String, Object> data) {
        return db.collection(COL_HAWKERS)
                .document(uid)
                .update(data);
    }
}
