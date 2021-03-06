package com.tecmov.acmeexplorer.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.tecmov.acmeexplorer.Constants;
import com.tecmov.acmeexplorer.FirestoreService;
import com.tecmov.acmeexplorer.LocationActivity;
import com.tecmov.acmeexplorer.MainActivity;
import com.tecmov.acmeexplorer.R;
import com.tecmov.acmeexplorer.TripActivity;
import com.tecmov.acmeexplorer.TripCreateActivity;
import com.tecmov.acmeexplorer.entity.Trip;
import com.tecmov.acmeexplorer.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.ViewHolder> implements EventListener<QuerySnapshot> {

    private final List<Trip> tripList;
    private DataChangedListener mDataChangedListener;
    private ItemErrorListener mErrorListener;
    public final ListenerRegistration listenerRegistration;

    public TripListAdapter() {
        tripList = new ArrayList<>();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        listenerRegistration = FirestoreService.getServiceInstance().getAllTrips(this);
    }

    public TripListAdapter(String userId) {
        tripList = new ArrayList<>();
        listenerRegistration = FirestoreService.getServiceInstance().getTrips(this);
    }

    public TripListAdapter(boolean liked) {
        tripList = new ArrayList<>();
        listenerRegistration = FirestoreService.getServiceInstance().getTripsLike(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        holder.textViewTitle.setText(trip.getTitle());
        holder.textViewDescription.setText(trip.getDescription());
        holder.textViewPrice.setText(trip.getPrice().toString() + "???");
        holder.textViewStartedDate.setText(Util.formatDate(trip.getStartedDate().getTime() / 1000));
        holder.textViewFinishedDate.setText(Util.formatDate(trip.getFinishedDate().getTime() / 1000));
        holder.switchLike.setChecked(trip.isLike());

        Picasso.get()
                .load(trip.getPicture())
                .placeholder(android.R.drawable.ic_dialog_map)
                .error(android.R.drawable.ic_dialog_alert)
                .into(holder.imageView);

        holder.linearLayoutTrip.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), TripActivity.class);
            intent.putExtra("Trip", trip);
            view.getContext().startActivity(intent);
        });

        holder.linearLayoutTrip.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), TripActivity.class);
            intent.putExtra("Trip", trip);
            view.getContext().startActivity(intent);
        });

        holder.switchLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isLiked) {

                FirestoreService firestoreService = FirestoreService.getServiceInstance();

                if (isLiked == true) {
                    trip.setLike(true);
                    List<Trip> tripsLiked = new ArrayList<>();

                    firestoreService.getTripsLiked(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    Trip trip = documentSnapshot.toObject(Trip.class);
                                    tripsLiked.add(trip);
                                }
                                Log.i("Acme-Explorer", "Firestore lectura " + tripsLiked.toString());

                                if (!tripsLiked.contains(trip)) {
                                    firestoreService.saveTripLike(trip, task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentReference documentReference = task1.getResult();
                                            documentReference.get().addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    DocumentSnapshot documentSnapshot = task2.getResult();
                                                    Trip trip = documentSnapshot.toObject(Trip.class);
                                                    Log.i("Acme-Explorer", "Liked trip saved feedback: " + trip.toString());
                                                }
                                            });
                                            Log.i("Acme-Explorer", "Liked trip saved successfully " + task1.getResult().getId());

                                        } else {
                                            Log.i("Acme-Explorer", "Error saving liked trip");
                                        }
                                    });
                                }
                            }


                        }
                    });

                } else {
                    //TODO
                    firestoreService.getTripsLiked(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    Trip ftrip = documentSnapshot.toObject(Trip.class);
                                    if (ftrip.equals(trip)) {
                                        firestoreService.removeTrip(documentSnapshot.getId());
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

        holder.trip_location_button.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), LocationActivity.class);
            intent.putExtra("Trip", trip);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            mErrorListener.onItemError(e);
        }

        tripList.clear();
        if (queryDocumentSnapshots != null) {
            tripList.addAll(queryDocumentSnapshots.toObjects(Trip.class));
        }

        notifyDataSetChanged();
        mDataChangedListener.onDataChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewDescription, textViewPrice, textViewStartedDate, textViewFinishedDate;
        ImageView imageView;
        Switch switchLike;
        LinearLayout linearLayoutTrip;
        Button trip_location_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewStartedDate = itemView.findViewById(R.id.textViewStartedDate);
            textViewFinishedDate = itemView.findViewById(R.id.textViewFinishedDate);
            imageView = itemView.findViewById(R.id.imageViewTrip);
            switchLike = itemView.findViewById(R.id.switchLike);
            trip_location_button = itemView.findViewById(R.id.trip_location_button);
            linearLayoutTrip = itemView.findViewById(R.id.linearLayoutTrip);
        }
    }

    public void setErrorListener(ItemErrorListener itemErrorListener) {
        mErrorListener = itemErrorListener;
    }


    public interface ItemErrorListener {
        void onItemError(FirebaseFirestoreException error);
    }

    public void setDataChangedListener(DataChangedListener dataChangedListener) {
        mDataChangedListener = dataChangedListener;
    }

    public interface DataChangedListener {
        void onDataChanged();
    }
}
