package com.tecmov.acmeexplorer.adapters;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tecmov.acmeexplorer.Constants;
import com.tecmov.acmeexplorer.R;
import com.tecmov.acmeexplorer.TripActivity;
import com.tecmov.acmeexplorer.entity.Trip;
import com.tecmov.acmeexplorer.utils.Util;

import java.util.List;


public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private List<Trip> tripsList;

    public TripAdapter(List<Trip> tripsList) {
        this.tripsList = tripsList;
    }

    @NonNull
    @Override
    public TripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Here inflate layout from parent context
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View tripView = inflater.inflate(R.layout.trip_item, parent, false);

        // Return a new holder instance
        TripAdapter.ViewHolder viewHolder = new TripAdapter.ViewHolder(tripView);
        return viewHolder;
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        Trip trip = tripsList.get(position);

        // Set item views based on your views and data model
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
        return tripsList.size();
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
}
