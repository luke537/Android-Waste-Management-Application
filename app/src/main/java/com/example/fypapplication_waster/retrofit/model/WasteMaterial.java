package com.example.fypapplication_waster.retrofit.model;

import com.google.gson.annotations.SerializedName;

public class WasteMaterial {
    @SerializedName("_id")
    private String id;
    @SerializedName("category_id")
    private String categoryId;
    @SerializedName("waste_class_id")
    private String wasteClassId;

    public WasteMaterial(String id, String categoryId, String wasteClassId) {
        this.id = id;
        this.categoryId = categoryId;
        this.wasteClassId = wasteClassId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getWasteClassId() {
        return wasteClassId;
    }

    public void setWasteClassId(String wasteClassId) {
        this.wasteClassId = wasteClassId;
    }
}
