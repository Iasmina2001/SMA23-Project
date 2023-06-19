package com.sma.proiect.reader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.BookType;
import com.sma.proiect.R;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import com.sma.proiect.user.User;
import java.util.List;


public class BookAdapterReaderActivity extends ArrayAdapter<Book> {

    private Context context;
    private List<Book> books;
    private int layoutResID;
    private DatabaseOperationHelper databaseOperationHelper;

    public BookAdapterReaderActivity(Context context, int layoutResourceID, List<Book> books) {
        super(context, layoutResourceID, books);
        this.context = context;
        this.books = books;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookAdapterReaderActivity.ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new BookAdapterReaderActivity.ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.lHeader = view.findViewById(R.id.lHeader);
            itemHolder.tTitle = view.findViewById(R.id.tTitle);
            itemHolder.tAuthor = view.findViewById(R.id.tAuthorValue);
            itemHolder.tPublisher = view.findViewById(R.id.tPublisherValue);
            itemHolder.tPublicationDate = view.findViewById(R.id.tPublicationDateValue);
            itemHolder.tGenre = view.findViewById(R.id.tGenreValue);
            itemHolder.tISBN10 = view.findViewById(R.id.tISBN10Value);
            itemHolder.tNumOfBooksValue = view.findViewById(R.id.tNumOfBooksValue);
            itemHolder.iAdd = view.findViewById(R.id.iAddReq);

            view.setTag(itemHolder);

        } else {
            itemHolder = (BookAdapterReaderActivity.ItemHolder) view.getTag();
        }

        // current values of book
        final Book bItem = books.get(position);
        String title = bItem.getTitle();
        String author = bItem.getAuthor();
        String publisher = bItem.getPublisher();
        String publicationDate = bItem.getPublicationDate();
        String genre = bItem.getGenre();
        String ISBN10 = bItem.getISBN10();
        String tNumOfBooksValue = bItem.getNumOfBooks();

        itemHolder.iAdd.setVisibility(View.VISIBLE);
        itemHolder.lHeader.setBackgroundColor(BookType.getColorFromBookGenre(bItem.getGenre()));
        itemHolder.tTitle.setText(title);
        itemHolder.tAuthor.setText(author);
        itemHolder.tPublisher.setText(publisher);
        itemHolder.tPublicationDate.setText(publicationDate);
        itemHolder.tGenre.setText(genre);
        itemHolder.tISBN10.setText(ISBN10);
        itemHolder.tNumOfBooksValue.setText(tNumOfBooksValue);

        User currentUser = AppState.get().getCurrentUser();
        databaseOperationHelper = new DatabaseOperationHelper();

        itemHolder.iAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request book
                databaseOperationHelper.requestBook(context, currentUser, ISBN10, title);
            }
        });

        return view;
    }

    public static class ItemHolder {
        TextView tTitle;
        TextView tAuthor;
        TextView tPublisher;
        TextView tPublicationDate;
        TextView tGenre;
        TextView tISBN10;
        TextView tNumOfBooksValue;
        RelativeLayout lHeader;
        ImageView iAdd;
    }
}