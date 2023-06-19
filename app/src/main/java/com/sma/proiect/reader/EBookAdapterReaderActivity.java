package com.sma.proiect.reader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.BookType;
import com.sma.proiect.R;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import com.sma.proiect.user.User;
import java.util.List;


public class EBookAdapterReaderActivity extends ArrayAdapter<Book> {

    private Context context;
    private List<Book> eBooks;
    private int layoutResID;
    private DatabaseOperationHelper databaseOperationHelper;

    public EBookAdapterReaderActivity(Context context, int layoutResourceID, List<Book> eBooks) {
        super(context, layoutResourceID, eBooks);
        this.context = context;
        this.eBooks = eBooks;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EBookAdapterReaderActivity.ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new EBookAdapterReaderActivity.ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.lHeader = view.findViewById(R.id.lHeader);
            itemHolder.tTitle = view.findViewById(R.id.tTitle);
            itemHolder.tAuthor = view.findViewById(R.id.tAuthorValue);
            itemHolder.tPublisher = view.findViewById(R.id.tPublisherValue);
            itemHolder.tPublicationDate = view.findViewById(R.id.tPublicationDateValue);
            itemHolder.tGenre = view.findViewById(R.id.tGenreValue);
            itemHolder.tISBN10 = view.findViewById(R.id.tISBN10Value);
            itemHolder.tNumOfBooksValue = view.findViewById(R.id.tNumOfBooksValue);
            itemHolder.bBorrowEBook = view.findViewById(R.id.bBorrowEBook);

            view.setTag(itemHolder);

        } else {
            itemHolder = (EBookAdapterReaderActivity.ItemHolder) view.getTag();
        }

        // current values of eBook
        final Book bItem = eBooks.get(position);
        String title = bItem.getTitle();
        String author = bItem.getAuthor();
        String publisher = bItem.getPublisher();
        String publicationDate = bItem.getPublicationDate();
        String genre = bItem.getGenre();
        String ISBN10 = bItem.getISBN10();
        String tNumOfBooksValue = bItem.getNumOfBooks();

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

        itemHolder.bBorrowEBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request book
                databaseOperationHelper.borrowEBookIfNotAlreadyBorrowed(context, title, author, ISBN10);
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
        Button bBorrowEBook;
    }
}