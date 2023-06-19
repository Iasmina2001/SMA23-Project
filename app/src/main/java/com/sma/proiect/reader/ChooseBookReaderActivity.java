package com.sma.proiect.reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.sma.proiect.AppState;
import com.sma.proiect.BroadcastNotificationManager;
import com.sma.proiect.FirebaseBlockAccountCallback;
import com.sma.proiect.ItemForMenu;
import com.sma.proiect.R;
import com.sma.proiect.helpers.AdapterViewHelper;
import com.sma.proiect.helpers.AlertDialogHelper;
import com.sma.proiect.helpers.CallbackHelper;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import com.sma.proiect.helpers.NavigationViewHelper;
import java.util.ArrayList;
import java.util.List;


public class ChooseBookReaderActivity extends AppCompatActivity {

    public static final String TAG_NEWS = "APPROVED_REQUEST";
    public static final String TAG_CHILL = "NOT_APPROVED_REQUEST";
    int[] iconsForMenu = new int[] {
            R.drawable.book, R.drawable.ebook, R.drawable.rate, R.drawable.reading_challenge
    };
    String[] textsForMenu = new String[] {
            "Borrow books", "Borrow eBooks", "Rate books", "Reading challenge"
    };
    List<ItemForMenu> menuItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_book_reader);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        TextView message = findViewById(R.id.tMessage);
        ImageView iNotification = findViewById(R.id.iNotification);
        GridView gridView = findViewById(R.id.gridView);

        // add icons to view
        menuItems.clear();
        for (int i = 0; i < iconsForMenu.length; i++) {
            int currentIcon = iconsForMenu[i];
            String currentText = textsForMenu[i];
            menuItems.add(new ItemForMenu(currentText, currentIcon));
        }

        GridAdapterChooseBookReaderActivity gridAdapter = new GridAdapterChooseBookReaderActivity(ChooseBookReaderActivity.this, R.layout.grid_adapter_choose_book_reader, menuItems);
        gridView.setAdapter(gridAdapter);

        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        AlertDialogHelper alertDialogHelper = new AlertDialogHelper();
        AdapterViewHelper adapterViewHelper = new AdapterViewHelper();
        NavigationViewHelper navigationViewHelper = new NavigationViewHelper();
        CallbackHelper callbackHelper = new CallbackHelper();
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        BroadcastNotificationManager broadcastNotificationManagerForFunction = new BroadcastNotificationManager();
        broadcastNotificationManagerForFunction.createNotificationChannel(ChooseBookReaderActivity.this);

        iNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogHelper.addAlertDialogToReaderNotification(ChooseBookReaderActivity.this, iNotification);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        gridView.setOnItemClickListener(
                adapterViewHelper.addListenerToGridviewForReader(
                        ChooseBookReaderActivity.this
                )
        );

        navigationView.setNavigationItemSelectedListener(
                navigationViewHelper.getNavViewForReader(
                        ChooseBookReaderActivity.this,
                        drawerLayout
                )
        );

        String currentUID = AppState.get().getUserID();
        if (currentUID != null) {
            databaseReference.child("eBookBorrows").child(currentUID).addValueEventListener(
                    databaseOperationHelper.returnLateEBooks()
            );
        }

        String currentUserUID = AppState.get().getUserID();
        AppState.get().blockAccount(false);    // suppose the account isn't blocked
        if (currentUserUID != null) {
            databaseReference.child(String.format("bookRequests/%s", currentUserUID)).addListenerForSingleValueEvent(
                    callbackHelper.grantAccessToAccount(
                            ChooseBookReaderActivity.this,
                            new FirebaseBlockAccountCallback() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onCallback(boolean blockAccount) {
                                    AppState.get().blockAccount(blockAccount);
                                }
                            },
                            iNotification
                    )
            );
        }
    }
}