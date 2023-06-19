package com.sma.proiect.librarian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.sma.proiect.AppState;
import com.sma.proiect.ItemForMenu;
import com.sma.proiect.R;
import com.sma.proiect.helpers.AdapterViewHelper;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import com.sma.proiect.helpers.NavigationViewHelper;
import java.util.ArrayList;
import java.util.List;


public class ChooseBookLibrarianActivity extends AppCompatActivity {

    private DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
    int[] iconsForMenu = new int[] {
            R.drawable.book, R.drawable.ebook
    };
    String[] textsForMenu = new String[] {
            "Search books", "Search eBooks"
    };
    List<ItemForMenu> menuItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_book_librarian);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        GridView gridView = findViewById(R.id.gridView);

        GridAdapterChooseBookLibrarianActivity gridAdapter = new GridAdapterChooseBookLibrarianActivity(ChooseBookLibrarianActivity.this, R.layout.grid_adapter_choose_book_librarian, menuItems);
        gridView.setAdapter(gridAdapter);

        NavigationViewHelper navigationViewHelper = new NavigationViewHelper();
        AdapterViewHelper adapterViewHelper = new AdapterViewHelper();
        ArrayAdapter<String> searchTypeAdapter = new ArrayAdapter<>(ChooseBookLibrarianActivity.this, android.R.layout.simple_spinner_item, new String[]{"Title", "ISBN"});
        searchTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();

        // add icons to view
        menuItems.clear();
        for (int i = 0; i < iconsForMenu.length; i++) {
            int currentIcon = iconsForMenu[i];
            String currentText = textsForMenu[i];
            menuItems.add(new ItemForMenu(currentText, currentIcon));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(
                navigationViewHelper.getNavViewForLibrarian(
                        ChooseBookLibrarianActivity.this,
                        drawerLayout
                )
        );

        gridView.setOnItemClickListener(
                adapterViewHelper.addListenerToGridviewForLibrarian(
                        ChooseBookLibrarianActivity.this
                )
        );

        if (AppState.get().getUserID() != null) {
            databaseReference.child("eBookBorrows").addValueEventListener(
                    databaseOperationHelper.retrieveLateEBooksFromUsers()
            );
        }
    }
}