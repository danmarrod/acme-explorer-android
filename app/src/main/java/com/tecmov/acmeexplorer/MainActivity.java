package com.tecmov.acmeexplorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tecmov.acmeexplorer.entity.Link;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    Button main_create_trip_button, main_create_profile_button, main_location_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView=findViewById(R.id.gridView);
        gridView.setAdapter(new LinkAdapter(Link.generateLink(),this));
        main_create_trip_button = findViewById(R.id.main_create_trip_button);
        main_create_profile_button = findViewById(R.id.main_create_profile_button);
        main_location_button = findViewById(R.id.main_location_button);

        main_create_trip_button.setOnClickListener(l -> startActivity(new Intent(this, TripCreateActivity.class)));
        main_create_profile_button.setOnClickListener(l -> startActivity(new Intent(this, ProfileActivity.class)));
        main_location_button.setOnClickListener(l -> startActivity(new Intent(this, LocationActivity.class)));
    }

    public void ListView(View view) {
        startActivity(new Intent(this, FilterActivity.class));
    }
}

class LinkAdapter extends BaseAdapter {

    List<Link> links;
    Context context;

    public LinkAdapter(List<Link> links, Context context) {
        this.links = links;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.links.size();
    }

    @Override
    public Object getItem(int i) {
        return this.links.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.links.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Link link = links.get(i);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.link_item, viewGroup, false);
        }
        CardView cardView = view.findViewById(R.id.cardView);
        TextView textView = view.findViewById(R.id.textView);
        ImageView imageView = view.findViewById(R.id.imageView);
        textView.setText(link.getDescription());
        imageView.setImageResource(link.getImageView());
        cardView.setOnClickListener(view1 -> context.startActivity(new Intent(context, link.getClassName())));

        return view;
    }
}