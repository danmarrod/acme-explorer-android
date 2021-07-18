package com.tecmov.acmeexplorer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tecmov.acmeexplorer.entity.Trip;
import com.tecmov.acmeexplorer.entity.User;

public class    FirestoreService {

    private static String userId;
    private static FirebaseFirestore mDatabase;
    private static FirestoreService service;


    // Patrón factoría, nos devuelve una instancia el servicio si está creado
    public static FirestoreService getServiceInstance() {
        if (service == null || mDatabase == null) {
            service = new FirestoreService();
            mDatabase = FirebaseFirestore.getInstance();
        }
        if (userId == null || userId.isEmpty()) {
            userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        }
        return service;
    }

    public void saveTrip(Trip trip, OnCompleteListener<DocumentReference> listener) {
        mDatabase.collection("users").document(userId).collection("trips").add(trip).addOnCompleteListener(listener);
    }

    // get a collection trips only once, not for future updates
    public void getTrips(OnCompleteListener<QuerySnapshot> querySnapshotOnCompleteListener) {
        mDatabase.collection("users").document(userId).collection("trips").get().addOnCompleteListener(querySnapshotOnCompleteListener);
    }

    public void getTripsFiltered(OnCompleteListener<QuerySnapshot> querySnapshotOnCompleteListener) {
        mDatabase.collection("users").document(userId).collection("trips").whereGreaterThanOrEqualTo("price", 32.0).orderBy("price", Query.Direction.ASCENDING).limit(3).get().addOnCompleteListener(querySnapshotOnCompleteListener);
    }

    public void getTrip(String id, EventListener<DocumentSnapshot> snapshotListener) {
        mDatabase.collection("users").document(userId).collection("trips").document(id).addSnapshotListener(snapshotListener);

    }

    // get a collection trips like getTrips before but this case we'll get future updates
    public ListenerRegistration getTrips(EventListener<QuerySnapshot> querySnapshotOnCompleteListener) {
        return mDatabase.collection("users").document(userId).collection("trips").addSnapshotListener(querySnapshotOnCompleteListener);
    }

    public ListenerRegistration getAllTrips(EventListener<QuerySnapshot> querySnapshotOnCompleteListener) {
        return mDatabase.collectionGroup("trips").addSnapshotListener(querySnapshotOnCompleteListener);
    }

    public void saveUser(User user, OnCompleteListener<DocumentReference> listener) {
        mDatabase.collection("users").document(userId).set(user);
    }

    public void getUser(EventListener<DocumentSnapshot> snapshotListener) {
        mDatabase.collection("users").document(userId).addSnapshotListener(snapshotListener);
    }

    public void saveTripLike(Trip trip, OnCompleteListener<DocumentReference> listener) {
        mDatabase.collection("users").document(userId).collection("tripsLikes").add(trip).addOnCompleteListener(listener);
    }

    public ListenerRegistration getTripsLike(EventListener<QuerySnapshot> querySnapshotOnCompleteListener) {
        return mDatabase.collection("users").document(userId).collection("tripsLikes").addSnapshotListener(querySnapshotOnCompleteListener);
    }

    public void getTripsLiked(OnCompleteListener<QuerySnapshot> querySnapshotOnCompleteListener) {
        mDatabase.collection("users").document(userId).collection("tripsLikes").get().addOnCompleteListener(querySnapshotOnCompleteListener);
    }

    public void removeTrip(String id) {
        mDatabase.collection("users").document(userId).collection("tripsLikes").document(id).delete();
    }
}
