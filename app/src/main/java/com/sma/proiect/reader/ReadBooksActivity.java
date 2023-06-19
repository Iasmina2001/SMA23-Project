package com.sma.proiect.reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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


public class ReadBooksActivity extends AppCompatActivity {

    List<Book> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_books);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        TextView message = findViewById(R.id.tMessage);
        GridView gridView = findViewById(R.id.gridView);

        BookRatingGridAdapterActivity bookRatingGridAdapter = new BookRatingGridAdapterActivity(ReadBooksActivity.this, R.layout.activity_book_rating_grid_adapter, books);
        gridView.setAdapter(bookRatingGridAdapter);

        AdapterViewHelper adapterViewHelper = new AdapterViewHelper();
        BroadcastNotificationManager broadcastNotificationManagerForFunction = new BroadcastNotificationManager();
        NavigationViewHelper navigationViewHelper = new NavigationViewHelper();
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        broadcastNotificationManagerForFunction.createNotificationChannel(ReadBooksActivity.this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        gridView.setOnItemClickListener(
                adapterViewHelper.addListenerToGridviewForReader(
                        ReadBooksActivity.this
                )
        );

        navigationView.setNavigationItemSelectedListener(
                navigationViewHelper.getNavViewForReader(
                        ReadBooksActivity.this,
                        drawerLayout
                )
        );

        if (!AppState.get().isAccountBlocked()) {
            databaseOperationHelper.displayBookRatingsForUser(bookRatingGridAdapter, books, "eBooks");
            databaseOperationHelper.displayBookRatingsForUser(bookRatingGridAdapter, books, "books");
        }
    }
}