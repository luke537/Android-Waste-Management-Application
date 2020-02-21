package com.example.fypapplication_waster.util;

import android.util.Log;

import com.example.fypapplication_waster.GetDataService;
import com.example.fypapplication_waster.model.BinToBeReceived;
import com.example.fypapplication_waster.network.RetrofitClientInstance;

import java.util.List;

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
}
