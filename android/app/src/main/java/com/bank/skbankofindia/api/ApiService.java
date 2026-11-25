package com.bank.skbankofindia.api;

import com.bank.skbankofindia.model.ApiResponse;
import com.bank.skbankofindia.model.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<ApiResponse> login(@Body LoginRequest loginRequest);

    @GET("protected/accounts")
    Call<ApiResponse> getCustomerAccounts(@Header("Authorization") String token);

    @POST("protected/transfers/internal")
    Call<ApiResponse> processFundTransfer(
            @Header("Authorization") String token,
            @Body Object transferPayload
    );
}
