package com.example.fypapplication_waster;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fypapplication_waster.retrofit.GetDataService;
import com.example.fypapplication_waster.retrofit.model.BinToBeReceived;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Double latitude;
    private Double longitude;
    private ArrayList bins;
    AlertDialog dialog;
    private GetDataService service;

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

//        if (dialog.isShowing()) {
//            dialog.dismiss();
//        }
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

        // Set listener for "Add Button" click
        Button btnAddBin = findViewById(R.id.btnAddBinOnMap);
        btnAddBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onAddBinBtnClick();
            }
        });
    }

//    private void onAddBinBtnClick() {
//        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
//        View mView = getLayoutInflater().inflate(R.layout.add_bin_modal, null);
//
//        final TextView binName = (TextView) mView.findViewById(R.id.txtBinName);
//        final CheckBox cbxGlass = mView.findViewById(R.id.cbxGlass);
//        final CheckBox cbxPlastic = mView.findViewById(R.id.cbxPlastic);
//        final CheckBox cbxMetal = mView.findViewById(R.id.cbxMetal);
//        final EditText binPrice = mView.findViewById(R.id.txtPrice);
//
//        mBuilder.setView(mView);
//        dialog = mBuilder.create();
//
//        Button btnCancel = mView.findViewById(R.id.btnCancelAddBin);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.cancel();
//                dialog.dismiss();
//            }
//        });
//
//        Button btnSubmit = mView.findViewById(R.id.btnSubmitBin);
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                // Validate fields
//                if (!TextUtils.isEmpty(binName.getText()) && !TextUtils.isEmpty(binPrice.getText())) {
//                    ArrayList materials = new ArrayList<>();
//
//                    if (cbxGlass.isChecked()) { materials.add("glass"); }
//                    if (cbxMetal.isChecked()) { materials.add("metal"); }
//                    if (cbxPlastic.isChecked()) { materials.add("plastic"); }
//
//
//                    // POST bin to server
//                    service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
//                    Call<ResponseBody> call = service.addBin(new BinToBeSent(binName.getText().toString(), latitude, longitude, null, materials, null, null, Double.valueOf(binPrice.getText().toString()), null));
//
//                    call.enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            Log.d("MapsActivity -> Add", response.message());
//                            dialog.dismiss();
//                            if (response.isSuccessful()) {
//                                Toast.makeText(MapsActivity.this, "Bin added successfully!", Toast.LENGTH_SHORT).show();
//                                Log.d("AddBinActivity", "Bin Added Successfully");
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            Toast.makeText(MapsActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
//                            Log.e("AddBinActivity", "Connection Failed\n" + t.getMessage());
//                        }
//                    });
//                }
//
//                else {
//                    Toast.makeText(MapsActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        dialog.show();
//    }

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
//            final ImageView binImg = mView.findViewById(R.id.imgBinPic);
//
//            String encodedImage = bin.getPhoto();
//            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
//            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//
//            binImg.setImageBitmap(decodedByte);

            mBinName.setText(bin.getName());

            if (bin.getMaterials() != null) {
                for (Object material : bin.getMaterials()) {
                    mBinMaterials.append((String) material + "\n");
                }
            }

            if (bin.getComments() != null) {
                for (Object comment : bin.getComments()) {
                    mBinComments.append((String) comment + "\n");
                }
            }


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
