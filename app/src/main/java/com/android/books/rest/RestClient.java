package com.android.books.rest;

import android.content.Context;

import com.android.books.config.APIConfig;
import com.google.gson.Gson;

import java.io.IOException;


import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static Retrofit retrofit;
    private static okhttp3.OkHttpClient.Builder builder;


    public static Retrofit getRetrofit(final Context context) {


        if (retrofit == null) {
            builder = new okhttp3.OkHttpClient().newBuilder();
            // Logging Interceptor

            // Release APK change boolean vale here !!!!!!!!!!
            if (APIConfig.DEBUG) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(interceptor);
            }
            // Header Interceptor
            builder.addInterceptor(new Interceptor() {
                                       @Override
                                       public Response intercept(Chain chain) throws IOException {
                                           Request request = chain.request().newBuilder()
                                                   .build();
                                           return chain.proceed(request);
                                       }
                                   });

            retrofit = new Retrofit.Builder()
                    .baseUrl(APIConfig.API_ENDPOINT)
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .build();
        }
        return retrofit;
    }
}
