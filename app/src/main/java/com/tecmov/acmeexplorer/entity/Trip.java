package com.tecmov.acmeexplorer.entity;

import com.tecmov.acmeexplorer.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trip implements Serializable {

    private String ticker, title, description, picture;
    private Double price;
    private Date startedDate, finishedDate;
    private boolean isLike;

    public Trip(String ticker, String title, String description, Double price, Date startedDate, Date finishedDate, String picture, boolean isLike) {
        this.ticker = ticker;
        this.title = title;
        this.description = description;
        this.price = price;
        this.startedDate = startedDate;
        this.finishedDate = finishedDate;
        this.picture = picture;
        this.isLike = isLike;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Date startedDate) {
        this.startedDate = startedDate;
    }

    public Date getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(Date finishedDate) {
        this.finishedDate = finishedDate;
    }

    @Override
    public String toString() {
        return "Ticker='" + ticker + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public static List<Trip> generateTrips(int max) {
        List<Trip> list = new ArrayList<Trip>(max);

        for (int i = 0; i < max; i++) {
            if (Constants.trips[i] != null)
                list.add(Constants.trips[i]);
        }

        return list;
    }

    public static List<Trip> generateMyTrips(int max) {

        List<Trip> list = new ArrayList<Trip>(max);

        for (int i = 0; i < max; i++) {
            if (Constants.trips[i] != null && Constants.trips[i].isLike())
                list.add(Constants.trips[i]);
        }

        return list;
    }

}
