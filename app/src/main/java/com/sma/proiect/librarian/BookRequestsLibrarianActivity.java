package com.sma.proiect.librarian;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.sma.proiect.AppState;
import com.sma.proiect.BookRequest;
import com.sma.proiect.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BookRequestsLibrarianActivity extends AppCompatActivity {

    private List<BookRequest> bookRequests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_requests_librarian);

        ListView lBookRequests = findViewById(R.id.listBooks);

        final BookRequestsAdapterLibrarianActivity adapter = new BookRequestsAdapterLibrarianActivity(this, R.layout.activity_book_requests_adapter_librarian, bookRequests);
        lBookRequests.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        lBookRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppState.get().setCurrentBookRequest(bookRequests.get(i));
            }
        });

        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        databaseReference.child("bookRequests").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    String ISBN10 = null;
                    String sSubmitted = null;
                    String sFine;
                    String sTitle = null;
                    String sCurrentUserID = snapshot.getKey();
                    String startDate = null;
                    String endDate = null;
                    String requestStatus;
                    BookRequest bookRequestObject;

                    if (!snapshot.child("Fine").exists()) {
                        databaseReference.child("bookRequests").child(sCurrentUserID).child("Fine").setValue(0);
                    }

                    sFine = Objects.requireNonNull(snapshot.child("Fine").getValue()).toString();

                    for (DataSnapshot bookRequest: snapshot.getChildren()) {
                        ISBN10 = bookRequest.getKey();
                        sTitle = bookRequest.child("Title").getValue().toString();
                        sSubmitted = bookRequest.child("Submitted").getValue().toString();

                        if (bookRequest.child("Start date").exists()) {
                            startDate = Objects.requireNonNull(bookRequest.child("Start date").getValue()).toString();
                        }

                        if (bookRequest.child("End date").exists()) {
                            endDate = Objects.requireNonNull(bookRequest.child("End date").getValue()).toString();
                        }

                        if (startDate != null && endDate != null) {
                            requestStatus = "1";
                        } else {
                            requestStatus = "0";
                        }

                        bookRequestObject = new BookRequest(sFine, ISBN10, sTitle, sCurrentUserID, sSubmitted, requestStatus);
                        bookRequests.add(bookRequestObject);
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    String ISBN10 = null;
                    String sSubmitted = null;
                    String sFine;
                    String sTitle = null;
                    String sCurrentUserID = snapshot.getKey();
                    String startDate = null;
                    String endDate = null;
                    String requestStatus;
                    BookRequest bookRequestObject;

                    bookRequests.clear();

                    if (!snapshot.child("Fine").exists()) {
                        databaseReference.child("bookRequests").child(sCurrentUserID).child("Fine").setValue(0);
                    }

                    sFine = Objects.requireNonNull(snapshot.child("Fine").getValue()).toString();

                    for (DataSnapshot bookRequest: snapshot.getChildren()) {
                        ISBN10 = bookRequest.getKey();
                        sTitle = bookRequest.child("Title").getValue().toString();
                        sSubmitted = bookRequest.child("Submitted").getValue().toString();

                        startDate = null;
                        endDate = null;
                        if (bookRequest.child("Start date").exists()) {
                            startDate = Objects.requireNonNull(bookRequest.child("Start date").getValue()).toString();
                        }

                        if (bookRequest.child("End date").exists()) {
                            endDate = Objects.requireNonNull(bookRequest.child("End date").getValue()).toString();
                        }

                        if (startDate != null && endDate != null) {
                            requestStatus = "1";
                        } else {
                            requestStatus = "0";
                        }

                        bookRequestObject = new BookRequest(sFine, ISBN10, sTitle, sCurrentUserID, sSubmitted, requestStatus);
                        bookRequests.add(bookRequestObject);
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    String ISBN10 = null;
                    String sSubmitted = null;
                    String sFine;
                    String sTitle = null;
                    String sCurrentUserID = snapshot.getKey();
                    String startDate = null;
                    String endDate = null;
                    String requestStatus;
                    BookRequest bookRequestObject;

                    bookRequests.clear();

                    if (!snapshot.child("Fine").exists()) {
                        databaseReference.child("bookRequests").child(sCurrentUserID).child("Fine").setValue(0);
                    }

                    sFine = Objects.requireNonNull(snapshot.child("Fine").getValue()).toString();

                    for (DataSnapshot bookRequest: snapshot.getChildren()) {
                        ISBN10 = bookRequest.getKey();
                        sTitle = bookRequest.child("Title").getValue().toString();
                        sSubmitted = bookRequest.child("Submitted").getValue().toString();

                        startDate = null;
                        endDate = null;
                        if (bookRequest.child("Start date").exists()) {
                            startDate = Objects.requireNonNull(bookRequest.child("Start date").getValue()).toString();
                        }

                        if (bookRequest.child("End date").exists()) {
                            endDate = Objects.requireNonNull(bookRequest.child("End date").getValue()).toString();
                        }

                        if (startDate != null && endDate != null) {
                            requestStatus = "1";
                        } else {
                            requestStatus = "0";
                        }

                        bookRequestObject = new BookRequest(sFine, ISBN10, sTitle, sCurrentUserID, sSubmitted, requestStatus);
                        bookRequests.add(bookRequestObject);
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * The function deletes the not updated book requests from the arraylist.
     * @param ISBN10: Given ISBN10, the book requests that have not been submitted are deleted from
     *              the book list.
     */
    private void removeBookRequestWithGivenISBN10(String ISBN10) {
        for (int i = 0; i < bookRequests.size(); i++) {
            BookRequest currentBook = bookRequests.get(i);
            String currentBookISBN10 = currentBook.getISBN10();
            if (currentBookISBN10.equals(ISBN10)) {
                bookRequests.remove(currentBook);
                break;
            }
        }
    }
}