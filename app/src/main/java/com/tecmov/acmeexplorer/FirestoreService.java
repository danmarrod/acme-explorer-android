package com.tecmov.acmeexplorer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tecmov.acmeexplorer.entity.Trip;

public class FirestoreService {

    private static String userId;
    private static FirebaseFirestore mDatabase;
    private static FirestoreService service;


    // Patrón factoría, nos devuelve una instancia el servicio si está creado
    public static FirestoreService getServiceInstance() {
        if (service == null || mDatabase == null) {
            service = new FirestoreService();
            mDatabase = FirebaseFirestore.getInstance();
        }
        if(userId == null || userId.isEmpty()) {
            userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        }
        return service;
    }

    public void saveTrip(Trip trip, OnCompleteListener<DocumentReference> listener) {
        mDatabase.collection("users").document(userId).collection("trips").add(trip).addOnCompleteListener(listener);
    }

    public void getTrips(OnCompleteListener<QuerySnapshot> querySnapshotOnCompleteListener){
        mDatabase.collection("users").document(userId).collection("trips").get().addOnCompleteListener(querySnapshotOnCompleteListener);
    }

    public void getTripsFiltered(OnCompleteListener<QuerySnapshot> querySnapshotOnCompleteListener){
        mDatabase.collection("users").document(userId).collection("trips").whereGreaterThanOrEqualTo("price",32.0).orderBy("price", Query.Direction.ASCENDING).limit(3).get().addOnCompleteListener(querySnapshotOnCompleteListener);
    }

    public void getTrip(String id, EventListener<DocumentSnapshot> snapshotListener){
        mDatabase.collection("users").document(userId).collection("trips").document(id).addSnapshotListener(snapshotListener);

    }
}