package com.example.fypapplication_waster;

public class WasteItemRecyclerViewItem {

    private String wasteItemName;
    private int wasteItemImageId;

    public WasteItemRecyclerViewItem(String wasteItemName, int wasteItemImageId) {
        this.wasteItemName = wasteItemName;
        this.wasteItemImageId = wasteItemImageId;
    }

    public String getWasteItemName() {
        return wasteItemName;
    }

    public void setWasteItemName(String wasteItemName) {
        this.wasteItemName = wasteItemName;
    }

    public int getWasteItemImageId() {
        return wasteItemImageId;
    }

    public void setWasteItemImageId(int wasteItemImageId) {
        this.wasteItemImageId = wasteItemImageId;
    }
}