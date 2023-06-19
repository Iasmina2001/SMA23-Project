package com.sma.proiect;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sma.proiect.helpers.ActivityForResultHelper;


public class MainActivity extends AppCompatActivity {

    public static final int SU_CODE = 101;
    public static final int SI_CODE = 106;
    private FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    ActivityResultLauncher<Intent> mStartForResult;    // register callback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        AppState.get().setDatabaseReference(databaseReference);
        AppState.get().setCurrentUser(null);
        AppState.get().setUserId(null);

        Button bLogIn = findViewById(R.id.bLogIn);
        Button bSignUp = findViewById(R.id.bSignUp);
        Button bHelp = findViewById(R.id.bHelp);

        ActivityForResultHelper activityForResultHelper = new ActivityForResultHelper(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = activityForResultHelper.checkAuthStatus();
        mAuth.addAuthStateListener(mAuthListener);

        mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                activityForResultHelper.signUpOrLogIn(MainActivity.this, mAuth)
        );

        bLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                mStartForResult.launch(intent);
            }
        });

        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                mStartForResult.launch(intent);
            }
        });

        bHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("MainActivity", "MainActivity started");
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}