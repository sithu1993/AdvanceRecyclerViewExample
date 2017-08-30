package com.android.books.rest.service;

import com.android.books.models.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;



public interface SearchService {
    @GET("search.json")
    Call<SearchResponse> getSearchResult(@Query("q") String search, @Query("page")int page);
}
