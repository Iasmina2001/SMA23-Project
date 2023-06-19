package com.sma.proiect.reader;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.google.firebase.database.DatabaseReference;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.R;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import java.util.ArrayList;
import java.util.List;


public class BookRequestsReaderActivity extends AppCompatActivity {

    private List<Book> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_requests);

        ListView listBooks = findViewById(R.id.listBooks);
        Button bSubmitRequests = findViewById(R.id.bSubmitRequests);

        final BookRequestsAdapterActivity adapter = new BookRequestsAdapterActivity(this, R.layout.activity_book_requests_adapter, books);
        listBooks.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        String currentUserID = AppState.get().getUserID();

        listBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppState.get().setCurrentBook(books.get(i));
            }
        });

        bSubmitRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseOperationHelper.submitRequestToDB(BookRequestsReaderActivity.this, books);
            }
        });

        databaseReference.child("bookRequests").child(currentUserID).addChildEventListener(
                databaseOperationHelper.displayIfRequestsSubmitted(adapter, books)
        );
    }
}