package com.example.fypapplication_waster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class WasteItemRecyclerViewDataAdapter extends RecyclerView.Adapter<WasteItemRecyclerViewItemHolder> {

    private List<WasteItemRecyclerViewItem> wasteItemList;

    public WasteItemRecyclerViewDataAdapter(List<WasteItemRecyclerViewItem> wasteItemList) {
        this.wasteItemList = wasteItemList;
    }

    @Override
    public WasteItemRecyclerViewItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get LayoutInflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the RecyclerView item layout xml.
        View carItemView = layoutInflater.inflate(R.layout.waste_item_cardview_item, parent, false);

        // Get car title text view object.
        final TextView carTitleView = (TextView)carItemView.findViewById(R.id.card_view_image_title);
        // Get car image view object.
        final ImageView carImageView = (ImageView)carItemView.findViewById(R.id.card_view_image);
        // When click the image.
        carImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get car title text.
                String carTitle = carTitleView.getText().toString();
                // Create a snackbar and show it.
                Snackbar snackbar = Snackbar.make(carImageView, "You click " + carTitle +" image", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        // Create and return our custom Car Recycler View Item Holder object.
        WasteItemRecyclerViewItemHolder ret = new WasteItemRecyclerViewItemHolder(carItemView);

        return ret;
    }

    @Override
    public void onBindViewHolder(WasteItemRecyclerViewItemHolder holder, int position) {
        if(wasteItemList != null) {
            // Get car item dto in list.
            WasteItemRecyclerViewItem carItem = wasteItemList.get(position);

            if(carItem != null) {
                // Set car item title.
                holder.getwasteItemTitleText().setText(carItem.getWasteItemName());
                // Set car image resource id.
                holder.getCarImageView().setImageResource(carItem.getWasteItemImageId());
            }
        }
    }

    @Override
    public int getItemCount() {
        int ret = 0;
        if (wasteItemList != null) {
            ret = wasteItemList.size();
        }
        return ret;
    }
}