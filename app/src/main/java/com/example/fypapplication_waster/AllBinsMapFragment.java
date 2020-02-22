package com.example.fypapplication_waster;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.fypapplication_waster.retrofit.model.BinToBeReceived;
import com.example.fypapplication_waster.retrofit.GetDataService;
import com.example.fypapplication_waster.retrofit.model.BinToBeSent;
import com.example.fypapplication_waster.util.RetrofitUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This fragment displays a map showing all bins within a specifc radius of the user's current location
 * 1. Upon clicking on this fragment, we ask the user for location permission
 * 2. On Accept permission, we call the Retrofit find all bins query
 * 3. Populate map with bin markers
 */

public class AllBinsMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, OnLocationUpdatedListener, GoogleMap.OnMarkerDragListener {

    SupportMapFragment mapFragment;
    GoogleMap mMap;

    GetDataService service;
    List<BinToBeReceived> matchedBins;
    Double latitude, longitude;

    Double newBinLatitude, newBinLongitude;

    private final static String TAG = "AllBinsMapFragment";
    public final static int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private AlertDialog dialog;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        service = RetrofitUtils.getRetrofitClientInstance();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        startLocation();

        if (latitude == null || longitude == null) {
            getLastLocation();
        }

        Log.d(TAG, "onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.map_fragment_layout, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map_fragment);
        setUpMapIfNeeded();

        Button btnAddBin = rootView.findViewById(R.id.btnAddBinOnMap);
        btnAddBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Call a method that drops a marker down for the user to edit position
                createAddBinModal();
            }
        });

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        setUpMapIfNeeded();
    }

    private void startLocation() {
        SmartLocation.with(getContext()).location()
                .oneFix().start(this);
    }

    private void getLastLocation() {
        Location lastLocation = SmartLocation.with(getContext()).location().getLastLocation();
        latitude = lastLocation.getLatitude();
        longitude = lastLocation.getLongitude();
    }

    @Override
    public void onLocationUpdated(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mapFragment.getMapAsync(this);
            getBinsInRadiusRestCall(latitude, longitude);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mMap.setMyLocationEnabled(true);
            }
        }
    }


    private void zoomInOnMarker(LatLng location) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocation();

                } else {
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }


    public List<BinToBeReceived> getBinsInRadiusRestCall(Double latitude, Double longitude) {
        Call<List<BinToBeReceived>> call = service.getBinsAtLocation(latitude, longitude);

        call.enqueue(new Callback<List<BinToBeReceived>>() {
            @Override
            public void onResponse(Call<List<BinToBeReceived>> call, Response<List<BinToBeReceived>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Successful response getting bins");
                    matchedBins = response.body();

                    // TODO put the stuff I want to happen after GET request here
                    if (matchedBins != null && matchedBins.size() > 0 && mMap != null) {
                        drawBinsOnMap();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BinToBeReceived>> call, Throwable t) {
                Log.d(TAG, "No server response: Failure getting bins");
                t.printStackTrace();
                matchedBins = null;
            }
        });

        return matchedBins;
    }

    public void drawBinsOnMap() {
        if (matchedBins != null) {
            Log.d(TAG, String.format("Found %d bins", matchedBins.size()));

            for (Object bin : matchedBins) {
                LatLng binLatLng = new LatLng(((BinToBeReceived) bin).getLocation().getLatitude(), ((BinToBeReceived) bin).getLocation().getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions().draggable(true).position(binLatLng).title(((BinToBeReceived) bin).getName()));
                marker.setTag((BinToBeReceived) bin);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(binLatLng));
            }

            BinToBeReceived closestBin = (BinToBeReceived) matchedBins.get(0); //bins are ordered with nearest first
            LatLng closestBinLatLng = new LatLng(((BinToBeReceived) closestBin).getLocation().getLatitude(), ((BinToBeReceived) closestBin).getLocation().getLongitude());
            zoomInOnMarker(closestBinLatLng);

            // Set a listener for marker click.
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMarkerDragListener(this);
        }

        else {
            Log.d(TAG, "No bins found");
            Toast.makeText(getActivity(), "Unfortunately, there were no bins found in your area", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        BinToBeReceived bin = (BinToBeReceived) marker.getTag();

        if (bin != null) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
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

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // Set the lat/long of the new bin to be added to the marker position
        newBinLatitude = marker.getPosition().latitude;
        newBinLongitude = marker.getPosition().longitude;

        // TODO Bring up modal for adding bin after marker adjusted
    }

    private void createAddBinModal() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.add_bin_modal, null);

        final TextView binName = mView.findViewById(R.id.txtBinName);
        final CheckBox cbxGlass = mView.findViewById(R.id.cbxGlass);
        final CheckBox cbxPlastic = mView.findViewById(R.id.cbxPlastic);
        final CheckBox cbxMetal = mView.findViewById(R.id.cbxMetal);

        // TODO set bin lat and long to be marker lat and long
        newBinLatitude = latitude;
        newBinLongitude = longitude;

        Spinner spinner = mView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.floor_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        LinearLayout binAccessLayout = mView.findViewById(R.id.linearLayoutBinAccess);

        EditText txtBuildingName = mView.findViewById(R.id.txtBuildingName);

        RadioGroup radioGroup = (RadioGroup) mView.findViewById(R.id.accessibilityRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == R.id.accessRadioBtnInside) {
                    binAccessLayout.setVisibility(View.VISIBLE);
                }
                else {
                    if (binAccessLayout.getVisibility() != View.GONE) {
                        binAccessLayout.setVisibility(View.GONE);
                    }
                }
            }
        });


        final EditText binPrice = mView.findViewById(R.id.txtPrice);

        mBuilder.setView(mView);
        dialog = mBuilder.create();

        Button btnCancel = mView.findViewById(R.id.btnCancelAddBin);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dialog.dismiss();
            }
        });

        Button btnSubmit = mView.findViewById(R.id.btnSubmitBin);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Validate fields
                if (!TextUtils.isEmpty(binName.getText()) && !TextUtils.isEmpty(binPrice.getText())) {
                    ArrayList materials = new ArrayList<>();

                    if (cbxGlass.isChecked()) { materials.add("glass"); }
                    if (cbxMetal.isChecked()) { materials.add("metal"); }
                    if (cbxPlastic.isChecked()) { materials.add("plastic"); }

                    boolean isInside;
                    String buildingFloor;
                    String buildingName;

                    //check radio buttons
                    if (radioGroup.getCheckedRadioButtonId() == R.id.accessRadioBtnInside) {
                        isInside = true;
                        buildingFloor = spinner.getSelectedItem().toString();
                        buildingName = txtBuildingName.getText().toString();
                    }
                    else {
                        isInside = false;
                        buildingFloor = null;
                        buildingName = null;
                    }

                    // POST bin to server
                    Call<ResponseBody> call = service.addBin(new BinToBeSent(binName.getText().toString(), newBinLatitude, newBinLongitude,
                            null, materials,
                            null, null, Double.valueOf(binPrice.getText().toString()),
                            null, isInside, buildingName, buildingFloor));

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d("MapsActivity -> Add", response.message());
                            dialog.dismiss();
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Bin added successfully!", Toast.LENGTH_SHORT).show();
                                Log.d("AddBinActivity", "Bin Added Successfully");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                            Log.e("AddBinActivity", "Connection Failed\n" + t.getMessage());
                        }
                    });
                }

                else {
                    Toast.makeText(getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

}
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//
//        permissionsToRequest = permissionsToRequest(permissions);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (permissionsToRequest.size() > 0) {
//                requestPermissions(permissionsToRequest.toArray(
//                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
//            }
//        }
//
//        // we build google api client
//        googleApiClient = new GoogleApiClient.Builder(getActivity()).
//                addApi(LocationServices.API).
//                addConnectionCallbacks(this).
//                addOnConnectionFailedListener(this).build();
//
////        askForLocationPermissions();
//
//
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        View rootView = inflater.inflate(R.layout.map_fragment_layout, container, false);
//        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map_fragment); //this
//        mMapFragment.getMapAsync(this); //and this are breaking location
//
//        return rootView;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        if (googleApiClient != null) {
//            googleApiClient.connect();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//            googleApiClient.disconnect();
//        }
//
//        mMapView.onResume();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if (!checkPlayServices()) {
//            Toast.makeText(getActivity(), "You need to install Google Play Services to use the App properly", Toast.LENGTH_LONG);
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mMapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mMapView.onLowMemory();
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.setMyLocationEnabled(true);
//////        List<BinToBeReceived> bins = getBinsInRadiusRestCall(latitude, longitude);
//////
//////        if (bins != null) {
//////            Log.d(TAG, String.format("Found %d bins", bins.size()));
//////
//////            for (Object bin : bins) {
//////                LatLng binLatLng = new LatLng(((BinToBeReceived) bin).getLocation().getLatitude(), ((BinToBeReceived) bin).getLocation().getLongitude());
//////                Marker marker = mMap.addMarker(new MarkerOptions().position(binLatLng).title(((BinToBeReceived) bin).getName()));
//////                marker.setTag((BinToBeReceived) bin);
//////                mMap.moveCamera(CameraUpdateFactory.newLatLng(binLatLng));
//////            }
//////
//////            BinToBeReceived closestBin = (BinToBeReceived) bins.get(0); //bins are ordered with nearest first
//////            LatLng closestBinLatLng = new LatLng(((BinToBeReceived) closestBin).getLocation().getLatitude(), ((BinToBeReceived) closestBin).getLocation().getLongitude());
//////            zoomInOnMarker(closestBinLatLng);
//////
//////            // Set a listener for marker click.
//////            mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
//////        }
//////
//////        else {
//////            Log.d(TAG, "No bins found");
//////            Toast.makeText(getActivity(), "Unfortunately, there were no bins found in your area", Toast.LENGTH_LONG).show();
//////        }
////
//        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Current Location"));
////
////        //TODO: Figure out what to do with add bin modal
////
////        // Set listener for "Add Bin" click
//////        Button btnAddBin = findViewById(R.id.btnAddBinOnMap);
//////        btnAddBin.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View v) {
//////                onAddBinBtnClick();
//////            }
//////        });
//    }
//
//    @Override
//    public boolean onMarkerClick(final Marker marker) {
////
////        // Retrieve the data from the marker.
//////        BinToBeReceived bin = (BinToBeReceived) marker.getTag();
//////
//////        if (bin != null) {
//////            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
//////            View mView = getLayoutInflater().inflate(R.layout.bin_info_modal, null);
//////            final TextView mBinName = (TextView) mView.findViewById(R.id.txtBinName);
//////            final TextView mBinMaterials = (TextView) mView.findViewById(R.id.txtBinMaterials);
//////            final TextView mBinComments = (TextView) mView.findViewById(R.id.txtBinComments);
////////            final ImageView binImg = mView.findViewById(R.id.imgBinPic);
////////
////////            String encodedImage = bin.getPhoto();
////////            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
////////            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
////////
////////            binImg.setImageBitmap(decodedByte);
//////
//////            mBinName.setText(bin.getName());
//////
//////            if (bin.getMaterials() != null) {
//////                for (Object material : bin.getMaterials()) {
//////                    mBinMaterials.append((String) material + "\n");
//////                }
//////            }
//////
//////            if (bin.getComments() != null) {
//////                for (Object comment : bin.getComments()) {
//////                    mBinComments.append((String) comment + "\n");
//////                }
//////            }
//////
//////
//////            mBuilder.setView(mView);
//////            dialog = mBuilder.create();
//////
//////            Button btnCancel = mView.findViewById(R.id.btnCancelBinInfoModal);
//////            btnCancel.setOnClickListener(new View.OnClickListener() {
//////                @Override
//////                public void onClick(View v) {
//////                    dialog.cancel();
//////                    dialog.dismiss();
//////                }
//////            });
//////
//////            Button btnDirections = mView.findViewById(R.id.btnDirections);
//////            btnDirections.setOnClickListener(new View.OnClickListener() {
//////                @Override
//////                public void onClick(View v) {
//////                    //Get directions to bin
//////                }
//////            });
//////
//////            dialog.show();
//////        }
//        return false;
//    }
////
////    private void zoomInOnMarker(LatLng location)
////    {
////        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
////        // Zoom in, animating the camera.
////        mMap.animateCamera(CameraUpdateFactory.zoomIn());
////        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
////        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
////    }
