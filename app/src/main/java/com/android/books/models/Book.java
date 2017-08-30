package com.android.books.models;



import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import java.util.Arrays;
import java.util.List;


public class Book implements Parcelable{

  @SerializedName("isbn")
  private List<String> isbnList;
  @SerializedName("author_name")
  private List<String> author;
  @SerializedName("title")
  private String title;
  @SerializedName("cover_edition_key")
  private String cover_id;
  @SerializedName("publisher")
  private List<String> publisher;
  @SerializedName("lccn")
  private List<String> lccnList;

  private String authorName;
  private String[] authorArray;

  public String getTitle() {
    return title;
  }

  public List<String> getAuthor() {
    return author;
  }

  public List<String> getLccnList() {
    return lccnList;
  }

  public String getAuthorName(){
    if(author!=null){
      authorArray=new String[getAuthor().size()];
    for (int i=0; i< getAuthor().size();i++){
        authorArray[i]=getAuthor().get(i);
        authorName= TextUtils.join(",",authorArray);

    }
    }
    else {
      authorName="Unknown";
    }
    return authorName;
  }

  public String getCoverUrl() {
    return "http://covers.openlibrary.org/b/olid/" + cover_id + "-M.jpg?default=false";
  }

  public String getLargeCoverUrl() {
    return "http://covers.openlibrary.org/b/olid/" + cover_id + "-L.jpg?default=false";
  }

  public List<String> getIsbnList() {
    return isbnList;
  }

  public List<String> getPublisher() {
    return publisher;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeStringList(this.isbnList);
    dest.writeStringList(this.author);
    dest.writeString(this.title);
    dest.writeString(this.cover_id);
    dest.writeStringList(this.publisher);
    dest.writeStringList(this.lccnList);
    dest.writeString(this.authorName);
  }

  public Book() {
  }

  protected Book(Parcel in) {
    this.isbnList = in.createStringArrayList();
    this.author = in.createStringArrayList();
    this.title = in.readString();
    this.cover_id = in.readString();
    this.publisher = in.createStringArrayList();
    this.lccnList = in.createStringArrayList();
    this.authorName = in.readString();
  }

  public static final Creator<Book> CREATOR = new Creator<Book>() {
    @Override
    public Book createFromParcel(Parcel source) {
      return new Book(source);
    }

    @Override
    public Book[] newArray(int size) {
      return new Book[size];
    }
  };

  @Override
  public String toString() {
    return "Book{" +
            "isbnList=" + isbnList +
            ", author=" + author +
            ", title='" + title + '\'' +
            ", cover_id='" + cover_id + '\'' +
            ", publisher=" + publisher +
            ", lccnList=" + lccnList +
            ", authorName='" + authorName + '\'' +
            ", authorArray=" + Arrays.toString(authorArray) +
            '}';
  }
}
