package com.sma.proiect.librarian;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import com.google.firebase.database.DatabaseReference;
import com.sma.proiect.AppState;
import com.sma.proiect.BookRequest;
import com.sma.proiect.R;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import com.sma.proiect.helpers.SearchHelper;
import java.util.ArrayList;
import java.util.List;


public class BookRequestsLibrarianActivity extends AppCompatActivity {

    private List<BookRequest> bookRequests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_requests_librarian);

        ListView lBookRequests = findViewById(R.id.listBooks);
        Spinner sSearchSpinner = findViewById(R.id.sSearchSpinner);
        EditText eSearch = findViewById(R.id.eSearch);

        SearchHelper searchHelper = new SearchHelper();
        List<BookRequest> tempBookRequests = new ArrayList<>();
        ArrayAdapter<String> searchTypeAdapter = new ArrayAdapter<>(BookRequestsLibrarianActivity.this, android.R.layout.simple_spinner_item, new String[]{"Title", "ISBN", "User ID", "Unreturned books"});
        searchTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSearchSpinner.setAdapter(searchTypeAdapter);

        final BookRequestsAdapterLibrarianActivity adapter = new BookRequestsAdapterLibrarianActivity(this, R.layout.activity_book_requests_adapter_librarian, bookRequests);
        lBookRequests.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        lBookRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppState.get().setCurrentBookRequest(bookRequests.get(i));
            }
        });

        eSearch.addTextChangedListener(
                searchHelper.addTextWatcherToBookRequestListForLibrarian(
                        BookRequestsLibrarianActivity.this,
                        eSearch,
                        sSearchSpinner,
                        bookRequests,
                        tempBookRequests,
                        lBookRequests
                )
        );

        sSearchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                if (selectedItem.equals("Unreturned books")) {
                    tempBookRequests.clear();
                    for (int j = 0; j < bookRequests.size(); j++) {
                        BookRequest currentElement = bookRequests.get(j);
                        String sCurrentFine = currentElement.getFine();
                        long lCurrentFine = 0;
                        try {
                            lCurrentFine = Long.parseLong(sCurrentFine);
                        } catch (NumberFormatException nfe) {
                            // :)
                        }
                        if (lCurrentFine > 0) {
                            tempBookRequests.add(currentElement);
                        }
                    }
                    BookRequestsAdapterLibrarianActivity searchAdapter = new BookRequestsAdapterLibrarianActivity(BookRequestsLibrarianActivity.this, R.layout.activity_book_requests_adapter_librarian, tempBookRequests);
                    lBookRequests.setAdapter(searchAdapter);
                    searchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();

        // check user accounts if there are overdue book requests
        // if there are overdue book requests, block the account and calculate the fine
        databaseOperationHelper.checkFineAndAccessForUsers();

        // after checking the accounts, display the book requests
        databaseReference.child("bookRequests").addChildEventListener(
                databaseOperationHelper.displayBookRequests(
                        databaseReference,
                        bookRequests,
                        adapter
                )
        );
    }
}