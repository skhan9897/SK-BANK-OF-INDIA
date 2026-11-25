package com.bank.skbankofindia.api;

import android.content.Context;
import com.bank.skbankofindia.util.SessionManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {

    public static final String PRODUCTION_BASE_URL = "https://sk-bank-of-india-rzy4.onrender.com/api/v1/";
    public static final String WEBSITE_DOWNLOAD_URL = "https://sk-bank-of-india-rzy4.onrender.com/download-app.jsp";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        return getApiService(null);
    }

    public static ApiService getApiService(Context context) {
        if (retrofit == null || context != null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                                .header("Pragma", "no-cache")
                                .header("Expires", "0");

                        if (context != null) {
                            SessionManager sm = new SessionManager(context);
                            String token = sm.getAuthToken();
                            if (token != null && !token.isEmpty()) {
                                requestBuilder.header("Authorization", token);
                            }
                        }
                        return chain.proceed(requestBuilder.build());
                    });

            retrofit = new Retrofit.Builder()
                    .baseUrl(PRODUCTION_BASE_URL)
                    .client(clientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
