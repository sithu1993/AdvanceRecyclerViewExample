package com.android.books.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Geek on 8/22/17.
 */

public class SearchResponse {
    @SerializedName("numFound")
    private int numberFound;
    @SerializedName("docs")
    private List<Book> bookList;

    public int getNumberFound() {
        return numberFound;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "numberFound=" + numberFound +
                ", bookList=" + bookList +
                '}';
    }
}
