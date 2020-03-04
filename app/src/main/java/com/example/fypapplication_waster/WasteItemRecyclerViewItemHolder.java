package com.example.fypapplication_waster;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class WasteItemRecyclerViewItemHolder extends RecyclerView.ViewHolder {

    private TextView wasteItemTitleText = null;
    private ImageView wasteItemImageView = null;

    public WasteItemRecyclerViewItemHolder(View itemView) {
        super(itemView);

        if(itemView != null) {
            wasteItemTitleText = (TextView) itemView.findViewById(R.id.card_view_image_title);
            wasteItemImageView = (ImageView) itemView.findViewById(R.id.card_view_image);
        }
    }

    public TextView getwasteItemTitleText() {
        return wasteItemTitleText;
    }

    public ImageView getCarImageView() {
        return wasteItemImageView;
    }
}