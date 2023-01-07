package com.sma.proiect.reader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.BookRequest;
import com.sma.proiect.BroadcastNotificationManager;
import com.sma.proiect.FirebaseBlockAccountCallback;
import com.sma.proiect.FirebaseCallback;
import com.sma.proiect.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class SearchBookReaderActivity extends AppCompatActivity {

    private List<Book> books = new ArrayList<>();
    private List<BookRequest> bookRequests = new ArrayList<>();
    private static final String TAG_NEWS = "APPROVED_REQUEST";
    private static final String TAG_CHILL = "NOT_APPROVED_REQUEST";
    private ImageView iNotification;
    private ImageView iAccountID;
    private ListView listBooks;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book_reader);

        listBooks = findViewById(R.id.listBooks);
        Button yourBookRequests = findViewById(R.id.bRequests);
        Button yourFines = findViewById(R.id.bFines);
        Button bLogOut = findViewById(R.id.bLogOut);
        message = findViewById(R.id.tStatus);
        iAccountID = findViewById(R.id.iAccountID);
        iNotification = findViewById(R.id.iNotification);
        iNotification.setTag(TAG_CHILL);

        final BookAdapterReaderActivity adapter = new BookAdapterReaderActivity(this, R.layout.activity_book_adapter_reader, books);
        listBooks.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        createNotificationChannel();

        yourBookRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchBookReaderActivity.this, BookRequestsReaderActivity.class));
            }
        });

        yourFines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchBookReaderActivity.this, Fines.class));
            }
        });

        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        listBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppState.get().setCurrentBook(books.get(i));
            }
        });

        waitDatabaseToLoad(new FirebaseBlockAccountCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCallback(boolean blockAccount) {
                AppState.get().blockAccount(blockAccount);
                if (!blockAccount) {
                    loadBooks(adapter);
                } else {
                    message.setText("Unfortunately your account is blocked, because you didn't return your books in time to the librarian.\n\nReturn the books and pay the fine. ");
                }
            }
        });

        iAccountID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchBookReaderActivity.this);
                String currentUserUID = AppState.get().getUserID();
                String message = "Your account user ID is:\n" + currentUserUID;

                builder.setCancelable(true);
                builder.setMessage(message);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        iNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchBookReaderActivity.this);

                String greeting = "";
                if (String.valueOf(iNotification.getTag()).equals(TAG_CHILL)) {
                    greeting = "You currently don't have approved book requests. :'(";
                } else if (String.valueOf(iNotification.getTag()).equals(TAG_NEWS)) {
                    greeting = "You have approved book requests. :D Check them in your fines tab.";
                }

                builder.setCancelable(true);
                builder.setMessage(greeting);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // message to display
                        int duration = Toast.LENGTH_SHORT;
                        String text = "I understood! XD";
                        Toast toast = Toast.makeText(SearchBookReaderActivity.this, text, duration);
                        // to show the toast
                        toast.show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    public void loadBooks(BookAdapterReaderActivity adapter) {
        if (!AppState.get().isAccountBlocked()) {
            DatabaseReference databaseReference = AppState.get().getDatabaseReference();
            databaseReference.child("books").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    try {
                        String title = null;
                        String author = null;
                        String publisher = null;
                        String publicationDate = null;
                        String genre = null;
                        String ISBN10 = null;
                        String numOfBooks = null;

                        if (snapshot.child("Title").exists()) {
                            title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                        }

                        if (snapshot.child("Author").exists()) {
                            author = Objects.requireNonNull(snapshot.child("Author").getValue()).toString();
                        }

                        if (snapshot.child("Publisher").exists()) {
                            publisher = Objects.requireNonNull(snapshot.child("Publisher").getValue()).toString();
                        }

                        if (snapshot.child("Publication date").exists()) {
                            publicationDate = Objects.requireNonNull(snapshot.child("Publication date").getValue()).toString();
                        }

                        if (snapshot.child("Genre").exists()) {
                            genre = Objects.requireNonNull(snapshot.child("Genre").getValue()).toString();
                        }

                        ISBN10 = snapshot.getKey();

                        if (snapshot.child("Number of books").exists()) {
                            numOfBooks = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                        }

                        if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && numOfBooks != null) {
                            Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks);
                            books.add(book);
                        }

                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        // :)
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    try {
                        String title = null;
                        String author = null;
                        String publisher = null;
                        String publicationDate = null;
                        String genre = null;
                        String ISBN10 = null;
                        String numOfBooks = null;

                        if (snapshot.child("Title").exists()) {
                            title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                        }

                        if (snapshot.child("Author").exists()) {
                            author = Objects.requireNonNull(snapshot.child("Author").getValue()).toString();
                        }

                        if (snapshot.child("Publisher").exists()) {
                            publisher = Objects.requireNonNull(snapshot.child("Publisher").getValue()).toString();
                        }

                        if (snapshot.child("Publication date").exists()) {
                            publicationDate = Objects.requireNonNull(snapshot.child("Publication date").getValue()).toString();
                        }

                        if (snapshot.child("Genre").exists()) {
                            genre = Objects.requireNonNull(snapshot.child("Genre").getValue()).toString();
                        }

                        ISBN10 = snapshot.getKey();

                        if (snapshot.child("Number of books").exists()) {
                            numOfBooks = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                        }

                        if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && numOfBooks != null) {
                            Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks);
                            books.remove(AppState.get().getCurrentBook());
                            books.add(book);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        // :D
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    String title = null;
                    String author = null;
                    String publisher = null;
                    String publicationDate = null;
                    String genre = null;
                    String ISBN10 = null;
                    String numOfBooks = null;

                    if (snapshot.child("Title").exists()) {
                        title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                    }

                    if (snapshot.child("Author").exists()) {
                        author = Objects.requireNonNull(snapshot.child("Author").getValue()).toString();
                    }

                    if (snapshot.child("Publisher").exists()) {
                        publisher = Objects.requireNonNull(snapshot.child("Publisher").getValue()).toString();
                    }

                    if (snapshot.child("Publication date").exists()) {
                        publicationDate = Objects.requireNonNull(snapshot.child("Publication date").getValue()).toString();
                    }

                    if (snapshot.child("Genre").exists()) {
                        genre = Objects.requireNonNull(snapshot.child("Genre").getValue()).toString();
                    }

                    ISBN10 = snapshot.getKey();

                    if (snapshot.child("Number of books").exists()) {
                        numOfBooks = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                    }

                    if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && numOfBooks != null) {
                        Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks);
                        books.remove(book);
                    }
                    adapter.notifyDataSetChanged();
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
    }

    public void waitDatabaseToLoad(FirebaseBlockAccountCallback firebaseCallback) {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        String currentUserUID = AppState.get().getUserID();
        // checks if reader has late book returns
        // checks if book requests have been approved
        databaseReference.child(String.format("bookRequests/%s", currentUserUID)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean blockAccount;
                for (DataSnapshot bookRequest: dataSnapshot.getChildren()) {
                    if (bookRequest.child("End date").exists()) {
                        String endDate = Objects.requireNonNull(bookRequest.child("End date").getValue()).toString();
                        if (remindBookRequest(endDate, 1)) {
                            Intent intent = new Intent(SearchBookReaderActivity.this, BroadcastNotificationManager.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(SearchBookReaderActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                        }
                        if (isBookRequestLate(endDate)) {
                            // checks if reader didn't return books on time
                            databaseReference.child("bookRequests").child(currentUserUID).child("Forbid access").setValue(1);
                            databaseReference.child("bookRequests").child(currentUserUID).child("Fine").setValue(50);
                            break;
                        } else {
                            // if the end date didn't expire, the notification bell if turned on
                            // to show the user that his book requests have been submitted
                            iNotification.setImageResource(R.drawable.bell_nofification);
                            iNotification.setTag(TAG_NEWS);
                        }
                    }
                }
                // checks if reader account is blocked
                if (dataSnapshot.child("Forbid access").exists()) {
                    String forbidAccess = Objects.requireNonNull(dataSnapshot.child("Forbid access").getValue()).toString();
                    blockAccount = forbidAccess.equals("1");
                } else {
                    blockAccount = false;
                }
                firebaseCallback.onCallback(blockAccount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public boolean isBookRequestLate(String sEndDate) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.mm.yyyy", Locale.getDefault());
        String sCurrentDate = formatter.format(new Date(cal.getTimeInMillis()));
        Date endDate, currentDate;
        try {
            endDate = formatter.parse(sEndDate);
            currentDate = formatter.parse(sCurrentDate);
            return (currentDate.getTime() - endDate.getTime()) > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Function reminds the user to return the book nDays before the end date expires, by displaying
     * a notification.
     * @param sEndDate: end date when the book lend expires
     * @return: returns boolean, which indicates whether the remind notification should display
     */
    public boolean remindBookRequest(String sEndDate, int nDays) {
        String dateFormat = "dd.MM.yyyy";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());

        String sRemindDate;
        cal.add(Calendar.DAY_OF_YEAR, -nDays);

        String sCurrentDate = formatter.format(new Date(cal.getTimeInMillis()));
        Date currentDate, remindDate;
        try {
            sRemindDate = formatter.format(new Date(Objects.requireNonNull(formatter.parse(sEndDate)).getTime()));
            remindDate = formatter.parse(sRemindDate);
            currentDate = formatter.parse(sCurrentDate);
            return (remindDate.getTime() - currentDate.getTime()) > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("bookRequestReminder", "my channel name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            channel.setSound(null, null);
            ((NotificationManager) getSystemService(NotificationManager.class)).createNotificationChannel(channel);
        }
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        AppState.get().setCurrentUser(null);
        finish();
    }
}