package com.sma.proiect.helpers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import com.sma.proiect.Book;
import com.sma.proiect.BookRequest;
import com.sma.proiect.R;
import com.sma.proiect.librarian.BookAdapterLibrarianActivity;
import com.sma.proiect.librarian.BookRequestsAdapterLibrarianActivity;
import com.sma.proiect.reader.BookAdapterReaderActivity;
import com.sma.proiect.reader.BooksGridAdapterActivity;

import java.util.List;


public class SearchHelper {

    public TextWatcher addTextWatcherToBookListForReader(Context context, EditText editText, Spinner spinner, List<Book> arrayListData, List<Book> searchOptions, ListView listView) {

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String editTextValue = editText.getText().toString();
                int editTextLength = editTextValue.length();
                searchOptions.clear();
                String searchType = spinner.getSelectedItem().toString();
                if (searchType.equals("Title")) {
                    for (int j = 0; j < arrayListData.size(); j++) {
                        Book currentElement = arrayListData.get(j);
                        String currentTitle = currentElement.getTitle();
                        int currentElementLength = currentTitle.length();
                        if (editText.getText().length() <= currentElementLength) {
                            if (editTextValue.equalsIgnoreCase((String) currentTitle.subSequence(0, editTextLength))) {
                                searchOptions.add(currentElement);
                            }
                        }
                    }
                } else if (searchType.equals("ISBN")){
                    for (int j = 0; j < arrayListData.size(); j++) {
                        Book currentElement = arrayListData.get(j);
                        String currentISBN = currentElement.getISBN10();
                        int currentElementLength = currentISBN.length();
                        if (editText.getText().length() <= currentElementLength) {
                            if (editTextValue.equalsIgnoreCase((String) currentISBN.subSequence(0, editTextLength))) {
                                searchOptions.add(currentElement);
                            }
                        }
                    }
                }

                BookAdapterReaderActivity searchAdapter = new BookAdapterReaderActivity(context, R.layout.activity_book_adapter_reader, searchOptions);
                listView.setAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public TextWatcher addTextWatcherToBookListForLibrarian(Context context, EditText editText, Spinner spinner, List<Book> arrayListData, List<Book> searchOptions, ListView listView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String editTextValue = editText.getText().toString();
                int editTextLength = editTextValue.length();
                searchOptions.clear();
                String searchType = spinner.getSelectedItem().toString();
                if (searchType.equals("Title")) {
                    for (int j = 0; j < arrayListData.size(); j++) {
                        Book currentElement = arrayListData.get(j);
                        String currentTitle = currentElement.getTitle();
                        int currentElementLength = currentTitle.length();
                        if (editText.getText().length() <= currentElementLength) {
                            if (editTextValue.equalsIgnoreCase((String) currentTitle.subSequence(0, editTextLength))) {
                                searchOptions.add(currentElement);
                            }
                        }
                    }
                } else if (searchType.equals("ISBN")){
                    for (int j = 0; j < arrayListData.size(); j++) {
                        Book currentElement = arrayListData.get(j);
                        String currentISBN = currentElement.getISBN10();
                        int currentElementLength = currentISBN.length();
                        if (editText.getText().length() <= currentElementLength) {
                            if (editTextValue.equalsIgnoreCase((String) currentISBN.subSequence(0, editTextLength))) {
                                searchOptions.add(currentElement);
                            }
                        }
                    }
                }

                BookAdapterLibrarianActivity searchAdapter = new BookAdapterLibrarianActivity(context, R.layout.activity_book_adapter_librarian, searchOptions);
                listView.setAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public TextWatcher addTextWatcherToBookRequestListForLibrarian(Context context, EditText editText, Spinner spinner, List<BookRequest> arrayListData, List<BookRequest> searchOptions, ListView listView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String editTextValue = editText.getText().toString();
                int editTextLength = editTextValue.length();
                searchOptions.clear();
                String searchType = spinner.getSelectedItem().toString();
                if (searchType.equals("Title")) {
                    for (int j = 0; j < arrayListData.size(); j++) {
                        BookRequest currentElement = arrayListData.get(j);
                        String currentTitle = currentElement.getTitle();
                        int currentElementLength = currentTitle.length();
                        if (editText.getText().length() <= currentElementLength) {
                            if (editTextValue.equalsIgnoreCase((String) currentTitle.subSequence(0, editTextLength))) {
                                searchOptions.add(currentElement);
                            }
                        }
                    }
                } else if (searchType.equals("ISBN")){
                    for (int j = 0; j < arrayListData.size(); j++) {
                        BookRequest currentElement = arrayListData.get(j);
                        String currentISBN = currentElement.getISBN10();
                        int currentElementLength = currentISBN.length();
                        if (editText.getText().length() <= currentElementLength) {
                            if (editTextValue.equalsIgnoreCase((String) currentISBN.subSequence(0, editTextLength))) {
                                searchOptions.add(currentElement);
                            }
                        }
                    }
                } else if (searchType.equals("User ID")) {
                    for (int j = 0; j < arrayListData.size(); j++) {
                        BookRequest currentElement = arrayListData.get(j);
                        String currentUID = currentElement.getCurrentUID();
                        int currentElementLength = currentUID.length();
                        if (editText.getText().length() <= currentElementLength) {
                            if (editTextValue.equalsIgnoreCase((String) currentUID.subSequence(0, editTextLength))) {
                                searchOptions.add(currentElement);
                            }
                        }
                    }
                }

                BookRequestsAdapterLibrarianActivity searchAdapter = new BookRequestsAdapterLibrarianActivity(context, R.layout.activity_book_requests_adapter_librarian, searchOptions);
                listView.setAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public TextWatcher addTextWatcherToGridView(Context context, EditText editText, List<Book> arrayListData, List<Book> searchOptions, GridView gridView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String editTextValue = editText.getText().toString();
                int editTextLength = editTextValue.length();
                searchOptions.clear();
                for (int j = 0; j < arrayListData.size(); j++) {
                    Book currentElement = arrayListData.get(j);
                    String currentTitle = currentElement.getTitle();
                    int currentElementLength = currentTitle.length();
                    if (editText.getText().length() <= currentElementLength) {
                        if (editTextValue.equalsIgnoreCase((String) currentTitle.subSequence(0, editTextLength))) {
                            searchOptions.add(currentElement);
                        }
                    }
                }

                BooksGridAdapterActivity searchAdapter = new BooksGridAdapterActivity(context, R.layout.activity_books_grid_adapter, searchOptions);
                gridView.setAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }
}
