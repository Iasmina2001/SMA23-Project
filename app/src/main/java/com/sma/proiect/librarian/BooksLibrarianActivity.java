package com.sma.proiect.librarian;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
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


public class BooksLibrarianActivity extends AppCompatActivity {

    private List<Book> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_librarian);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        ListView listPayments = findViewById(R.id.listPayments);
        Button bLogOut = findViewById(R.id.bLogOut);
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();

        final BookAdapterLibrarianActivity adapter = new BookAdapterLibrarianActivity(this, R.layout.activity_book_adapter_librarian, books);
        listPayments.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppState.get().setCurrentBook(null);
                startActivity(new Intent(getApplicationContext(), AddBookLibrarianActivity.class));
            }
        });

        listPayments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppState.get().setCurrentBook(books.get(i));
                startActivity(new Intent(getApplicationContext(), AddBookLibrarianActivity.class));
            }
        });

        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

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
                    String ISBN13 = null;

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

                    if (snapshot.child("ISBN13").exists()) {
                        ISBN13 = Objects.requireNonNull(snapshot.child("ISBN13").getValue()).toString();
                    }

                    if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && ISBN13 != null) {
                        Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, ISBN13);
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
                    String ISBN13 = null;

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

                    if (snapshot.child("ISBN13").exists()) {
                        ISBN13 = Objects.requireNonNull(snapshot.child("ISBN13").getValue()).toString();
                    }

                    if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && ISBN13 != null) {
                        Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, ISBN13);
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
                String ISBN13 = null;

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

                if (snapshot.child("ISBN13").exists()) {
                    ISBN13 = Objects.requireNonNull(snapshot.child("ISBN13").getValue()).toString();
                }

                if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && ISBN13 != null) {
                    Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, ISBN13);
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

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
}