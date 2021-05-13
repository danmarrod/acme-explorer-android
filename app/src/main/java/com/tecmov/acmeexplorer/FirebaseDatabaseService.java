package com.tecmov.acmeexplorer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseService {
    private static String userId;
    private static FirebaseDatabaseService service;
    private static FirebaseDatabase mDatabase;

    // Patrón factoría, nos devuelve una instancia el servicio si está creado
    public static FirebaseDatabaseService getServiceInstance() {
        if (service == null || mDatabase == null) {
            service = new FirebaseDatabaseService();
            mDatabase = FirebaseDatabase.getInstance();
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
        String userId = "U123";
        return mDatabase.getReference("users/"+userId+"trips/" + tripId).getRef();
    }

}
