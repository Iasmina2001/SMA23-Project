package com.sma.proiect.librarian;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.BookRequest;
import com.sma.proiect.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class BookRequestsAdapterLibrarianActivity extends ArrayAdapter<BookRequest> {

    private Context context;
    private List<BookRequest> bookRequests;
    private int layoutResID;

    public BookRequestsAdapterLibrarianActivity(Context context, int layoutResourceID, List<BookRequest> bookRequests) {
        super(context, layoutResourceID, bookRequests);
        this.context = context;
        this.bookRequests = bookRequests;
        this.layoutResID = layoutResourceID;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookRequestsAdapterLibrarianActivity.ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new BookRequestsAdapterLibrarianActivity.ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.bAccept = view.findViewById(R.id.bAcceptReq);
            itemHolder.bClearFinesAndRequest = view.findViewById(R.id.bClearFines);
            itemHolder.tTitle = view.findViewById(R.id.tTitle);
            itemHolder.tISBN10 = view.findViewById(R.id.tISBN10Value);
            itemHolder.tUserID = view.findViewById(R.id.tUserIDValue);
            itemHolder.tFine = view.findViewById(R.id.tFineValue);
            itemHolder.tRequestStatus = view.findViewById(R.id.tRequestStatusValue);

            view.setTag(itemHolder);

        } else {
            itemHolder = (BookRequestsAdapterLibrarianActivity.ItemHolder) view.getTag();
        }

        // current values of book
        final BookRequest bItem = bookRequests.get(position);

        String title = bItem.getTitle();
        String ISBN10 = bItem.getISBN10();
        String fine = bItem.getFine();
        String userID = bItem.getCurrentUID();
        String requestStatus = bItem.getRequestStatus();
        BookRequest currentBookRequest = new BookRequest(fine, ISBN10, title, userID, "1", requestStatus);

        itemHolder.tTitle.setText(title);
        itemHolder.tISBN10.setText(ISBN10);
        itemHolder.tUserID.setText(userID);
        itemHolder.tFine.setText(fine);
        if (requestStatus.equals("0")) {
            itemHolder.tRequestStatus.setText("Not accepted");
        } else if (requestStatus.equals("1")) {
            itemHolder.tRequestStatus.setText("Accepted");
        }

        itemHolder.bAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // accept request
                acceptRequest(0, userID, ISBN10);
            }
        });

        itemHolder.bClearFinesAndRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // clear fines and book request
                AppState.get().setCurrentBookRequest(currentBookRequest);
                clearFinesAndRequest(ISBN10, userID, currentBookRequest);
            }
        });

        return view;
    }

    public static class ItemHolder {
        TextView tTitle;
        TextView tISBN10;
        TextView tFine;
        TextView tUserID;
        TextView tRequestStatus;
        Button bAccept, bClearFinesAndRequest;
    }

    private void acceptRequest(int days, String readerID, String ISBN10) {
    /**
     * @param days: number of days within the reader must return the book to the library
     * The function calculates the start date and the end date for a borrow and adds the dates
     * for each book to Firebase.
     */
        String dateFormat = "dd.mm.yyyy";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat, Locale.getDefault());
        String startDate = s.format(new Date(cal.getTimeInMillis()));
        cal.add(Calendar.DAY_OF_YEAR, days);
        String endDate = s.format(new Date(cal.getTimeInMillis()));

        DatabaseReference databaseReference = AppState.get().getDatabaseReference();

        for (int i = 0; i < bookRequests.size(); i++) {
            BookRequest currentBook = bookRequests.get(i);
            String currentBookISBN10 = currentBook.getISBN10();
            if (currentBookISBN10.equals(ISBN10)) {
                databaseReference.child("bookRequests").child(readerID).child(ISBN10).child("Start date").setValue(startDate);
                databaseReference.child("bookRequests").child(readerID).child(ISBN10).child("End date").setValue(endDate);
            }
        }
    }

    /**
     * The librarian clears the request if the reader returns the lent book in time.
     * Another situation in which the librarian clears the request is when the reader returned late
     * the book, but payed his fines.
     */
    private void clearFinesAndRequest(String ISBN10, String readerID, BookRequest currentBookRequest) {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        databaseReference.child("bookRequests").child(readerID).child(ISBN10).removeValue();
        databaseReference.child("bookRequests").child(readerID).child("Fine").setValue(0);
        databaseReference.child("bookRequests").child(readerID).child("Forbid access").removeValue();
        AppState.get().setCurrentBookRequest(currentBookRequest);
    }
}