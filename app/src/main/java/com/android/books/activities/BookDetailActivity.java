package com.android.books.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.books.R;
import com.android.books.models.Book;
import com.android.books.rest.RestClient;
import com.android.books.rest.service.BookService;
import com.android.books.utils.network.NetworkUtil;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookDetailActivity extends AppCompatActivity {
  @BindView(R.id.ivBookCover)
  ImageView ivBookCover;
  @BindView(R.id.tvTitle)
  TextView tvTitle;
  @BindView(R.id.tvAuthor)
  TextView tvAuthor;
  @BindView(R.id.tvPublisher)
  TextView tvPublisher;
  @BindView(R.id.tvPageCount)
  TextView tvPageCount;

  private Book book;
  private final String ISBN="ISBN:";
  private final String LCCN="LCCN:";
  private String bookIDType;
  private String bookIDValue;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_book_detail);
    ButterKnife.bind(this);

    book =getIntent().getParcelableExtra("book");
    loadBook(book);
  }

  /**
   * @param book
   *
   * */

  private void loadBook(final Book book) {
    this.setTitle(book.getTitle());
    Picasso.with(this)
        .load(Uri.parse(book.getLargeCoverUrl()))
        .error(R.drawable.ic_nocover)
        .into(ivBookCover);
    tvTitle.setText(book.getTitle());
    tvAuthor.setText(book.getAuthorName());
    if(book.getPublisher()!=null) {
      String[] publisher = new String[book.getPublisher().size()];
      for (int i = 0; i < book.getPublisher().size(); i++) {
        publisher[i] = book.getPublisher().get(i);
        tvPublisher.setText(TextUtils.join(",", publisher));
      }
    }else {
      tvPublisher.setText("Unknown");
    }

    if(book.getIsbnList()!=null){
      bookIDValue=book.getIsbnList().get(0);
      bookIDType=ISBN;
    }else {
      bookIDValue=book.getLccnList().get(0);
      bookIDType=LCCN;
    }

    if(NetworkUtil.isNetworkConnected(this)) {
      Retrofit retrofit = RestClient.getRetrofit(this);
      BookService bookService = retrofit.create(BookService.class);
      bookService.getBook(bookIDType+bookIDValue, "data", "json").enqueue(new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
          if (response.body() != null) {

           if(bookIDType.equals(ISBN)){
             JsonObject jsonObject = (JsonObject) response.body().get(ISBN+ book.getIsbnList().get(0));
             if (jsonObject.has("number_of_pages")) {
               tvPageCount.setText("Number of Pages - " + jsonObject.get("number_of_pages").toString());
             } else {
               tvPageCount.setText("Number of Pages - 0");
             }

           }else {
             JsonObject jsonObject = (JsonObject) response.body().get(LCCN+ book.getLccnList().get(0));
             if (jsonObject.has("number_of_pages")) {
               tvPageCount.setText("Number of Pages - " + jsonObject.get("number_of_pages").toString());
             } else {
               tvPageCount.setText("Number of Pages - 0");
             }
           }




          }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
          Log.i("Si thu detail", t.getMessage());
        }
      });

    }
    else {
      Toast.makeText(this,"Please Connect to Internet",Toast.LENGTH_SHORT).show();
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_book_detail, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_share) {
      setShareIntent();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void setShareIntent() {
    ImageView ivImage = (ImageView) findViewById(R.id.ivBookCover);

    Uri bmpUri = getLocalBitmapUri(ivImage);
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.setType("*/*");
    shareIntent.putExtra(Intent.EXTRA_TEXT, (String) tvTitle.getText());
    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
    startActivity(Intent.createChooser(shareIntent, "Share Image"));
  }

  public Uri getLocalBitmapUri(ImageView imageView) {
    // Extract Bitmap from ImageView drawable
    Drawable drawable = imageView.getDrawable();
    Bitmap bmp = null;
    if (drawable instanceof BitmapDrawable){
      bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    } else {
      return null;
    }
    Uri bmpUri = null;
    try {

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
        file.getParentFile().mkdirs();
        if(file.exists())file.delete();
        FileOutputStream out = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
        out.close();
        bmpUri = Uri.fromFile(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bmpUri;
  }
}
