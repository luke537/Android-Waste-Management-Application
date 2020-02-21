package com.example.fypapplication_waster.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MongoDbId implements Serializable {

    @SerializedName("$oid")
    private String $oid;

    public MongoDbId(String $oid) {
        this.$oid = $oid;
    }

    public String get$oid() {
        return $oid;
    }

    public void set$oid(String $oid) {
        this.$oid = $oid;
    }
}

