//package com.example.fypapplication_waster;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.FragmentActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.Manifest;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.example.fypapplication_waster.retrofit.model.BinToBeReceived;
//import com.example.fypapplication_waster.retrofit.network.RetrofitClientInstance;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.android.gms.common.api.Api;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.PendingResult;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnSuccessListener;
//
//import java.io.FileDescriptor;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class MainActivity_old extends AppCompatActivity
//        implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener, LocationListener{
//
//    private Location location;
//    private GoogleApiClient googleApiClient;
//    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//    private LocationRequest locationRequest;
//    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
//
//    // lists for permissions
//    private ArrayList<String> permissionsToRequest;
//    private ArrayList<String> permissionsRejected = new ArrayList<>();
//    private ArrayList<String> permissions = new ArrayList<>();
//    // integer for permissions results request
//    private static final int ALL_PERMISSIONS_RESULT = 1011;
//
//    private GetDataService service;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        // we add permissions we need to request location of the users
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
//        googleApiClient = new GoogleApiClient.Builder(this).
//                addApi(LocationServices.API).
//                addConnectionCallbacks(this).
//                addOnConnectionFailedListener(this).build();
//
//
//        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
//
//        Button btnSearchForBin = findViewById(R.id.btnSearchForBin);
//        btnSearchForBin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onSearchForBinButtonClick();
//            }
//        });
//    }
//
//    public void onSearchForBinButtonClick() {
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                &&  ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        // Permissions ok, we get last location
//        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//
//        if (location != null) {
//            Toast.makeText(this, "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(), Toast.LENGTH_LONG);
//            Double latitude = location.getLatitude();
//            Double longitude = location.getLongitude();
//
//            Call<List<BinToBeReceived>> call = service.getBinsAtLocation(latitude, longitude);
//
//            call.enqueue(new Callback<List<BinToBeReceived>>() {
//                @Override
//                public void onResponse(Call<List<BinToBeReceived>> call, Response<List<BinToBeReceived>> response) {
//                    if (response.isSuccessful()) {
//                        List<BinToBeReceived> matchedBins = response.body();
//                        // Logic to handle location object
//                        Intent myIntent = new Intent(getBaseContext(), MapsActivity.class);
//                        myIntent.putExtra("latitude", latitude);
//                        myIntent.putExtra("longitude", longitude);
//                        myIntent.putExtra("bins", (ArrayList<BinToBeReceived>) matchedBins);
//                        startActivity(myIntent);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<BinToBeReceived>> call, Throwable t) {
//                    Log.d("MainActivity", "No server response: Failure getting bins");
//                    t.printStackTrace();
//                }
//            });
//        }
//    }
//
//    public void onGoToAddButtonClick(View v){
//        Intent myIntent = new Intent(getBaseContext(), AddBinActivity.class);
//        startActivity(myIntent);
//    }
//
//    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
//        ArrayList<String> result = new ArrayList<>();
//
//        for (String perm : wantedPermissions) {
//            if (!hasPermission(perm)) {
//                result.add(perm);
//            }
//        }
//
//        return result;
//    }
//
//    private boolean hasPermission(String permission) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
//        }
//
//        return true;
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (googleApiClient != null) {
//            googleApiClient.connect();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (!checkPlayServices()) {
//            Toast.makeText(this, "You need to install Google Play Services to use the App properly", Toast.LENGTH_LONG);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        // stop location updates
//        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
//            googleApiClient.disconnect();
//        }
//    }
//
//    private boolean checkPlayServices() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
//
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
//            } else {
//                finish();
//            }
//
//            return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                &&  ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        // Permissions ok, we get last location
//        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//
//        if (location != null) {
//            Toast.makeText(this, "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(), Toast.LENGTH_LONG);
//        }
//
//        startLocationUpdates();
//    }
//
//    private void startLocationUpdates() {
//        locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(UPDATE_INTERVAL);
//        locationRequest.setFastestInterval(FASTEST_INTERVAL);
//
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                &&  ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
//        }
//
//        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        if (location != null) {
//            Toast.makeText(this, "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(), Toast.LENGTH_LONG);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch(requestCode) {
//            case ALL_PERMISSIONS_RESULT:
//                for (String perm : permissionsToRequest) {
//                    if (!hasPermission(perm)) {
//                        permissionsRejected.add(perm);
//                    }
//                }
//
//                if (permissionsRejected.size() > 0) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
//                            new AlertDialog.Builder(MainActivity.this).
//                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
//                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                requestPermissions(permissionsRejected.
//                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
//                                            }
//                                        }
//                                    }).setNegativeButton("Cancel", null).create().show();
//
//                            return;
//                        }
//                    }
//                } else {
//                    if (googleApiClient != null) {
//                        googleApiClient.connect();
//                    }
//                }
//
//                break;
//        }
//    }
//}
//
