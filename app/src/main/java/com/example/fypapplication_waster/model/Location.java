package com.example.fypapplication_waster.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Location {

    @SerializedName("coordinates")
    private ArrayList coordinates;
    @SerializedName("type")
    private String type;

    public Location(ArrayList coordinates, String type) {
        this.coordinates = coordinates;
        this.type = type;
    }

    public ArrayList getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getLatitude() {
        return (Double) coordinates.get(1);
    }

    public Double getLongitude() {
        return (Double) coordinates.get(0);
    }
}
