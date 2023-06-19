package com.sma.proiect.reader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.sma.proiect.BookRequest;
import com.sma.proiect.R;
import java.util.List;


public class FinesAdapterActivity extends ArrayAdapter<BookRequest> {

    private Context context;
    private List<BookRequest> bookRequests;
    private int layoutResID;

    public FinesAdapterActivity(Context context, int layoutResourceID, List<BookRequest> bookRequests) {
        super(context, layoutResourceID, bookRequests);
        this.context = context;
        this.bookRequests = bookRequests;
        this.layoutResID = layoutResourceID;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FinesAdapterActivity.ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new FinesAdapterActivity.ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.ISBN10 = view.findViewById(R.id.tISBN10Value);
            itemHolder.tTitle = view.findViewById(R.id.tTitle);
            itemHolder.tEndDate = view.findViewById(R.id.tEndDateValue);

            view.setTag(itemHolder);

        } else {
            itemHolder = (FinesAdapterActivity.ItemHolder) view.getTag();
        }

        // current values of book
        final BookRequest bItem = bookRequests.get(position);
        String ISBN10 = bItem.getISBN10();
        String title = bItem.getTitle();
        String endDate = bItem.getEndDate();

        itemHolder.tTitle.setText(title);
        itemHolder.ISBN10.setText(ISBN10);
        itemHolder.tEndDate.setText(endDate);

        return view;
    }

    public static class ItemHolder {
        TextView tTitle;
        TextView ISBN10;
        TextView tEndDate;
    }
}