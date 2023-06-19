package com.sma.proiect.reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
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
import java.util.ArrayList;
import java.util.List;


public class CurrentlyReadingBooksActivity extends AppCompatActivity {

    List<Book> books = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currently_reading_books);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        TextView message = findViewById(R.id.tMessage);
        GridView gridView = findViewById(R.id.gridView);
        TextView tBooksCurrentlyReading = findViewById(R.id.tBooksCurrentlyReading);

        BooksGridAdapterActivity booksGridAdapter = new BooksGridAdapterActivity(CurrentlyReadingBooksActivity.this, R.layout.activity_books_grid_adapter, books);
        gridView.setAdapter(booksGridAdapter);

        AdapterViewHelper adapterViewHelper = new AdapterViewHelper();
        BroadcastNotificationManager broadcastNotificationManagerForFunction = new BroadcastNotificationManager();
        NavigationViewHelper navigationViewHelper = new NavigationViewHelper();
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        broadcastNotificationManagerForFunction.createNotificationChannel(CurrentlyReadingBooksActivity.this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        gridView.setOnItemClickListener(
                adapterViewHelper.addListenerToGridviewForReader(
                        CurrentlyReadingBooksActivity.this
                )
        );

        navigationView.setNavigationItemSelectedListener(
                navigationViewHelper.getNavViewForReader(
                        CurrentlyReadingBooksActivity.this,
                        drawerLayout
                )
        );

        if (!AppState.get().isAccountBlocked()) {
            databaseOperationHelper.displayCurrentlyReadingBooks(booksGridAdapter, books, "books");
            databaseOperationHelper.displayCurrentlyReadingBooks(booksGridAdapter, books, "eBooks");
        }
    }
}