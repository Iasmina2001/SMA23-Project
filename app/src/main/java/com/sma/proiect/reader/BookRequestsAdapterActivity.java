package com.sma.proiect.reader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.R;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import com.sma.proiect.user.User;
import java.util.List;


public class BookRequestsAdapterActivity extends ArrayAdapter<Book> {

    private Context context;
    private List<Book> books;
    private int layoutResID;

    public BookRequestsAdapterActivity(Context context, int layoutResourceID, List<Book> books) {
        super(context, layoutResourceID, books);
        this.context = context;
        this.books = books;
        this.layoutResID = layoutResourceID;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookRequestsAdapterActivity.ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new BookRequestsAdapterActivity.ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.ISBN10 = view.findViewById(R.id.tISBN10Value);
            itemHolder.tTitle = view.findViewById(R.id.tTitle);
            itemHolder.iDelete = view.findViewById(R.id.iDelete);
            itemHolder.tRequestStatus = view.findViewById(R.id.tRequestValue);

            view.setTag(itemHolder);

        } else {
            itemHolder = (BookRequestsAdapterActivity.ItemHolder) view.getTag();
        }

        // current values of book
        final Book bItem = books.get(position);
        String ISBN10 = bItem.getISBN10();
        String title = bItem.getTitle();
        String requestStatus = bItem.getSubmitStatus();

        itemHolder.tTitle.setText(title);
        itemHolder.ISBN10.setText(ISBN10);
        if (requestStatus.equals("0")) {
            itemHolder.tRequestStatus.setText("Not submitted");
        } else {
            itemHolder.tRequestStatus.setText("Submitted");
        }

        User currentUser = AppState.get().getCurrentUser();
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();

        itemHolder.iDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete book request at position
                if (itemHolder.tRequestStatus.getText().toString().equals("Not submitted")) {
                    databaseOperationHelper.deleteBookRequest(currentUser, ISBN10);
                } else {
                    Toast.makeText(context, "Cannot delete submitted requests.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public static class ItemHolder {
        TextView tTitle;
        TextView ISBN10;
        TextView tRequestStatus;
        ImageView iDelete;
    }
}