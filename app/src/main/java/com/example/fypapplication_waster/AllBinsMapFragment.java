package com.example.fypapplication_waster;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fypapplication_waster.retrofit.model.Accessibility;
import com.example.fypapplication_waster.retrofit.model.BinToBeReceived;
import com.example.fypapplication_waster.retrofit.GetDataService;
import com.example.fypapplication_waster.util.FirebaseUtils;
import com.example.fypapplication_waster.util.RetrofitUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.List;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This fragment displays a map showing all bins within a specifc radius of the user's current location
 * 1. Upon clicking on this fragment, we ask the user for location permission
 * 2. On Accept permission, we call the Retrofit find all bins query
 * 3. Populate map with bin markers
 */
//TODO Remove new map marker after bin has been added or canceled

public class AllBinsMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, OnLocationUpdatedListener, GoogleMap.OnMarkerDragListener {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private GetDataService service;
    private FirebaseStorage storage;
    private List<BinToBeReceived> matchedBins;
    private Double latitude, longitude;

    private Double newBinLatitude, newBinLongitude;

    byte[] retrievedBinImageStream;

    private final static String TAG = "AllBinsMapFragment";
    public final static int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    private AlertDialog dialog;
    private ExtendedFloatingActionButton btnAddBin;
    private ExtendedFloatingActionButton btnConfirmBinLocation;

    ImageView binImgView;

    private Uri firebaseBinImgDownloadUri;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        service = RetrofitUtils.getRetrofitClientInstance();
        storage = FirebaseUtils.getFirebaseStorageInstance();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        startLocation();

        if (latitude == null || longitude == null) {
            getLastLocation();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.map_fragment_layout, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map_fragment);
        setUpMapIfNeeded();

        btnAddBin = rootView.findViewById(R.id.btnAddBinOnMap);
        btnAddBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAskLocationModal();
            }
        });

        btnConfirmBinLocation = rootView.findViewById(R.id.btnConfirmBinLocation);
        btnConfirmBinLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAddBinModal();
            }
        });

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void onMapReady(GoogleMap googleMap) {
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

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }


    private void zoomInOnMarker(LatLng location) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,17));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
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
            }
        }
    }


    public List<BinToBeReceived> getBinsInRadiusRestCall(Double latitude, Double longitude) {
        Call<List<BinToBeReceived>> call = service.getBinsAtLocation(latitude, longitude);

        call.enqueue(new Callback<List<BinToBeReceived>>() {
            @Override
            public void onResponse(Call<List<BinToBeReceived>> call, Response<List<BinToBeReceived>> response) {
                if (response.isSuccessful()) {
                    // time when the request was made to server, which you get from ```sentRequestAtMillis```
                    long requestTime = response.raw().sentRequestAtMillis();

                    // time when the response was received, which you get from ```receivedResponseAtMillis```
                    long responseTime = response.raw().receivedResponseAtMillis();

                    //time taken to receive the response after the request was sent
                    long apiTime =  responseTime - requestTime;
                    Log.i(TAG, String.format("Time taken to respond with %d waste containers: %d ms", response.body().size(), apiTime));

                    Log.d(TAG, "Successful response getting bins");
                    matchedBins = response.body();

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
                Marker marker = mMap.addMarker(new MarkerOptions().draggable(false).position(binLatLng).title(((BinToBeReceived) bin).getName()));
                marker.setTag((BinToBeReceived) bin);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(binLatLng));
            }

            BinToBeReceived closestBin = (BinToBeReceived) matchedBins.get(0); //bins are ordered with nearest first
            LatLng closestBinLatLng = new LatLng(((BinToBeReceived) closestBin).getLocation().getLatitude(), ((BinToBeReceived) closestBin).getLocation().getLongitude());
            zoomInOnMarker(closestBinLatLng);

        }

        else {
            Log.d(TAG, "No bins found");
            Toast.makeText(getActivity(), "Unfortunately, there were no bins found in your area", Toast.LENGTH_LONG).show();
        }
    }

    private void drawMarkerOnCurrentLocation() {
        LatLng latLng = new LatLng(latitude, longitude);
        Marker marker = mMap.addMarker(new MarkerOptions().draggable(true).position(latLng).title(getResources().getString(R.string.drag_marker_to_change_location)));
        marker.showInfoWindow();
        zoomInOnMarker(latLng);

        newBinLatitude = marker.getPosition().latitude;
        newBinLongitude = marker.getPosition().longitude;
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
            final TextView binAccessibility = (TextView) mView.findViewById(R.id.txtBinAccessibility);
            final TextView mBinComments = (TextView) mView.findViewById(R.id.txtBinComments);
            binImgView = mView.findViewById(R.id.imgBinPic);

            StorageReference binImgRef = storage.getReferenceFromUrl(bin.getPhoto());
            downloadEncodedBitmapFromFirebase(binImgRef);

            mBinName.setText(bin.getName());

            if (bin.getMaterials() != null) {
                for (Object material : bin.getMaterials()) {
                    mBinMaterials.append((String) material + "\n");
                }
            }

            final Accessibility accessibility = bin.getAccessibility();
            if (accessibility.isInside()) {
                binAccessibility.append("Inside\n" + "Building Name: " + accessibility.getBuildingName() + "\n" + "Building Floor: " + accessibility.getBuildingFloor());
            }
            else {
                binAccessibility.append("Outside");
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
        marker.hideInfoWindow();

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        marker.hideInfoWindow();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        marker.showInfoWindow();
        // Set the lat/long of the new bin to be added to the marker position
        newBinLatitude = marker.getPosition().latitude;
        newBinLongitude = marker.getPosition().longitude;
    }

    private void createAskLocationModal() {
        new AlertDialog.Builder(getContext())
                .setTitle("Choose Bin Location")
                .setMessage("Use your current location or choose location?")
                .setPositiveButton("Use my current location", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        getLastLocation();
                        newBinLatitude = latitude;
                        newBinLongitude = longitude;
                        createAddBinModal();
                    }
                })
                .setNegativeButton("Choose location", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        btnAddBin.setVisibility(View.GONE);
                        btnConfirmBinLocation.setVisibility(View.VISIBLE);
                        drawMarkerOnCurrentLocation();
                    }
                })
                .setIcon(R.drawable.ic_location_on_white_24dp)
                .show();
    }

    private void createAddBinModal() {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        Fragment prevDialog = getFragmentManager().findFragmentByTag("dialog");
        if (prevDialog != null) {
            fragmentTransaction.remove(prevDialog);
        }
        fragmentTransaction.addToBackStack(null);

        AddBinDialogFragment addBinDialogFragment = new AddBinDialogFragment();

        Bundle argsBundle = new Bundle(2);
        argsBundle.putDouble("latitude", newBinLatitude);
        argsBundle.putDouble("longitude", newBinLongitude);
        addBinDialogFragment.setArguments(argsBundle);

        addBinDialogFragment.show(fragmentTransaction, "dialog");
    }

    public void downloadEncodedBitmapFromFirebase(StorageReference binImageRef) {

        final long EIGHT_MEGABYTES = 20*(1024 * 1024);
        binImageRef.getBytes(EIGHT_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                retrievedBinImageStream = bytes;
                Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                binImgView.setImageBitmap(decodedByte);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e(TAG, "Could not retrieve bin image from Firebase");
            }
        });
    }
}