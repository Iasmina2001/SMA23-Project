package com.sma.proiect.reader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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

        listBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppState.get().setCurrentBook(books.get(i));
            }
        });

        bSubmitRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitBorrowToDB();
            }
        });

        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        String currentUserID = AppState.get().getUserID();

        databaseReference.child("bookRequests").child(currentUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    String title = null;
                    String ISBN10 = null;
                    String submittedBook = null;
                    String requestAccepted = "0";

                    if (snapshot.child("Start date").exists() && snapshot.child("End date").exists()) {
                        requestAccepted = "1";
                    }

                    if (requestAccepted.equals("0")) {

                        if (snapshot.child("Title").exists()) {
                            title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                        }

                        if (snapshot.child("Submitted").exists()) {
                            submittedBook = Objects.requireNonNull(snapshot.child("Submitted").getValue()).toString();
                        }

                        ISBN10 = snapshot.getKey();

                        if (title != null && ISBN10 != null) {
                            Book book = new Book(title, ISBN10);
                            book.setRequestStatus(submittedBook);
                            books.add(book);
                        }

                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    String title = null;
                    String ISBN10 = null;
                    String submittedBook = null;
                    String requestAccepted = "0";

                    if (snapshot.child("Start date").exists() && snapshot.child("End date").exists()) {
                        requestAccepted = "1";
                    }

                    if (requestAccepted.equals("0")) {
                        if (snapshot.child("Title").exists()) {
                            title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                        }

                        if (snapshot.child("Submitted").exists()) {
                            submittedBook = Objects.requireNonNull(snapshot.child("Submitted").getValue()).toString();
                        }

                        ISBN10 = snapshot.getKey();

                        if (title != null && ISBN10 != null) {
                            Book book = new Book(title, ISBN10);
                            removeBookWithGivenISBN10(ISBN10);
                            book.setRequestStatus(submittedBook);
                            books.add(book);
                        }

                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String title = null;
                String ISBN10 = null;
                String submittedBook = null;
                String requestAccepted = "0";

                if (snapshot.child("Start date").exists() && snapshot.child("End date").exists()) {
                    requestAccepted = "1";
                }

                if (requestAccepted.equals("0")) {

                    if (snapshot.child("Title").exists()) {
                        title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                    }

                    if (snapshot.child("Submitted").exists()) {
                        submittedBook = Objects.requireNonNull(snapshot.child("Submitted").getValue()).toString();
                    }

                    ISBN10 = snapshot.getKey();

                    if (title != null) {
                        Book book = new Book(title, ISBN10);
                        book.setRequestStatus(submittedBook);
                        books.remove(book);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * The function submits the book and writes the submission to database.
     */
    private void submitBorrowToDB() {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        String currentUserUID = AppState.get().getUserID();

        for (int i = 0; i < books.size(); i++) {
            Book currentBook = books.get(i);
            String ISBN10 = currentBook.getISBN10();
            databaseReference.child("bookRequests").child(currentUserUID).child(ISBN10).child("Submitted").setValue(1);
        }

        Toast.makeText(BookRequestsReaderActivity.this, "Your book requests have been submitted.", Toast.LENGTH_SHORT).show();
    }

    /**
     * The function deletes the not updated books from the arraylist.
     * @param ISBN10: Given ISBN10, the books that have not been submitted are deleted from the book
     *              list.
     */
    private void removeBookWithGivenISBN10(String ISBN10) {
        for (int i = 0; i < books.size(); i++) {
            Book currentBook = books.get(i);
            String currentBookISBN10 = currentBook.getISBN10();
            if (currentBookISBN10.equals(ISBN10)) {
                books.remove(currentBook);
            }
        }
    }
}