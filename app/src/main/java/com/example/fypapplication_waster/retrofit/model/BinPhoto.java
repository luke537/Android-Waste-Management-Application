package com.example.fypapplication_waster.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BinPhoto implements Serializable {

    @SerializedName("encodedString")
    private String encodedString;

    public BinPhoto(String encodedString) {
        this.encodedString = encodedString;
    }

    public String getEncodedString() {
        return encodedString;
    }

    public void setEncodedString(String $oid) {
        this.encodedString = encodedString;
    }
}
