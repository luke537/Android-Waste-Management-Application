package com.example.fypapplication_waster;

import com.example.fypapplication_waster.model.BinToBeReceived;
import com.example.fypapplication_waster.model.BinToBeSent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("bins")
    Call<List<BinToBeReceived>> getAllBins();

    @GET("bins/{latitude}/{longitude}")
    Call<List<BinToBeReceived>> getBinsAtLocation(@Path("latitude") Double latitude, @Path("longitude") Double longitude);

    @POST("add_bin")
    Call<BinToBeSent> addBin(@Body BinToBeSent bin);
}