package com.sma.proiect.helpers;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import com.sma.proiect.librarian.BooksLibrarianActivity;
import com.sma.proiect.librarian.EBooksLibrarianActivity;
import com.sma.proiect.reader.RateBooksActivity;
import com.sma.proiect.reader.ReadingChallengeActivity;
import com.sma.proiect.reader.SearchBookReaderActivity;
import com.sma.proiect.reader.SearchEBookReaderActivity;


public class AdapterViewHelper {

    public AdapterView.OnItemClickListener addListenerToGridviewForReader(Context context) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                switch (i) {
                    case 0:
                        // borrow books
                        intent = new Intent(context, SearchBookReaderActivity.class);
                        context.startActivity(intent);
                        break;
                    case 1:
                        // borrow eBooks
                        intent = new Intent(context, SearchEBookReaderActivity.class);
                        context.startActivity(intent);
                        break;
                    case 2:
                        // rate books
                        intent = new Intent(context, RateBooksActivity.class);
                        context.startActivity(intent);
                        break;
                    case 3:
                        // reading challenge
                        intent = new Intent(context, ReadingChallengeActivity.class);
                        context.startActivity(intent);
                        break;
                }
            }
        };
    }

    public AdapterView.OnItemClickListener addListenerToGridviewForLibrarian(Context context) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                switch (i) {
                    case 0:
                        // search books
                        intent = new Intent(context, BooksLibrarianActivity.class);
                        context.startActivity(intent);
                        break;
                    case 1:
                        // search eBooks
                        intent = new Intent(context, EBooksLibrarianActivity.class);
                        context.startActivity(intent);
                        break;
                }
            }
        };
    }
}