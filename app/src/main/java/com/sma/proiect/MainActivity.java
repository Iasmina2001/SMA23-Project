package com.sma.proiect;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sma.proiect.librarian.BooksLibrarianActivity;
import com.sma.proiect.reader.SearchBookReaderActivity;
import com.sma.proiect.user.Librarian;
import com.sma.proiect.user.Reader;
import com.sma.proiect.user.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private String firstName;
    private String lastName;
    private String email;
    private String userType;
    private String password;
    private String password2;

    public static final int SU_CODE = 101;
    public static final int SI_CODE = 106;

    private final String TAG_SI = "Sign in activity";
    private final String TAG_SU = "Sign up activity";
    private FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;

    // -- register callback --
    ActivityResultLauncher<Intent> mStartForResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        AppState.get().setDatabaseReference(databaseReference);
        AppState.get().setCurrentUser(null);
        AppState.get().setUserId(null);
        AppState.get().setCanAttachDBListener(false);

        Button bLogIn = findViewById(R.id.bLogIn);
        Button bSignUp = findViewById(R.id.bSignUp);
        Button bHelp = findViewById(R.id.bHelp);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    waitForDatabaseToLoad(new FirebaseCallback() {
                        @Override
                        public void onResponse(User currentUser) {
                            Log.d(TAG_SU, "onAuthStateChanged:signed_in:" + user.getUid());
                            AppState.get().setUserId(user.getUid());
                            AppState.get().setCanAttachDBListener(true);
                            AppState.get().setCurrentUser(currentUser);
                        }
                    }, user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG_SU, "onAuthStateChanged:signed_out");
                    AppState.get().setCurrentUser(null);
                    AppState.get().setUserId(null);
                    AppState.get().setCanAttachDBListener(false);
                    // mStartForResult.launch();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent intent = result.getData();
                        if (resultCode == SU_CODE) {
                            if (intent != null)
                            {
                                email = intent.getStringExtra("email");
                                password = intent.getStringExtra("password");
                                password2 = intent.getStringExtra("password2");
                                firstName = intent.getStringExtra("first name");
                                lastName = intent.getStringExtra("last name");
                                userType = intent.getStringExtra("user type");

                                if (email != null && password != null)
                                {
                                    if (password.equals(password2))
                                    {
                                        mAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        Log.d(TAG_SU, "Authentication successful");
                                                        if (task.isSuccessful()) {
                                                            // Sign in success, update UI with the signed-in user's information
                                                            Log.d(TAG_SU, "signUp:success");
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            if (user != null) {
                                                                Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                                                                if (userType.equals("librarian")) {
                                                                    addUserToDatabase(firstName, lastName, userType, user.getUid());
                                                                    startActivity(new Intent(MainActivity.this, BooksLibrarianActivity.class));
                                                                } else if (userType.equals("reader")) {
                                                                    addUserToDatabase(firstName, lastName, userType, user.getUid());
                                                                    startActivity(new Intent(MainActivity.this, SearchBookReaderActivity.class));
                                                                }
                                                            }
                                                        } else {
                                                            // If sign in fails, display a message to the user.
                                                            Log.w(TAG_SU, "signIn:failure", task.getException());
                                                            Toast.makeText(MainActivity.this, "Sign in failed.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(MainActivity.this, "Passwords don't coincide", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } else if (resultCode == SI_CODE) {
                            if (intent != null)
                            {
                                email = intent.getStringExtra("email");
                                password = intent.getStringExtra("password");

                                if (email != null && password != null)
                                {
                                    mAuth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    Log.d(TAG_SI, "Authentication successful");
                                                    if (task.isSuccessful()) {
                                                        // Sign in success, update UI with the signed-in user's information
                                                        Log.d(TAG_SI, "createUserWithEmail:success");

                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        if (user != null) {
                                                            User currentUser = AppState.get().getCurrentUser();    // aici e greseala :(; nu se citeste din firebase si nu seteaza user-ul in appState
                                                            String currentUserUID = AppState.get().getUserID();
                                                            if (currentUser != null) {
                                                                if (currentUser.getUserType().equals("librarian")) {
                                                                    startActivity(new Intent(MainActivity.this, BooksLibrarianActivity.class));
                                                                } else if (currentUser.getUserType().equals("reader")) {
                                                                    startActivity(new Intent(MainActivity.this, SearchBookReaderActivity.class));
                                                                }
                                                                Toast.makeText(MainActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    } else {
                                                        // If sign in fails, display a message to the user.
                                                        Log.w(TAG_SI, "createUserWithEmail:failure", task.getException());
                                                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });

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
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private void waitForDatabaseToLoad(FirebaseCallback callback, String currentUserUID) {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        databaseReference.child(String.format("users/%s", currentUserUID)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String firstName = Objects.requireNonNull(task.getResult().child("First name").getValue()).toString();
                    String lastName = Objects.requireNonNull(task.getResult().child("Last name").getValue()).toString();
                    String userType = Objects.requireNonNull(task.getResult().child("User type").getValue()).toString();
                    String userID = Objects.requireNonNull(task.getResult().child("User ID").getValue()).toString();
                    User user = null;
                    if (userType.equals("librarian")) {
                        user = new Librarian(firstName, lastName, userType, userID);
                        startActivity(new Intent(MainActivity.this, BooksLibrarianActivity.class));
                    } else if (userType.equals("reader")) {
                        user = new Reader(firstName, lastName, userType, userID);
                        startActivity(new Intent(MainActivity.this, SearchBookReaderActivity.class));
                    }
                    callback.onResponse(user);
                } else {
                    Log.d("DB attach", Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }

    private void deleteUserFromDatabase(String userID) {
        AppState.get().getDatabaseReference().child("users").child(userID).removeValue();
    }

    private void addUserToDatabase(String firstName, String lastName, String userType, String userID) {
        Map<String, Object> map = new HashMap<>();

        map.put("First name", firstName);
        map.put("Last name", lastName);
        map.put("User type", userType);
        map.put("User ID", userID);

        AppState.get().getDatabaseReference().child("users").child(userID).updateChildren(map);
    }
}