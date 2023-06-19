package com.sma.proiect.reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.BroadcastNotificationManager;
import com.sma.proiect.R;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import com.sma.proiect.helpers.NavigationViewHelper;
import com.sma.proiect.helpers.SearchHelper;
import java.util.ArrayList;
import java.util.List;


public class SearchEBookReaderActivity extends AppCompatActivity {

    private List<Book> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ebook_reader);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        ListView listBooks = findViewById(R.id.listBooks);
        EditText eSearch = findViewById(R.id.eSearch);
        Spinner sSearchSpinner = findViewById(R.id.sSearchSpinner);
        TextView message = findViewById(R.id.tMessage);

        BroadcastNotificationManager broadcastNotificationManagerForFunction = new BroadcastNotificationManager();
        NavigationViewHelper navigationViewHelper = new NavigationViewHelper();
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        SearchHelper searchHelper = new SearchHelper();
        final EBookAdapterReaderActivity adapter = new EBookAdapterReaderActivity(this, R.layout.activity_ebook_adapter_reader, books);
        listBooks.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        broadcastNotificationManagerForFunction.createNotificationChannel(SearchEBookReaderActivity.this);

        listBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppState.get().setCurrentBook(books.get(i));
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(
                navigationViewHelper.getNavViewForReader(
                        SearchEBookReaderActivity.this,
                        drawerLayout
                )
        );

        List<Book> tempBooks = new ArrayList<>();
        ArrayAdapter<String> searchTypeAdapter = new ArrayAdapter<>(SearchEBookReaderActivity.this, android.R.layout.simple_spinner_item, new String[]{"Title", "ISBN"});
        searchTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSearchSpinner.setAdapter(searchTypeAdapter);

        eSearch.addTextChangedListener(
                searchHelper.addTextWatcherToBookListForReader(
                        SearchEBookReaderActivity.this,
                        eSearch,
                        sSearchSpinner,
                        books,
                        tempBooks,
                        listBooks
                )
        );

        if (!AppState.get().isAccountBlocked()) {
            databaseOperationHelper.loadBooks(adapter, books, "eBooks");
        } else {
            message.setText("Unfortunately your account is blocked, because you didn't return your books in time to the librarian.\n\nReturn the books and pay the fine.");
            eSearch.setVisibility(View.INVISIBLE);
        }
    }
}