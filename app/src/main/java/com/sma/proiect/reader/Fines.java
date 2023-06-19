package com.sma.proiect.reader;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.sma.proiect.AppState;
import com.sma.proiect.BookRequest;
import com.sma.proiect.R;
import com.sma.proiect.helpers.CallbackHelper;
import com.sma.proiect.helpers.DatabaseOperationHelper;
import java.util.ArrayList;
import java.util.List;


public class Fines extends AppCompatActivity {

    private List<BookRequest> bookRequests = new ArrayList<>();
    private TextView tFines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fines);

        ListView listBooks = findViewById(R.id.listBooks);
        tFines = findViewById(R.id.tFinesValue);
        final FinesAdapterActivity adapter = new FinesAdapterActivity(this, R.layout.activity_fines_adapter, bookRequests);
        listBooks.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        String currentUID = AppState.get().getUserID();
        CallbackHelper callbackHelper = new CallbackHelper();
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        databaseReference.child("bookRequests").child(currentUID).addValueEventListener(
                databaseOperationHelper.displayFines(adapter, tFines, bookRequests)
        );
    }
}