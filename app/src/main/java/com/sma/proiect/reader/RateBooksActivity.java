package com.sma.proiect.reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.BroadcastNotificationManager;
import com.sma.proiect.R;
import com.sma.proiect.helpers.AdapterViewHelper;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import com.sma.proiect.helpers.NavigationViewHelper;
import com.sma.proiect.helpers.SearchHelper;
import java.util.ArrayList;
import java.util.List;


public class RateBooksActivity extends AppCompatActivity {

    List<Book> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_books);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        TextView message = findViewById(R.id.tMessage);
        GridView gridView = findViewById(R.id.gridView);
        CardView cardViewReadBooks = findViewById(R.id.cReadBooks);
        CardView cardViewCurrentlyReadingBooks = findViewById(R.id.cCurrentlyReadingBooks);
        EditText eSearchBar = findViewById(R.id.eSearchBar);

        BooksGridAdapterActivity booksGridAdapter = new BooksGridAdapterActivity(RateBooksActivity.this, R.layout.activity_books_grid_adapter, books);
        gridView.setAdapter(booksGridAdapter);

        SearchHelper searchHelper = new SearchHelper();
        AdapterViewHelper adapterViewHelper = new AdapterViewHelper();
        BroadcastNotificationManager broadcastNotificationManagerForFunction = new BroadcastNotificationManager();
        NavigationViewHelper navigationViewHelper = new NavigationViewHelper();
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        broadcastNotificationManagerForFunction.createNotificationChannel(RateBooksActivity.this);

        List<Book> tempBooks = new ArrayList<>();
        eSearchBar.addTextChangedListener(
                searchHelper.addTextWatcherToGridView(
                        RateBooksActivity.this,
                        eSearchBar,
                        books,
                        tempBooks,
                        gridView
                )
        );

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        gridView.setOnItemClickListener(
                adapterViewHelper.addListenerToGridviewForReader(
                        RateBooksActivity.this
                )
        );

        navigationView.setNavigationItemSelectedListener(
                navigationViewHelper.getNavViewForReader(
                        RateBooksActivity.this,
                        drawerLayout
                )
        );

        cardViewReadBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RateBooksActivity.this, ReadBooksActivity.class);
                startActivity(intent);
            }
        });

        cardViewCurrentlyReadingBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RateBooksActivity.this, CurrentlyReadingBooksActivity.class);
                startActivity(intent);
            }
        });

        if (!AppState.get().isAccountBlocked()) {
            databaseOperationHelper.loadBooks(booksGridAdapter, books, "eBooks");
            databaseOperationHelper.loadBooks(booksGridAdapter, books, "books");
        }
    }
}