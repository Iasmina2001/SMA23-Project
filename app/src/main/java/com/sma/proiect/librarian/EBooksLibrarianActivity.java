package com.sma.proiect.librarian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.R;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import com.sma.proiect.helpers.NavigationViewHelper;
import com.sma.proiect.helpers.SearchHelper;
import java.util.ArrayList;
import java.util.List;


public class EBooksLibrarianActivity extends AppCompatActivity {

    private List<Book> eBooks = new ArrayList<>();
    private DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebooks_librarian);

        ListView listBooks = findViewById(R.id.listBooks);
        Spinner sSearchSpinner = findViewById(R.id.sSearchSpinner);
        EditText eSearch = findViewById(R.id.eSearch);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        NavigationViewHelper navigationViewHelper = new NavigationViewHelper();
        SearchHelper searchHelper = new SearchHelper();
        List<Book> tempBooks = new ArrayList<>();
        ArrayAdapter<String> searchTypeAdapter = new ArrayAdapter<>(EBooksLibrarianActivity.this, android.R.layout.simple_spinner_item, new String[]{"Title", "ISBN"});
        searchTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSearchSpinner.setAdapter(searchTypeAdapter);

        final EBookAdapterLibrarianActivity adapter = new EBookAdapterLibrarianActivity(this, R.layout.activity_ebook_adapter_librarian, eBooks);
        listBooks.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        eSearch.addTextChangedListener(
                searchHelper.addTextWatcherToBookListForLibrarian(
                        EBooksLibrarianActivity.this,
                        eSearch,
                        sSearchSpinner,
                        eBooks,
                        tempBooks,
                        listBooks
                )
        );

        listBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppState.get().setCurrentBook(eBooks.get(i));
                Intent intent = new Intent(getApplicationContext(), AddBookLibrarianActivity.class);
                startActivity(intent);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(
                navigationViewHelper.getNavViewForLibrarian(
                        EBooksLibrarianActivity.this,
                        drawerLayout
                )
        );

        databaseOperationHelper.loadBooks(adapter, eBooks, "eBooks");
    }
}