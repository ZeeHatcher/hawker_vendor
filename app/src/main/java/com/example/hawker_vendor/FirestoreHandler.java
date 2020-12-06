package com.example.hawker_vendor;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public CollectionReference refHawkerItems(String uid) {
        return db.collection(COL_HAWKERS)
                .document(uid)
                .collection(COL_ITEMS);
    }

    public DocumentReference refHawker(String uid) {
        return db.collection(COL_HAWKERS)
                .document(uid);
    }

    public Task<Void> setHawker(String uid, Map<String, Object> data) {
        return refHawker(uid)
                .set(data);
    }

    public Task<DocumentSnapshot> getHawker(String uid) {
        return refHawker(uid)
                .get();
    }

    public Task<Void> updateHawker(String uid, Map<String, Object> data) {
        return refHawker(uid)
                .update(data);
    }

    public DocumentReference refHawkerItem(String uid, String itemId) {
        return refHawker(uid).collection(COL_ITEMS)
                .document(itemId);
    }

    public Task<DocumentReference> addHawkerItem(String uid, Map<String, Object> data) {
        return refHawkerItems(uid)
                .add(data);
    }

    public Task<DocumentSnapshot> getHawkerItem(String uid, String itemId) {
        return refHawkerItem(uid, itemId)
                .get();
    }

    public Task<Void> updateHawkerItem(String uid, String itemId, Map<String, Object> data) {
        return refHawkerItem(uid, itemId)
                .update(data);
    }

    public Task<Void> deleteHawkerItem(String uid, String itemId) {
        return refHawkerItem(uid, itemId)
                .delete();
    }
}
