package com.sma.proiect.librarian;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.BookType;
import com.sma.proiect.R;
import com.sma.proiect.helpers.ActivityForResultHelper;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import com.sma.proiect.helpers.StorageOperationHelper;
import java.util.Arrays;


public class AddBookLibrarianActivity extends AppCompatActivity {

    private Book currentBook;
    ActivityResultLauncher<Intent> mStartBrowseEBookFileForResult;    // register callback
    ActivityResultLauncher<Intent> mStartBrowseEBookCoverForResult;    // register callback
    private StorageOperationHelper storageOperationHelper = new StorageOperationHelper();
    private DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_librarian);

        EditText eTitle = findViewById(R.id.eTitleValue);
        EditText eAuthor = findViewById(R.id.eAuthorValue);
        EditText ePublisher = findViewById(R.id.ePublisherValue);
        EditText ePublicationDate = findViewById(R.id.ePublicationDateValue);
        Spinner sGenre = findViewById(R.id.sGenreValue);
        Spinner sType = findViewById(R.id.sTypeValue);
        EditText eISBN10 = findViewById(R.id.eISBN10Value);
        EditText eNumOfBooks = findViewById(R.id.ePiecesValue);
        TextView tEBookFilePath = findViewById(R.id.tEBookPath);
        Button bBrowseEBook = findViewById(R.id.bBrowseEBook);
        Button bBrowseEBookCover = findViewById(R.id.bBrowseEBookCover);
        Button bSave = findViewById(R.id.bSave);
        Button bDelete = findViewById(R.id.bDelete);
        Button bClearEBookFile = findViewById(R.id.bClearEBookFile);
        ImageView iEBookCover = findViewById(R.id.iEBookCover);

        String[] genres = BookType.getBookGenres();
        ArrayAdapter<String> sAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, genres);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sGenre.setAdapter(sAdapter);

        String[] types = BookType.getBookTypes();
        ArrayAdapter<String> sTypeAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, types);
        sTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sType.setAdapter(sTypeAdapter);

        ActivityForResultHelper activityForResultHelper = new ActivityForResultHelper(AddBookLibrarianActivity.this);

        mStartBrowseEBookFileForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                activityForResultHelper.setBrowsedEBookPath(tEBookFilePath)
        );

        mStartBrowseEBookCoverForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                activityForResultHelper.setBrowsedEBookCover(AddBookLibrarianActivity.this, iEBookCover)
        );

        currentBook = AppState.get().getCurrentBook();

        if (currentBook != null) {
            String ISBN10 = currentBook.getISBN10();

            eTitle.setText(currentBook.getTitle());
            eAuthor.setText(currentBook.getAuthor());
            ePublisher.setText(currentBook.getPublisher());
            ePublicationDate.setText(currentBook.getPublicationDate());
            eISBN10.setText(ISBN10);
            eNumOfBooks.setText(currentBook.getNumOfBooks());
            String sBookGenre = currentBook.getGenre();
            Book bookForFunction = new Book();
            String sBookType = bookForFunction.getBookTypeAccordingToRealtimeDatabasePath(currentBook.getRealtimeDatabasePath());

            if (sBookType.equals("eBook")) {
                databaseOperationHelper.setDownloadedFileLinkLibrarian(ISBN10, tEBookFilePath);
            }
            databaseOperationHelper.setDownloadedBookCoverLinkLibrarian(AddBookLibrarianActivity.this, iEBookCover);

            try {
                // set preselected genre
                sGenre.setSelection(Arrays.asList(genres).indexOf(sBookGenre));
                // set preselected book type
                sType.setSelection(Arrays.asList(types).indexOf(sBookType));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception: " + e, Toast.LENGTH_SHORT).show();
            }
        }

        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();

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
                String type = sType.getSelectedItem().toString();
                String eBookFilePath = tEBookFilePath.getText().toString();

                databaseOperationHelper.saveBook(
                        AddBookLibrarianActivity.this,
                        title,
                        author,
                        publisher,
                        publicationDate,
                        genre,
                        ISBN10,
                        numOfBooks,
                        type,
                        eBookFilePath,
                        tEBookFilePath,
                        iEBookCover
                );
            }
        });

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentBook = AppState.get().getCurrentBook();
                String databasePath = currentBook.getRealtimeDatabasePath();
                if (currentBook != null) {
                    databaseOperationHelper.deleteBook(AddBookLibrarianActivity.this, currentBook.getISBN10(), databasePath);
                    finish();
                } else {
                    Toast.makeText(AddBookLibrarianActivity.this, "Book does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bClearEBookFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tEBookFilePath.setText("");
            }
        });

        bBrowseEBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseBook();
            }
        });

        bBrowseEBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { browseCover(); }
        });
    }

    public void browseBook() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // intent.setType("application/pdf|application/epub+zip");
        intent.setType("application/*");
        mStartBrowseEBookFileForResult.launch(Intent.createChooser(intent, "Choose eBook"));
    }

    public void browseCover() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // intent.setType("application/pdf|application/epub+zip");
        intent.setType("image/*");
        mStartBrowseEBookCoverForResult.launch(Intent.createChooser(intent, "Choose cover"));
    }
}