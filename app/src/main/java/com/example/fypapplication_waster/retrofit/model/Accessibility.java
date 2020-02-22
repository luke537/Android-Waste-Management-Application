package com.example.fypapplication_waster.retrofit.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Accessibility implements Serializable {

    @SerializedName("isInside")
    private boolean isInside;
    @SerializedName("buildingName")
    private String buildingName;
    @SerializedName("buildingFloor")
    private String buildingFloor;

    public Accessibility(boolean isInside, String buildingName, String buildingFloor) {
        this.isInside = isInside;
        this.buildingName = buildingName;
        this.buildingFloor = buildingFloor;
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
