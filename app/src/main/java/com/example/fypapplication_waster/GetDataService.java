package com.example.fypapplication_waster;

import com.example.fypapplication_waster.model.AddBinResponse;
import com.example.fypapplication_waster.model.BinToBeReceived;
import com.example.fypapplication_waster.model.BinToBeSent;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
