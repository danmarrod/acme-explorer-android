package com.tecmov.acmeexplorer.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.tecmov.acmeexplorer.Constants;
import com.tecmov.acmeexplorer.FirestoreService;
import com.tecmov.acmeexplorer.R;
import com.tecmov.acmeexplorer.TripActivity;
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
        holder.textViewPrice.setText(trip.getPrice().toString() + "€");
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

        holder.switchLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isLiked) {
                trip.setLike(isLiked);
                for (Trip t : Constants.chargedTrips) {
                    if (t.getTicker().contentEquals(trip.getTicker()))
                        t.setLike(isLiked);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null){
            mErrorListener.onItemError(e);
        }

        tripList.clear();
        if(queryDocumentSnapshots != null)
            tripList.addAll(queryDocumentSnapshots.toObjects(Trip.class));

        notifyDataSetChanged();
        mDataChangedListener.onDataChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewDescription, textViewPrice, textViewStartedDate, textViewFinishedDate;
        ImageView imageView;
        Switch switchLike;
        LinearLayout linearLayoutTrip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewStartedDate = itemView.findViewById(R.id.textViewStartedDate);
            textViewFinishedDate = itemView.findViewById(R.id.textViewFinishedDate);
            imageView = itemView.findViewById(R.id.imageViewTrip);
            switchLike = itemView.findViewById(R.id.switchLike);
            linearLayoutTrip = itemView.findViewById(R.id.linearLayoutTrip);
        }
    }

    public void setErrorListener (ItemErrorListener itemErrorListener){
        mErrorListener = itemErrorListener;
    }


    public interface ItemErrorListener {
        void onItemError (FirebaseFirestoreException error);
    }

    public void setDataChangedListener (DataChangedListener dataChangedListener){
        mDataChangedListener = dataChangedListener;
    }

    public interface DataChangedListener {
        void onDataChanged();
    }
}
