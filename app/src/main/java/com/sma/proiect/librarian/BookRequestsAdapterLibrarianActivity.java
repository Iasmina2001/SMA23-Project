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
import android.widget.Toast;
import com.sma.proiect.AppState;
import com.sma.proiect.BookRequest;
import com.sma.proiect.R;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import java.util.List;


public class BookRequestsAdapterLibrarianActivity extends ArrayAdapter<BookRequest> {

    private Context context;
    private List<BookRequest> bookRequests;
    private int layoutResID;
    private DatabaseOperationHelper databaseOperationHelper;

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
            itemHolder.bClearFines = view.findViewById(R.id.bClearFines);
            itemHolder.bClearRequest = view.findViewById(R.id.bClearRequest);
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
        BookRequest currentBookRequest = new BookRequest(
                fine,
                ISBN10,
                title,
                userID,
                "1",
                requestStatus
        );

        itemHolder.tTitle.setText(title);
        itemHolder.tISBN10.setText(ISBN10);
        itemHolder.tUserID.setText(userID);
        itemHolder.tFine.setText(fine);
        if (requestStatus.equals("0")) {
            itemHolder.tRequestStatus.setText("Not accepted");
        } else if (requestStatus.equals("1")) {
            itemHolder.tRequestStatus.setText("Accepted");
        }

        databaseOperationHelper = new DatabaseOperationHelper();

        itemHolder.bAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // accept request
                AppState.get().setCurrentBookRequest(currentBookRequest);
                int iFine = 0;

                try {
                    iFine = Integer.parseInt(itemHolder.tFine.getText().toString());
                } catch(NumberFormatException nfe) {
                    // :)
                }

                if (iFine > 0) {
                    Toast.makeText(context.getApplicationContext(), "Cannot accept book requests from readers with fines.", Toast.LENGTH_SHORT).show();
                } else {
                    if (itemHolder.tRequestStatus.getText().toString().equals("Not accepted")) {
                        // reader has two weeks to return the book
                        databaseOperationHelper.acceptRequest(14);
                    } else {
                        Toast.makeText(context.getApplicationContext(), "You have already accepted the book request.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        itemHolder.bClearRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppState.get().setCurrentBookRequest(currentBookRequest);
                int iFine = 0;

                try {
                    iFine = Integer.parseInt(itemHolder.tFine.getText().toString());
                } catch(NumberFormatException nfe) {
                    // :)
                }

                if (iFine > 0) {
                    Toast.makeText(context.getApplicationContext(), "Cannot delete requests from users with fines.", Toast.LENGTH_SHORT).show();
                } else {
                    databaseOperationHelper.clearBookRequestReturnedInTime(context, userID, ISBN10);
                }
            }
        });

        itemHolder.bClearFines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // clear fines and late unreturned book requests only if the reader returns all books
                AppState.get().setCurrentBookRequest(currentBookRequest);
                databaseOperationHelper.clearUnreturnedLateBookRequestsForCurrentUser(context, userID);
                databaseOperationHelper.clearFinesAndAllowAccessToAccount(userID);
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
        Button bAccept, bClearFines, bClearRequest;
    }
}