package com.bank.skbankofindia.api;

import com.bank.skbankofindia.model.ApiResponse;
import com.bank.skbankofindia.model.LoginRequest;
import com.bank.skbankofindia.model.RegisterRequest;
import com.bank.skbankofindia.model.TransferRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("auth/login")
    Call<ApiResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<ApiResponse> register(@Body RegisterRequest registerRequest);

    @GET("protected/accounts")
    Call<ApiResponse> getCustomerAccounts(@Header("Authorization") String token);

    @GET("protected/accounts/{accountNo}/statement")
    Call<ApiResponse> getAccountStatement(
            @Header("Authorization") String token,
            @Path("accountNo") String accountNo
    );

    @POST("protected/transfers")
    Call<ApiResponse> processFundTransfer(
            @Header("Authorization") String token,
            @Body TransferRequest transferRequest
    );

    @POST("protected/transfers/internal")
    Call<ApiResponse> processInternalTransfer(
            @Header("Authorization") String token,
            @Body TransferRequest transferRequest
    );

    @GET("app/version")
    Call<ApiResponse> getAppVersion();

    @GET("app/check-update")
    Call<ApiResponse> checkAppUpdate();
}
