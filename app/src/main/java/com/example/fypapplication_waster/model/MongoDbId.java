package com.example.fypapplication_waster.model;

import com.google.gson.annotations.SerializedName;

public class MongoDbId {

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
