package com.example.fypapplication_waster.retrofit;

import com.example.fypapplication_waster.retrofit.model.BinToBeReceived;
import com.example.fypapplication_waster.retrofit.model.BinToBeSent;
import com.example.fypapplication_waster.retrofit.model.WasteMaterial;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GetDataService {

    @GET("bins")
    Call<List<BinToBeReceived>> getAllBins();

    @GET("bins/{latitude}/{longitude}")
    Call<List<BinToBeReceived>> getBinsAtLocation(@Path("latitude") Double latitude, @Path("longitude") Double longitude);

    @GET("bins/{material}/{latitude}/{longitude}")
    Call<List<BinToBeReceived>> getBinsAtLocationAndMaterial(@Path("material") String material, @Path("latitude") Double latitude, @Path("longitude") Double longitude);

    @POST("add_bin")
    Call<ResponseBody> addBin(@Body BinToBeSent bin);

//    @Multipart
//    @POST("add_bin")
//    Call<ResponseBody> addBin(@PartMap() Map<String, RequestBody> partMap,
//                              @Part MultipartBody.Part image);
}
