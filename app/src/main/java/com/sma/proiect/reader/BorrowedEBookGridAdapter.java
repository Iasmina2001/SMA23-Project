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
import com.sma.proiect.helpers.StorageOperationHelper;
import java.util.List;


public class BorrowedEBookGridAdapter extends ArrayAdapter<Book> implements PopupMenu.OnMenuItemClickListener {

    private Context context;
    private int layoutResID;
    private List<Book> eBooks;
    private String className;

    BorrowedEBookGridAdapter(Context context, int layoutResourceID, List<Book> eBooks, String className) {
        super(context, layoutResourceID, eBooks);
        this.context = context;
        this.layoutResID = layoutResourceID;
        this.eBooks = eBooks;
        this.className = className;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BorrowedEBookGridAdapter.ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new BorrowedEBookGridAdapter.ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.tTitle = view.findViewById(R.id.tTitle);
            itemHolder.tAuthor = view.findViewById(R.id.tAuthor);
            itemHolder.iCover = view.findViewById(R.id.iCover);
            itemHolder.ratingBar = view.findViewById(R.id.ratingBar);

            view.setTag(itemHolder);

        } else {
            itemHolder = (BorrowedEBookGridAdapter.ItemHolder) view.getTag();
        }

        // current values of menuItem
        final Book eBook = eBooks.get(position);
        String sTitle = eBook.getTitle();
        String sAuthor = eBook.getAuthor();
        String sISBN10 = eBook.getISBN10();
        String databasePath = eBook.getRealtimeDatabasePath();
        String sAverageRating = eBook.getAverageRatingValue();
        float fRatingValue = 0;
        try {
            fRatingValue = Float.parseFloat(sAverageRating);
        } catch (NumberFormatException nfe) {
            // :)
        }

        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        itemHolder.tTitle.setText(sTitle);
        itemHolder.tAuthor.setText(sAuthor);
        itemHolder.ratingBar.setRating(fRatingValue);
        databaseOperationHelper.setBookCoverReader(context, itemHolder.iCover, eBook);

        itemHolder.iCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!className.equals("com.sma.proiect.reader.ReadingChallengeActivity")) {
                    AppState.get().setCurrentBook(eBooks.get(position));
                    showMenu(view);
                }
            }
        });

        return view;
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener(this);    // This activity implements OnMenuItemClickListener
        popup.inflate(R.menu.popup_menu_borrowed_ebooks);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Book currentBook = AppState.get().getCurrentBook();
        String ISBN10 = currentBook.getISBN10();
        StorageOperationHelper storageOperationHelper = new StorageOperationHelper();
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        switch (item.getItemId()) {
            case R.id.nav_set_book_as_finished:
                databaseOperationHelper.returnFinishedEBook(ISBN10);
                return true;
            case R.id.nav_view_ebook:
                databaseOperationHelper.openEBook(context, ISBN10);
                //storageOperationHelper.openEBook(context, ISBN10);
                return true;
            default:
                return false;
        }
    }

    public static class ItemHolder {
        TextView tTitle, tAuthor;
        ImageView iCover;
        RatingBar ratingBar;
    }
}