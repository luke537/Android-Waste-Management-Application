package com.example.fypapplication_waster;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fypapplication_waster.model.BinToBeReceived;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Double latitude;
    private Double longitude;
    private ArrayList bins;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MapsActivity", "Top of onCreate()");
        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
        bins = (ArrayList<BinToBeReceived>) getIntent().getSerializableExtra("bins");
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (Object bin : bins) {
            LatLng binLatLng = new LatLng(((BinToBeReceived) bin).getLocation().getLatitude(), ((BinToBeReceived) bin).getLocation().getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(binLatLng).title(((BinToBeReceived) bin).getName()));
            marker.setTag((BinToBeReceived) bin);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(binLatLng));
        }

        BinToBeReceived closestBin = (BinToBeReceived) bins.get(0); //bins are ordered with nearest first
        LatLng closestBinLatLng = new LatLng(((BinToBeReceived) closestBin).getLocation().getLatitude(), ((BinToBeReceived) closestBin).getLocation().getLongitude());
        zoomInOnMarker(closestBinLatLng);

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        BinToBeReceived bin = (BinToBeReceived) marker.getTag();

        if (bin != null) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.bin_info_modal, null);
            final TextView mBinName = (TextView) mView.findViewById(R.id.txtBinName);
            final TextView mBinMaterials = (TextView) mView.findViewById(R.id.txtBinMaterials);
            final TextView mBinComments = (TextView) mView.findViewById(R.id.txtBinComments);

            mBinName.setText(bin.getName());

            for (Object material : bin.getMaterials()) {
                mBinMaterials.append((String) material + "\n");
            }

            for (Object comment : bin.getComments()) {
                mBinComments.append((String) comment + "\n");
            }

//            mBuilder.setPositiveButton(R.string.btnDirectionsTxt, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // Do something for getting directions to the bin
//                }
//            });
//
//            mBuilder.setNegativeButton(R.string.btnCancelTxt, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });


            mBuilder.setView(mView);
            dialog = mBuilder.create();

            Button btnCancel = mView.findViewById(R.id.btnCancelBinInfoModal);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    dialog.dismiss();
                }
            });

            Button btnDirections = mView.findViewById(R.id.btnDirections);
            btnDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Get directions to bin
                }
            });

            dialog.show();
        }

        // Check if a click count was set, then display the click count.
//        if (bin != null) {
//            new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
//                    .setTitleText(bin.getName())
//                    .setContentText((String) bin.getComments().get(0))
//                    .setCancelText("Back")
//                    .setConfirmText("Directions")
//                    .showCancelButton(true)
//                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//                            sDialog.cancel();
//                        }
//                    })
//                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sweetAlertDialog) {
//                            //Do something to get directions to the bin
//                        }
//                    })
//                    .show();
//        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    private void zoomInOnMarker(LatLng location)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }
}
