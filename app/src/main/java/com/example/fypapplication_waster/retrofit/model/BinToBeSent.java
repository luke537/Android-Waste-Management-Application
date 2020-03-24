package com.example.fypapplication_waster.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BinToBeSent {

    @SerializedName("name")
    private String name;
    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("latitude")
    private Double latitude;
    @SerializedName("photo")
    private String photo;
    @SerializedName("materials")
    private ArrayList materials;
    @SerializedName("owner")
    private String owner;
    @SerializedName("price")
    private Double price;
    @SerializedName("hours")
    private String hours;
    @SerializedName("isInside")
    private boolean isInside;
    @SerializedName("buildingName")
    private String buildingName;
    @SerializedName("buildingFloor")
    private String buildingFloor;

    public BinToBeSent(String name, Double latitude, Double longitude, String photo,
                           ArrayList materials, String owner,
                           Double price, String hours, boolean isInside, String buildingName, String buildingFloor) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo = photo;
        this.materials = materials;
        this.owner = owner;
        this.price = price;
        this.hours = hours;
        this.isInside = isInside;
        this.buildingName = buildingName;
        this.buildingFloor = buildingFloor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ArrayList getMaterials() {
        return materials;
    }

    public void setMaterials(ArrayList materials) {
        this.materials = materials;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public boolean isInside() {
        return isInside;
    }

    public void setInside(boolean inside) {
        isInside = inside;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingFloor() {
        return buildingFloor;
    }

    public void setBuildingFloor(String buildingFloor) {
        this.buildingFloor = buildingFloor;
    }
}
