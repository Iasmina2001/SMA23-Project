package com.sma.proiect.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.sma.proiect.AppState;
import com.sma.proiect.R;
import com.sma.proiect.librarian.AddBookLibrarianActivity;
import com.sma.proiect.librarian.BookRequestsLibrarianActivity;
import com.sma.proiect.librarian.ChooseBookLibrarianActivity;
import com.sma.proiect.reader.BookRequestsReaderActivity;
import com.sma.proiect.reader.BorrowedEBooksActivity;
import com.sma.proiect.reader.Fines;


public class NavigationViewHelper {

    public NavigationView.OnNavigationItemSelectedListener getNavViewForReader(Context context, DrawerLayout drawerLayout) {
        return new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
                AlertDialogHelper alertDialogHelper = new AlertDialogHelper();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id) {
                    case R.id.nav_account_info:
                        alertDialogHelper.addAlertDialogToAccountInfo(context);
                        break;
                    case R.id.nav_book_requests:
                        context.startActivity(new Intent(context, BookRequestsReaderActivity.class));
                        break;
                    case R.id.nav_borrowed_eBooks:
                        context.startActivity(new Intent(context, BorrowedEBooksActivity.class));
                        break;
                    case R.id.nav_fines:
                        context.startActivity(new Intent(context, Fines.class));
                        break;
                    case R.id.nav_logout:
                        databaseOperationHelper.signOutUser(context);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        };
    }

    public NavigationView.OnNavigationItemSelectedListener getNavViewForLibrarian(Context context, DrawerLayout drawerLayout) {
        return new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
                AlertDialogHelper alertDialogHelper = new AlertDialogHelper();
                int id = item.getItemId();
                Intent intent;
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id) {
                    case R.id.nav_account_info:
                        alertDialogHelper.addAlertDialogToAccountInfo(context);
                        break;
                    case R.id.nav_book_requests:
                        intent = new Intent(context, BookRequestsLibrarianActivity.class);
                        context.startActivity(intent);
                        break;
                    case R.id.nav_add_book:
                        AppState.get().setCurrentBook(null);
                        intent = new Intent(context, AddBookLibrarianActivity.class);
                        context.startActivity(intent);
                        break;
                    case R.id.nav_logout:
                        databaseOperationHelper.signOutUser(context);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        };
    }
}
