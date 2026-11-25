package com.bank.skbankofindia.api;

import com.bank.skbankofindia.model.ApiResponse;
import com.bank.skbankofindia.model.LoginRequest;
import com.bank.skbankofindia.model.RegisterRequest;
import com.bank.skbankofindia.model.TransferRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("app/version")
    Call<ApiResponse> getAppVersion();

    // UPI QR Endpoints
    @GET("protected/upi/qr/me")
    Call<ApiResponse> getMyPaymentQr(@Header("Authorization") String token);

    @POST("protected/upi/qr/resolve")
    Call<ApiResponse> resolveQrVpa(@Header("Authorization") String token, @Body Map<String, String> body);

    @POST("protected/upi/qr/pay")
    Call<ApiResponse> executeQrPayment(@Header("Authorization") String token, @Body Map<String, Object> body);

    // Chat & Send Money Endpoints
    @GET("protected/chat/search")
    Call<ApiResponse> searchChatCustomers(@Header("Authorization") String token, @Query("q") String query);

    @GET("protected/chat/messages")
    Call<ApiResponse> getChatMessages(@Header("Authorization") String token, @Query("partnerUserId") long partnerUserId);

    @POST("protected/chat/messages")
    Call<ApiResponse> sendChatMessage(@Header("Authorization") String token, @Body Map<String, Object> body);

    @POST("protected/chat/pay")
    Call<ApiResponse> executeChatPayment(@Header("Authorization") String token, @Body Map<String, Object> body);

    // Transaction PIN Endpoint
    @POST("protected/accounts/pin/generate")
    Call<ApiResponse> updateTransactionPin(@Header("Authorization") String token, @Body Map<String, String> body);
}
