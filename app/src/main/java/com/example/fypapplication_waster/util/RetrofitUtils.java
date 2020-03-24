package com.example.fypapplication_waster.util;

import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.fypapplication_waster.retrofit.GetDataService;
import com.example.fypapplication_waster.retrofit.model.BinToBeReceived;
import com.example.fypapplication_waster.retrofit.model.BinToBeSent;
import com.example.fypapplication_waster.retrofit.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class RetrofitUtils {
    private final static GetDataService RETROFIT_CLIENT_INSTANCE = RetrofitClientInstance.getRetrofitInstance().create(GetDataService .class);
    private final static String TAG = "RetrofitUtils";
    private List<BinToBeReceived> matchedBins;

    public static GetDataService getRetrofitClientInstance() {
        return RETROFIT_CLIENT_INSTANCE;
    }

    public static List<BinToBeReceived> getBinsInRadiusRestCall(Double latitude, Double longitude, List<BinToBeReceived> matchedBins) {
        matchedBins = null; // reset matchedBins to null before every call to prevent caching of old results

        Call<List<BinToBeReceived>> call = getRetrofitClientInstance().getBinsAtLocation(latitude, longitude);

        call.enqueue(new Callback<List<BinToBeReceived>>() {
            @Override
            public void onResponse(Call<List<BinToBeReceived>> call, Response<List<BinToBeReceived>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Successful response getting bins");
//                    matchedBins = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<BinToBeReceived>> call, Throwable t) {
                Log.d(TAG, "No server response: Failure getting bins");
                t.printStackTrace();
//                matchedBins = null;
            }
        });

        return matchedBins;
    }

    public static void addBin(DialogFragment fragment, String name, Double latitude,
                              Double longitude, String firebaseImgRef, ArrayList materials,
                              String owner, Double price, String hours, boolean isInside, String buildingName,
                              String buildingFloor) {

        Call<ResponseBody> call = getRetrofitClientInstance().addBin(
                new BinToBeSent(name, latitude, longitude, firebaseImgRef,
                        materials, owner, Double.valueOf(price), hours,
                        isInside, buildingName, buildingFloor));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(fragment.getTag(), response.message());

                if (response.isSuccessful()) {
                    Toast.makeText(fragment.getContext(), "Bin added successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(fragment.getTag(), "Bin Added Successfully");
                    fragment.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(fragment.getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                Log.e(fragment.getTag(), "Connection Failed\n" + t.getMessage());
                fragment.dismiss();
            }
        });
    }
}
