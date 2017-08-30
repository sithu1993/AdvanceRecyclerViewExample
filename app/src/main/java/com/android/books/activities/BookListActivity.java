package com.android.books.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.books.R;
import com.android.books.adapters.BookRecyclerAdapter;
import com.android.books.models.Book;
import com.android.books.models.SearchResponse;
import com.android.books.rest.RestClient;
import com.android.books.rest.service.SearchService;
import com.android.books.utils.RecyclerItemClickListener;
import com.android.books.utils.custom.PaginationScollListener;
import com.android.books.utils.network.NetworkUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookListActivity extends AppCompatActivity {
    @BindView(R.id.recyclerBooks)
    RecyclerView recyclerView;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;

    private BookRecyclerAdapter bookRecyclerAdapter;
    private List<Book> bookList;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);


        ButterKnife.bind(this);
        setUpRecycler();

    }

    private void setUpRecycler() {
        bookRecyclerAdapter = new BookRecyclerAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(bookRecyclerAdapter);
        recyclerView.setHasFixedSize(true);


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(BookListActivity.this, BookDetailActivity.class);
                intent.putExtra("book", bookRecyclerAdapter.getItem(position));
                startActivity(intent);


            }
        }));

        recyclerView.addOnScrollListener(new PaginationScollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchBooks(searchQuery);
                    }
                }, 1000);
            }


            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

    }

    /**
    *   @param query string from search view
    * */
    private void fetchBooks(String query) {
        if (NetworkUtil.isNetworkConnected(this)) {
            if (currentPage == 1) {
                progressBar.setVisibility(View.VISIBLE);
            }
            Retrofit retrofit = RestClient.getRetrofit(this);
            SearchService searchService = retrofit.create(SearchService.class);
            searchService.getSearchResult(query, currentPage).enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    if (response.body() != null) {
                        if (currentPage != 1) {
                            bookRecyclerAdapter.removeLoading();
                            isLoading = false;
                        }
                        bookList = response.body().getBookList();
                        if (bookList.size() > 0) {
                            bookRecyclerAdapter.addAll(bookList);
                        } else {
                            Toast.makeText(BookListActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                        }

                        if (bookList.size() < 100) {
                            isLastPage = true;
                        } else {
                            bookRecyclerAdapter.addLoading();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            Toast.makeText(this, "Please Connect to internet", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_list, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView view = (SearchView) MenuItemCompat.getActionView(item);
        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentPage = 1;
                if (bookList != null) {
                    bookRecyclerAdapter.clear();
                }
                searchQuery = query;
                fetchBooks(searchQuery);
                view.clearFocus();
                view.setQuery("", false);
                view.setIconified(true);
                item.collapseActionView();
                BookListActivity.this.setTitle(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
