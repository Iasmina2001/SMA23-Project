package com.sma.proiect.librarian;

import android.app.Activity;
import android.content.Context;
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
import com.sma.proiect.helpers.DatabaseOperationHelper;
import java.util.List;


public class EBookAdapterLibrarianActivity extends ArrayAdapter<Book> {

    private Context context;
    private List<Book> eBooks;
    private int layoutResID;
    private DatabaseOperationHelper databaseOperationHelper;

    public EBookAdapterLibrarianActivity(Context context, int layoutResourceID, List<Book> eBooks) {
        super(context, layoutResourceID, eBooks);
        this.context = context;
        this.eBooks = eBooks;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EBookAdapterLibrarianActivity.ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new EBookAdapterLibrarianActivity.ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.lHeader = view.findViewById(R.id.lHeader);
            itemHolder.iEdit = view.findViewById(R.id.iEdit);
            itemHolder.iDelete = view.findViewById(R.id.iDelete);
            itemHolder.tTitle = view.findViewById(R.id.tTitle);
            itemHolder.tAuthor = view.findViewById(R.id.tAuthorValue);
            itemHolder.tPublisher = view.findViewById(R.id.tPublisherValue);
            itemHolder.tPublicationDate = view.findViewById(R.id.tPublicationDateValue);
            itemHolder.tGenre = view.findViewById(R.id.tGenreValue);
            itemHolder.tISBN10 = view.findViewById(R.id.tISBN10Value);
            itemHolder.tNumOfBooks = view.findViewById(R.id.tNumOfBooksValue);

            view.setTag(itemHolder);

        } else {
            itemHolder = (EBookAdapterLibrarianActivity.ItemHolder) view.getTag();
        }

        // current values of eBook
        final Book bItem = eBooks.get(position);

        String title = bItem.getTitle();
        String author = bItem.getAuthor();
        String publisher = bItem.getPublisher();
        String publicationDate = bItem.getPublicationDate();
        String genre = bItem.getGenre();
        String ISBN10 = bItem.getISBN10();
        String numOfBooks = bItem.getNumOfBooks();

        itemHolder.lHeader.setBackgroundColor(BookType.getColorFromBookGenre(bItem.getGenre()));
        itemHolder.tTitle.setText(bItem.getTitle());
        itemHolder.tAuthor.setText(bItem.getAuthor());
        itemHolder.tPublisher.setText(bItem.getPublisher());
        itemHolder.tPublicationDate.setText(bItem.getPublicationDate().toString());
        itemHolder.tGenre.setText(bItem.getGenre());
        itemHolder.tISBN10.setText(bItem.getISBN10());
        itemHolder.tNumOfBooks.setText(bItem.getNumOfBooks());

        databaseOperationHelper = new DatabaseOperationHelper();

        itemHolder.iEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // edit book at selected position
                AppState.get().setCurrentBook(bItem);
                databaseOperationHelper.editBook(context);
            }
        });

        itemHolder.iDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete book at selected position
                AppState.get().setCurrentBook(bItem);
                databaseOperationHelper.deleteBook(context, ISBN10, "eBooks");
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
        TextView tNumOfBooks;
        RelativeLayout lHeader;
        ImageView iEdit, iDelete;
    }
}