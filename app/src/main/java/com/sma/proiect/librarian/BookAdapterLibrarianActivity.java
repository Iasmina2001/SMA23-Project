package com.sma.proiect.librarian;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.BookType;
import com.sma.proiect.R;
import java.util.List;


public class BookAdapterLibrarianActivity extends ArrayAdapter<Book> {

    private Context context;
    private List<Book> books;
    private int layoutResID;

    public BookAdapterLibrarianActivity(Context context, int layoutResourceID, List<Book> books) {
        super(context, layoutResourceID, books);
        this.context = context;
        this.books = books;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.tIndex = view.findViewById(R.id.tIndex);
            itemHolder.lHeader = view.findViewById(R.id.lHeader);
            itemHolder.iEdit = view.findViewById(R.id.iEdit);
            itemHolder.iDelete = view.findViewById(R.id.iDelete);
            itemHolder.tTitle = view.findViewById(R.id.tTitle);
            itemHolder.tAuthor = view.findViewById(R.id.tAuthorValue);
            itemHolder.tPublisher = view.findViewById(R.id.tPublisherValue);
            itemHolder.tPublicationDate = view.findViewById(R.id.tPublicationDateValue);
            itemHolder.tGenre = view.findViewById(R.id.tGenreValue);
            itemHolder.tISBN10 = view.findViewById(R.id.tISBN10Value);
            itemHolder.tISBN13 = view.findViewById(R.id.tISBN13Value);

            view.setTag(itemHolder);

        } else {
            itemHolder = (ItemHolder) view.getTag();
        }

        // current values of book
        final Book bItem = books.get(position);

        String title = bItem.getTitle();
        String author = bItem.getAuthor();
        String publisher = bItem.getPublisher();
        String publicationDate = bItem.getPublicationDate();
        String genre = bItem.getGenre();
        String ISBN10 = bItem.getISBN10();
        String ISBN13 = bItem.getISBN13();

        itemHolder.tIndex.setText(String.valueOf(position + 1));
        itemHolder.lHeader.setBackgroundColor(BookType.getColorFromBookGenre(bItem.getGenre()));
        itemHolder.tTitle.setText(bItem.getTitle());
        itemHolder.tAuthor.setText(bItem.getAuthor());
        itemHolder.tPublisher.setText(bItem.getPublisher());
        itemHolder.tPublicationDate.setText(bItem.getPublicationDate().toString());
        itemHolder.tGenre.setText(bItem.getGenre());
        itemHolder.tISBN10.setText(bItem.getISBN10());
        itemHolder.tISBN13.setText(bItem.getISBN13());

        itemHolder.iEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ... edit book at "position"
                edit(title, author, publisher, publicationDate, genre, ISBN10, ISBN13);
            }
        });

        itemHolder.iDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ... delete book at "position"
                delete(ISBN10);
            }
        });

        return view;
    }

    private static class ItemHolder {
        TextView tIndex;
        TextView tTitle;
        TextView tAuthor;
        TextView tPublisher;
        TextView tPublicationDate;
        TextView tGenre;
        TextView tISBN10;
        TextView tISBN13;
        RelativeLayout lHeader;
        ImageView iEdit, iDelete;
    }

    private void delete(String ISBN10) {
        AppState.get().getDatabaseReference().child("books").child(ISBN10).removeValue();
    }

    private void edit(String title, String author, String publisher, String publicationDate, String genre, String ISBN10, String ISBN13) {
        Book currentBook = new Book(title, author, publisher, publicationDate, genre, ISBN10, ISBN13);

        AppState.get().setCurrentBook(currentBook);
        ((Activity)context).startActivity(new Intent(context.getApplicationContext(), AddBookLibrarianActivity.class));
    }
}