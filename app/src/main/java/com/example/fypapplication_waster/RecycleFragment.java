package com.example.fypapplication_waster;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.fypapplication_waster.old.WasteItemRecyclerViewItem;
import com.example.fypapplication_waster.retrofit.model.BinToBeReceived;
import com.example.fypapplication_waster.retrofit.GetDataService;
import com.example.fypapplication_waster.util.Constants;
import com.example.fypapplication_waster.util.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecycleFragment extends Fragment implements OnLocationUpdatedListener {
    GetDataService service;

    List<BinToBeReceived> matchedBins;
    Double latitude, longitude;

    private List<WasteItemRecyclerViewItem> wasteItemList = null;

    private final static String TAG = "RecycleFragment";
    public final static int MY_PERMISSIONS_REQUEST_LOCATION = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        startLocation();

        if (latitude == null || longitude == null) {
            getLastLocation();
        }

        service = RetrofitUtils.getRetrofitClientInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycle_fragment_layout, container, false);

        final Button btnFindBinAABattery = rootView.findViewById(R.id.btnFindABinAABattery);
        btnFindBinAABattery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBinsWithMaterial(Constants.AA_BATTERY, latitude, longitude);
            }
        });

        final Button btnFindBinCardboard = rootView.findViewById(R.id.btnFindABinCardboard);
        btnFindBinCardboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBinsWithMaterial(Constants.CARDBOARD, latitude, longitude);
            }
        });

        final Button btnFindBinClearGlass = rootView.findViewById(R.id.btnFindABinClearGlass);
        btnFindBinClearGlass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBinsWithMaterial(Constants.CLEAR_GLASS, latitude, longitude);
            }
        });

        final Button btnFindBinDrinkCan = rootView.findViewById(R.id.btnFindABinDrinkCan);
        btnFindBinDrinkCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBinsWithMaterial(Constants.DRINK_CAN, latitude, longitude);
            }
        });

        final Button btnFindBinPlasticBottle = rootView.findViewById(R.id.btnFindABinPlasticBottle);
        btnFindBinPlasticBottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBinsWithMaterial(Constants.PLASTIC_BOTTLE, latitude, longitude);
            }
        });

        final Button btnFindBinPlasticFoodWrappers = rootView.findViewById(R.id.btnFindABinFoodWrappers);
        btnFindBinPlasticFoodWrappers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBinsWithMaterial(Constants.PLASTIC_FOOD_WRAPPER, latitude, longitude);
            }
        });

        return rootView;
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


    public List<BinToBeReceived> getBinsWithMaterial(String material, Double latitude, Double longitude) {
        Call<List<BinToBeReceived>> call = service.getBinsAtLocationAndMaterial(material, latitude, longitude);

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

                    launchMapIntent();
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


    private void launchMapIntent() {
        Intent myIntent = new Intent(getContext(), MapsActivity.class);
        myIntent.putExtra("latitude", latitude);
        myIntent.putExtra("longitude", longitude);
        myIntent.putExtra("bins", (ArrayList<BinToBeReceived>) matchedBins);
        startActivity(myIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocation();
                }

                else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
