package com.tecmov.acmeexplorer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.tecmov.acmeexplorer.entity.Trip;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0x152;
    private FirebaseAuth mAuth;
    private Button signinButtonGoogle;
    private Button signinButtonMail;
    private Button loginButtonSignup;
    private ProgressBar progressBar;
    private TextInputLayout loginEmailParent;
    private TextInputLayout loginPassParent;
    private AutoCompleteTextView loginEmail;
    private AutoCompleteTextView loginPass;
    private ValueEventListener valueEventListener;
    private FirebaseDatabaseService firebaseDatabaseService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.login_progress);
        loginEmail = findViewById(R.id.login_email_et);
        loginPass = findViewById(R.id.login_pass_et);
        loginEmailParent = findViewById(R.id.login_email);
        loginPassParent = findViewById(R.id.login_pass);
        signinButtonGoogle = findViewById(R.id.login_button_google);
        signinButtonMail = findViewById(R.id.login_button_mail);
        loginButtonSignup = findViewById(R.id.login_button_register);

        GoogleSignInOptions googleSingInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_client_id))
                .requestEmail()
                .build();

        signinButtonGoogle.setOnClickListener(l -> attemptLoginGoogle(googleSingInOptions));

        signinButtonMail.setOnClickListener(l -> attemptLoginEmail());

        loginButtonSignup.setOnClickListener(l -> redirectSignupActivity());

        progressBar.setVisibility(View.GONE);

        //startActivity(new Intent(this, LocationActivity.class));

    }

    private void redirectSignupActivity() {

        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra(SignupActivity.EMAIL_PARAM, loginEmail.getText().toString());
        startActivity(intent);

    }

    private void attemptLoginGoogle(GoogleSignInOptions googleSignInOptions) {
        GoogleSignInClient googleSignIn = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInIntent = googleSignIn.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = result.getResult(ApiException.class);
                assert account != null;
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                if (mAuth == null)
                    mAuth = FirebaseAuth.getInstance();
                if (mAuth != null) {
                    mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            checkUserDatabaseLogin(user);
                        } else {
                            showErrorDialogMail();
                        }
                    });
                } else {
                    showGooglePlayServicesError();
                }
            } catch (ApiException e) {
                showErrorDialogMail();
            }
        }
    }

    // Access to Firebase Authentication to check user and pass
    private void attemptLoginEmail() {
        loginEmailParent.setError(null);
        loginPassParent.setError(null);
        progressBar.setVisibility(View.VISIBLE);

        if (loginEmail.getText().length() == 0) {
            loginEmailParent.setErrorEnabled(true);
            loginEmailParent.setError(getString(R.string.login_mail_error_1));
            progressBar.setVisibility(View.GONE);
        } else if (loginPass.getText().length() == 0) {
            loginPassParent.setErrorEnabled(true);
            loginPassParent.setError(getString(R.string.login_mail_error_2));
            progressBar.setVisibility(View.GONE);
        } else {
            signInEmail();
        }

    }

    private void signInEmail() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        if (mAuth != null) {
            mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPass.getText().toString()).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful() || task.getResult().getUser() == null) {
                    showErrorDialogMail();
                } else if (!task.getResult().getUser().isEmailVerified()) {
                    showErrorEmailVerified(task.getResult().getUser());
                } else {
                    FirebaseUser user = task.getResult().getUser();
                    progressBar.setVisibility(View.GONE);
                    checkUserDatabaseLogin(user);
                }
            });

        } else {
            showGooglePlayServicesError();
        }

    }

    private void showGooglePlayServicesError() {
        Snackbar.make(loginButtonSignup, getString(R.string.login_mail_access_error), Snackbar.LENGTH_SHORT).setAction(R.string.login_download_gps, view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.gps_download_url))));
            } catch (Exception ex) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.market_download_url))));
            }
        }).show();
    }


    private void showErrorDialogMail() {
        hideLoginButton(false);
        Snackbar.make(signinButtonMail, getString(R.string.login_mail_access_error), Snackbar.LENGTH_LONG).show();
    }

    private void showErrorEmailVerified(FirebaseUser user) {
        hideLoginButton(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.login_verified_mail_error)
                .setPositiveButton(R.string.login_verified_mail_error_ok, ((dialog, which) -> user.sendEmailVerification().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Snackbar.make(loginEmail, getString(R.string.login_verified_mail_error_sent), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(loginEmail, getString(R.string.login_verified_mail_error_no_sent), Snackbar.LENGTH_SHORT).show();
                    }
                }))).setNegativeButton(R.string.login_verified_mail_error_cancel, (dialog, which) -> {
        }).show();
    }

    private void hideLoginButton(boolean hide) {

        TransitionSet transitionSet = new TransitionSet();
        Transition layoutFade = new AutoTransition();
        layoutFade.setDuration(1000);
        transitionSet.addTransition(layoutFade);

        if (hide) {
            TransitionManager.beginDelayedTransition(findViewById(R.id.login_main_layout), transitionSet);
            progressBar.setVisibility(View.VISIBLE);
            signinButtonMail.setVisibility(View.GONE);
            signinButtonGoogle.setVisibility(View.GONE);
            loginButtonSignup.setVisibility(View.GONE);
            loginEmailParent.setEnabled(false);
            loginPassParent.setEnabled(false);
        } else {
            TransitionManager.beginDelayedTransition(findViewById(R.id.login_main_layout), transitionSet);
            progressBar.setVisibility(View.GONE);
            signinButtonMail.setVisibility(View.VISIBLE);
            signinButtonGoogle.setVisibility(View.VISIBLE);
            loginButtonSignup.setVisibility(View.VISIBLE);
            loginEmailParent.setEnabled(true);
            loginPassParent.setEnabled(true);
        }
    }

    private void checkUserDatabaseLogin(FirebaseUser user) {

        Toast.makeText(this, String.format(getString(R.string.login_completed), user.getEmail()), Toast.LENGTH_SHORT).show();
        //testDatabases();

        startActivity(new Intent(this, MainActivity.class));
        //startActivity(new Intent(this, FirebaseStorageActivity.class));
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseDatabaseService != null && valueEventListener != null)
            firebaseDatabaseService.getTrips("2").removeEventListener(valueEventListener);
    }

    private void testDatabases() {

        firebaseDatabaseService = FirebaseDatabaseService.getServiceInstance();
        firebaseDatabaseService.saveTrips(new Trip("ABCA045", "Madrid", "Museo del Prado", 32.00, new GregorianCalendar(2021, 6, 15).getTime(), new GregorianCalendar(2021, 6, 30).getTime(), "https://iconape.com/wp-content/png_logo_vector/beach-tour-logo.png", false), new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if (databaseError == null)
                    Log.i("Acme-Explorer", "Trips insertado");
                else
                    Log.i("Acme-Exporer", "Error al insertar nuevo trip");
            }
        });

        // Get single value by asynchronous call, subscribe at the event
        firebaseDatabaseService.getTrips("1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    // Clase a la que se convertirá el valor devuelto, debe ser del mismo tipo. Igual en la base de datos
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    Toast.makeText(LoginActivity.this, trip.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Monitoring any updates in property 'title' for trip with id=1
        firebaseDatabaseService.getTripsTitle("2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    String title = dataSnapshot.getValue(String.class);
                    Log.i("Acme-Explorer", "Valor title modificado: " + title.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // to check only is called once
        if (valueEventListener == null) {

            // Monitor object with id 2, and info any updates in the object and its properties
            valueEventListener = firebaseDatabaseService.getTrips("2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                        Trip trip = dataSnapshot.getValue(Trip.class);
                        Log.i("Acme-Explorer", "Elemento modificado individualmente: " + trip.toString());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        // Monitoring any updates in every objects inside of the collection
        ChildEventListener childEventListener = firebaseDatabaseService.getTrips().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    Log.i("Acme-Explorer", "Elemento añadido: " + trip.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    Log.i("Acme-Explorer", "Elemento modificado: " + trip.toString());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    Log.i("Acme-Explorer", "Elemento eliminado: " + trip.toString());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    Log.i("Acme-Explorer", "Elemento movido: " + trip.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirestoreService firestoreService = FirestoreService.getServiceInstance();
        //
        firestoreService.saveTrip(new Trip("ABCA045", "Madrid", "Museo del Prado", 32.5, new GregorianCalendar(2021, 6, 15).getTime(), new GregorianCalendar(2021, 6, 30).getTime(), "https://iconape.com/wp-content/png_logo_vector/beach-tour-logo.png", false), new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    DocumentReference documentReference = task.getResult();
                    // listener for reading, before this one, it's for writing
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                // documentSnapshot get properties values of the object
                                Trip trip = documentSnapshot.toObject(Trip.class);
                                Log.i("Acme-Explorer", "Firestore almacenamiento feedback: " + trip.toString());
                            }

                        }
                    });
                    Log.i("Acme-Explorer", "Firestore almacenamiento completado " + task.getResult().getId());
                } else
                    Log.i("Acme-Explorer", "Firestore almacenamiento ha fallado");

            }
        });

        firestoreService.getTripsFiltered(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Trip> trips = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Trip trip = documentSnapshot.toObject(Trip.class);
                        trips.add(trip);
                    }
                    Log.i("Acme-Explorer", "Firestore lectura " + trips.toString());
                }
            }
        });

        firestoreService.getTrip("44GGE6Vmjj60nqgQOvWm", new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()){
                    Trip trip = documentSnapshot.toObject(Trip.class);
                    Log.i("Acme-Explorer", "Firestore lectura individual: " + trip.toString());
                    loginEmail.setText(trip.toString());
                }
            }
        });
    }

}