package com.example.digitalrefrige.services;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DigitalFridgeService {
    @GET("test1")
    Call<String> testHelloWorld();

    @Multipart
    @POST("photo")
    Call<String> getCVResult(@Part MultipartBody.Part filePart);

}
