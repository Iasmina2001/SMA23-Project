package com.sma.proiect.librarian;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.BookType;
import com.sma.proiect.R;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class AddBookLibrarianActivity extends AppCompatActivity {

    private Book currentBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_librarian);

        EditText eTitle = findViewById(R.id.eTitleValue);
        EditText eAuthor = findViewById(R.id.eAuthorValue);
        EditText ePublisher = findViewById(R.id.ePublisherValue);
        EditText ePublicationDate = findViewById(R.id.ePublicationDateValue);
        Spinner sGenre = findViewById(R.id.sGenreValue);
        EditText eISBN10 = findViewById(R.id.eISBN10Value);
        EditText eNumOfBooks = findViewById(R.id.ePiecesValue);
        Button bSave = findViewById(R.id.bSave);
        Button bDelete = findViewById(R.id.bDelete);

        String[] genres = BookType.getBookGenres();
        ArrayAdapter<String> sAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, genres);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sGenre.setAdapter(sAdapter);

        currentBook = AppState.get().getCurrentBook();

        if (currentBook != null) {
            eTitle.setText(currentBook.getTitle());
            eAuthor.setText(currentBook.getAuthor());
            ePublisher.setText(currentBook.getPublisher());
            ePublicationDate.setText(currentBook.getPublicationDate());
            eISBN10.setText(currentBook.getISBN10());
            eNumOfBooks.setText(currentBook.getNumOfBooks());
            try {
                sGenre.setSelection(Arrays.asList(genres).indexOf(currentBook.getGenre()));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception: " + e, Toast.LENGTH_SHORT).show();
            }
        }

        bSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                currentBook = AppState.get().getCurrentBook();

                String title = eTitle.getText().toString();
                String author = eAuthor.getText().toString();
                String publisher = ePublisher.getText().toString();
                String publicationDate = ePublicationDate.getText().toString();
                String genre = sGenre.getSelectedItem().toString();
                String ISBN10 = eISBN10.getText().toString();
                String numOfBooks = eNumOfBooks.getText().toString();

                save(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks);
            }
        });

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentBook = AppState.get().getCurrentBook();
                if (currentBook != null) {
                    delete(currentBook.getISBN10());
                } else {
                    Toast.makeText(getApplicationContext(), "Book does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void delete(String ISBN10) {
        AppState.get().getDatabaseReference().child("books").child(ISBN10).removeValue();
    }

    private void save(String title, String author, String publisher, String publicationDate, String genre, String ISBN10, String numOfBooks) {
        Map<String, Object> map = new HashMap<>();
        map.put("Title", title);
        map.put("Author", author);
        map.put("Publisher", publisher);
        map.put("Publication date", publicationDate);
        map.put("Genre", genre);
        map.put("Number of books", numOfBooks);
        AppState.get().getDatabaseReference().child("books").child(ISBN10).updateChildren(map);
    }
}