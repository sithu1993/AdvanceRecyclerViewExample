package com.android.books.rest.service;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BookService {

    @GET("api/books")
    Call<JsonObject> getBook(@Query("bibkeys") String isbn, @Query("jscmd")String page,
                             @Query("format")String format);
}
