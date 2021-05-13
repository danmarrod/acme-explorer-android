package com.tecmov.acmeexplorer.entity;

import com.tecmov.acmeexplorer.ListMyTripsActivity;
import com.tecmov.acmeexplorer.ListTripsActivity;
import com.tecmov.acmeexplorer.MainActivity;
import com.tecmov.acmeexplorer.R;


import java.util.ArrayList;
import java.util.List;

public class Link {
    private String description;
    private int imageView;
    private Class className;

    public Link(String description, int imageView, Class className) {
        this.description = description;
        this.imageView = imageView;
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public int getImageView() {
        return imageView;
    }

    public Class getClassName() {
        return className;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public void setClassName(Class className) {
        this.className = className;
    }

    public static List<Link> generateLink(){
        List<Link> list = new ArrayList<>();

        list.add(new Link("Trips", R.drawable.travels, ListTripsActivity.class));
        list.add(new Link("My Trips",R.drawable.my_travels, ListMyTripsActivity.class));

        return list;
    }
}
