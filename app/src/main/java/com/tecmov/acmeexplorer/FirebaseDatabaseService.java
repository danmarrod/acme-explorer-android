package com.tecmov.acmeexplorer;

import android.content.res.Resources;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseService {
    private static String userId;
    private static FirebaseDatabaseService service;
    private static FirebaseDatabase mDatabase;
    //datebase outside us-store needs set firebase url in connection
    private static String firebase_url = "https://acmeexplorer-1cff3-default-rtdb.europe-west1.firebasedatabase.app/";

    // Patrón factoría, nos devuelve una instancia el servicio si está creado
    public static FirebaseDatabaseService getServiceInstance() {
        if (service == null || mDatabase == null) {
            service = new FirebaseDatabaseService();
            mDatabase = FirebaseDatabase.getInstance(firebase_url);
            // Activamos la persistencia, permite almacenamiento local en caso de perdida de conexión
            // Cuando vuelve conexión sincroniza automáticamente
            mDatabase.setPersistenceEnabled(true);
        }

        if(userId == null || userId.isEmpty()) {
            //  el usuario ya ha hecho login en el sistema
            userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        }
        return service;
    }

    public DatabaseReference getTrips(String tripId) {
        // Reference, ruta de la estructura de datos donde se encuentre el identificador
        String user = "U123";
        return mDatabase.getReference("users/"+ user + "/trips/" + tripId).getRef();
    }

    public DatabaseReference getTrips() {
        return mDatabase.getReference("users/U123/trips").getRef();
    }

}
