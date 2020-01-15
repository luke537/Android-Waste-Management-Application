package com.example.fypapplication_waster.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BinToBeReceived {

    @SerializedName("_id")
    private MongoDbId binId;
    @SerializedName("name")
    private String name;
    @SerializedName("location")
    private ArrayList location;
    @SerializedName("photo")
    private String photo;
    @SerializedName("materials")
    private ArrayList materials;
    @SerializedName("owner")
    private String owner;
    @SerializedName("comments")
    private ArrayList comments;
    @SerializedName("price")
    private Double price;
    @SerializedName("hours")
    private String hours;

    public BinToBeReceived(MongoDbId binId, String name, ArrayList location, String photo,
                           ArrayList materials, String owner, ArrayList comments,
                           Double price, String hours) {
        this.binId = binId;
        this.name = name;
        this.location = location;
        this.photo = photo;
        this.materials = materials;
        this.owner = owner;
        this.comments = comments;
        this.price = price;
        this.hours = hours;
    }

    public MongoDbId getBinId() {
        return binId;
    }

    public void setBinId(MongoDbId binId) {
        this.binId = binId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList getLocation() {
        return location;
    }

    public void setLocation(ArrayList location) {
        this.location = location;
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

    public ArrayList getComments() {
        return comments;
    }

    public void setComments(ArrayList comments) {
        this.comments = comments;
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

}
