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


public class BorrowedEBooksActivity extends AppCompatActivity {

    List<Book> eBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowed_ebooks);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        TextView message = findViewById(R.id.tMessage);
        GridView gridView = findViewById(R.id.gridView);

        BorrowedEBookGridAdapter gridAdapter = new BorrowedEBookGridAdapter(BorrowedEBooksActivity.this, R.layout.activity_borrowed_ebook_grid_adapter, eBooks, BorrowedEBooksActivity.class.getName());
        gridView.setAdapter(gridAdapter);

        AdapterViewHelper adapterViewHelper = new AdapterViewHelper();
        BroadcastNotificationManager broadcastNotificationManagerForFunction = new BroadcastNotificationManager();
        NavigationViewHelper navigationViewHelper = new NavigationViewHelper();
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        broadcastNotificationManagerForFunction.createNotificationChannel(BorrowedEBooksActivity.this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        gridView.setOnItemClickListener(
                adapterViewHelper.addListenerToGridviewForReader(
                        BorrowedEBooksActivity.this
                )
        );

        navigationView.setNavigationItemSelectedListener(
                navigationViewHelper.getNavViewForReader(
                        BorrowedEBooksActivity.this,
                        drawerLayout
                )
        );

        if (!AppState.get().isAccountBlocked()) {
            databaseOperationHelper.displayBorrowedEBooks(gridAdapter, eBooks);
        }
    }
}