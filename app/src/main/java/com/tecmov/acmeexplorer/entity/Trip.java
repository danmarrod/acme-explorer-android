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
    private Double latitude;
    private Double longitude;

    public Trip() {
    }

    public Trip(String ticker, String title, String description, Double price, Date startedDate, Date finishedDate, String picture, boolean isLike, Double latitude, Double longitude) {
        this.ticker = ticker;
        this.title = title;
        this.description = description;
        this.price = price;
        this.startedDate = startedDate;
        this.finishedDate = finishedDate;
        this.picture = picture;
        this.isLike = isLike;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trip trip = (Trip) o;

        if (isLike != trip.isLike) return false;
        if (ticker != null ? !ticker.equals(trip.ticker) : trip.ticker != null) return false;
        if (title != null ? !title.equals(trip.title) : trip.title != null) return false;
        if (description != null ? !description.equals(trip.description) : trip.description != null)
            return false;
        if (picture != null ? !picture.equals(trip.picture) : trip.picture != null) return false;
        if (price != null ? !price.equals(trip.price) : trip.price != null) return false;
        if (startedDate != null ? !startedDate.equals(trip.startedDate) : trip.startedDate != null)
            return false;
        return finishedDate != null ? finishedDate.equals(trip.finishedDate) : trip.finishedDate == null;
    }

    @Override
    public int hashCode() {
        int result = ticker != null ? ticker.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (startedDate != null ? startedDate.hashCode() : 0);
        result = 31 * result + (finishedDate != null ? finishedDate.hashCode() : 0);
        result = 31 * result + (isLike ? 1 : 0);
        return result;
    }

}
