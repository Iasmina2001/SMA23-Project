package com.sma.proiect.reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.R;
import com.sma.proiect.helpers.AlertDialogHelper;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import com.sma.proiect.helpers.NavigationViewHelper;
import java.util.ArrayList;
import java.util.List;


public class ReadingChallengeActivity extends AppCompatActivity {

    private List<Book> books = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_challenge);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        TextView message = findViewById(R.id.tMessage);
        GridView gridView = findViewById(R.id.gridView);
        Button bAddReadingChallenge = findViewById(R.id.bAddReadingChallenge);
        Button bEditReadingChallenge = findViewById(R.id.bEditReadingChallenge);

        BorrowedEBookGridAdapter gridAdapter = new BorrowedEBookGridAdapter(ReadingChallengeActivity.this, R.layout.activity_borrowed_ebook_grid_adapter, books, ReadingChallengeActivity.class.getName());
        gridView.setAdapter(gridAdapter);

        NavigationViewHelper navigationViewHelper = new NavigationViewHelper();
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        AlertDialogHelper alertDialogHelper = new AlertDialogHelper();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(
                navigationViewHelper.getNavViewForReader(
                        ReadingChallengeActivity.this,
                        drawerLayout
                )
        );

        bAddReadingChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogHelper.addAlertDialogForReadingChallenge(ReadingChallengeActivity.this, progressBar, bAddReadingChallenge, bEditReadingChallenge, message, gridAdapter, books);
            }
        });

        bEditReadingChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogHelper.addAlertDialogForReadingChallenge(ReadingChallengeActivity.this, progressBar, bAddReadingChallenge, bEditReadingChallenge, message, gridAdapter, books);
            }
        });

        databaseOperationHelper.checkIfUserHasReadingChallenge(message, bAddReadingChallenge, bEditReadingChallenge, progressBar, gridAdapter, books);
    }
}