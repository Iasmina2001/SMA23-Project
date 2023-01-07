package com.sma.proiect.reader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

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


public class Fines extends AppCompatActivity {

    private List<BookRequest> bookRequests = new ArrayList<>();
    private TextView tFines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fines);

        ListView listBooks = findViewById(R.id.listBooks);
        tFines = findViewById(R.id.tFinesValue);
        final FinesAdapterActivity adapter = new FinesAdapterActivity(this, R.layout.activity_fines_adapter, bookRequests);
        listBooks.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        displayAcceptedBookRequests(adapter);
    }

    public void displayAcceptedBookRequests(FinesAdapterActivity adapter) {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        databaseReference.child("bookRequests").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    String ISBN10 = null;
                    String sTitle = null;
                    String startDate = null;
                    String endDate = null;
                    String requestStatus = null;
                    String fines = null;
                    BookRequest bookRequestObject;

                    if (snapshot.child("Fine").exists()) {
                        fines = snapshot.child("Fine").getValue().toString();
                        tFines.setText(fines);
                    } else {
                        tFines.setText(0);
                    }

                    for (DataSnapshot bookRequest : snapshot.getChildren()) {
                        ISBN10 = bookRequest.getKey();
                        sTitle = bookRequest.child("Title").getValue().toString();

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

                        if (requestStatus.equals("1")) {
                            bookRequestObject = new BookRequest(ISBN10, sTitle, endDate);
                            bookRequests.add(bookRequestObject);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    String ISBN10 = null;
                    String sTitle = null;
                    String startDate = null;
                    String endDate = null;
                    String requestStatus = null;
                    String fines = null;
                    BookRequest bookRequestObject;

                    bookRequests.clear();

                    if (snapshot.child("Fine").exists()) {
                        fines = snapshot.child("Fine").getValue().toString();
                        tFines.setText(fines);
                    } else {
                        tFines.setText(0);
                    }

                    for (DataSnapshot bookRequest : snapshot.getChildren()) {
                        ISBN10 = bookRequest.getKey();
                        sTitle = bookRequest.child("Title").getValue().toString();

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

                        if (requestStatus.equals("1")) {
                            bookRequestObject = new BookRequest(ISBN10, sTitle, endDate);
                            bookRequests.add(bookRequestObject);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    String ISBN10 = null;
                    String sTitle = null;
                    String sCurrentUserID = snapshot.getKey();
                    String startDate = null;
                    String endDate = null;
                    String requestStatus = null;
                    String fines = null;
                    BookRequest bookRequestObject;

                    bookRequests.clear();

                    if (!snapshot.child("Fine").exists()) {
                        databaseReference.child("bookRequests").child(sCurrentUserID).child("Fine").setValue(0);
                    }

                    if (snapshot.child("Fine").exists()) {
                        fines = snapshot.child("Fine").getValue().toString();
                        tFines.setText(fines);
                    } else {
                        tFines.setText(0);
                    }

                    for (DataSnapshot bookRequest : snapshot.getChildren()) {
                        ISBN10 = bookRequest.getKey();
                        sTitle = bookRequest.child("Title").getValue().toString();

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

                        if (requestStatus.equals("1")) {
                            bookRequestObject = new BookRequest(ISBN10, sTitle, endDate);
                            bookRequests.add(bookRequestObject);
                            adapter.notifyDataSetChanged();
                        }
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
}