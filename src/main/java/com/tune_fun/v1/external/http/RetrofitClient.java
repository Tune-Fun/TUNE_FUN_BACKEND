package com.tune_fun.v1.external.http;

import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private RetrofitClient() {
    }

    public static Retrofit getGoogleInstance() {
        return RetrofitClientHolder.GOOGLE_INSTANCE;
    }

    public static Retrofit getAppleInstance() {
        return RetrofitClientHolder.APPLE_INSTANCE;
    }

    private static class RetrofitClientHolder {
        private static final String GOOGLE_OAUTH2_BASE_URL = "https://oauth2.googleapis.com/";
        private static final String APPLE_OAUTH2_BASE_URL = "https://appleid.apple.com/auth/";
        private static final Retrofit GOOGLE_INSTANCE = getRetrofit(GOOGLE_OAUTH2_BASE_URL);
        private static final Retrofit APPLE_INSTANCE = getRetrofit(APPLE_OAUTH2_BASE_URL);
    }

    private static Retrofit getRetrofit(@NotNull final String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(getOkHttpClient())
                .build();
    }

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

}