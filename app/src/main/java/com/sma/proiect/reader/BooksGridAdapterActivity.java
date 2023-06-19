package com.sma.proiect.reader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.widget.PopupMenu;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.R;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import java.util.List;


public class BooksGridAdapterActivity extends ArrayAdapter<Book> implements PopupMenu.OnMenuItemClickListener {

    private Context context;
    private int layoutResID;
    private List<Book> books;
    private String databasePath;
    private DatabaseOperationHelper databaseOperationHelper;

    public BooksGridAdapterActivity(Context context, int layoutResourceID, List<Book> books) {
        super(context, layoutResourceID, books);
        this.context = context;
        this.layoutResID = layoutResourceID;
        this.books = books;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BooksGridAdapterActivity.ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new BooksGridAdapterActivity.ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.tTitle = view.findViewById(R.id.tTitle);
            itemHolder.tAuthor = view.findViewById(R.id.tAuthor);
            itemHolder.iCover = view.findViewById(R.id.iCover);
            itemHolder.ratingBar = view.findViewById(R.id.ratingBar);
            view.setTag(itemHolder);

        } else {
            itemHolder = (BooksGridAdapterActivity.ItemHolder) view.getTag();
        }

        final Book book = books.get(position);
        String sTitle = book.getTitle();
        String sAuthor = book.getAuthor();
        String ISBN10 = book.getISBN10();
        databasePath = book.getRealtimeDatabasePath();
        String sAverageRating = book.getAverageRatingValue();
        float fRatingValue = 0;
        try {
            fRatingValue = Float.parseFloat(sAverageRating);
        } catch (NumberFormatException nfe) {
            // :)
        }

        databaseOperationHelper = new DatabaseOperationHelper();

        itemHolder.tTitle.setText(sTitle);
        itemHolder.tAuthor.setText(sAuthor);
        itemHolder.ratingBar.setRating(fRatingValue);
        databaseOperationHelper.setBookCoverReader(context, itemHolder.iCover, book);

        itemHolder.iCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppState.get().setCurrentBook(books.get(position));
                showMenu(view);
            }
        });

        return view;
    }

    public static class ItemHolder {
        TextView tTitle, tAuthor;
        ImageView iCover;
        RatingBar ratingBar;
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener(this);    // This activity implements OnMenuItemClickListener
        popup.inflate(R.menu.popup_menu_reader_book_operations);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String ISBN10 = AppState.get().getCurrentBook().getISBN10();
        switch (item.getItemId()) {
            case R.id.nav_add_book_to_read:
                databaseOperationHelper.addBookToRead(context, ISBN10);
                return true;
            case R.id.nav_add_book_to_currently_reading:
                databaseOperationHelper.addBookToCurrentlyReading(context, ISBN10);
                return true;
            default:
                return false;
        }
    }
}