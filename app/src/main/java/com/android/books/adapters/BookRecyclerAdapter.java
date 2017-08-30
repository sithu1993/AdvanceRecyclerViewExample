package com.android.books.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.books.R;
import com.android.books.models.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BookRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private Context context;
    private List<Book> bookList = new ArrayList<>();


    public BookRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.lay_loading, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Book book = bookList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final BookVH bookVH = (BookVH) holder;
                bookVH.txtTvTitle.setText(book.getTitle());
                bookVH.txtAuther.setText(book.getAuthorName());
                Picasso.with(context)
                        .load(Uri.parse(book.getCoverUrl()))
                        .error(R.drawable.ic_nocover)
                        .into(bookVH.imgBookCover);

                break;
            case LOADING:
                break;

        }

    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_book, parent, false);
        viewHolder = new BookVH(v1);

        return viewHolder;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }

    public List<Book> getBookList() {
        return bookList;
    }


    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == bookList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    public void add(Book b) {
        bookList.add(b);
        notifyItemInserted(bookList.size() - 1);
    }

    public void addAll(List<Book> bookList) {
        for (Book book : bookList) {
            add(book);
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void remove(Book book) {
        int position = bookList.indexOf(book);
        if (position > -1) {
            bookList.remove(position);
            notifyItemRemoved(position);
        }

    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void addLoading() {
        isLoadingAdded = true;
        add(new Book());
    }

    public void removeLoading() {
        isLoadingAdded = false;

        int position = bookList.size() - 1;
        Book book = getItem(position);

        if (book != null) {
            bookList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Book getItem(int position) {
        return bookList.get(position);
    }

    protected class BookVH extends RecyclerView.ViewHolder {
        @BindView(R.id.ivBookCover)
        ImageView imgBookCover;
        @BindView(R.id.tvTitle)
        TextView txtTvTitle;
        @BindView(R.id.tvAuthor)
        TextView txtAuther;


        public BookVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {
        public LoadingVH(View itemView) {
            super(itemView);
        }
    }
}
