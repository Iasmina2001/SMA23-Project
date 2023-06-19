package com.sma.proiect.helpers;

import static android.content.Context.ALARM_SERVICE;
import static com.sma.proiect.reader.ChooseBookReaderActivity.TAG_NEWS;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sma.proiect.AppState;
import com.sma.proiect.BookRequest;
import com.sma.proiect.BroadcastNotificationManager;
import com.sma.proiect.FirebaseBlockAccountCallback;
import com.sma.proiect.FirebaseCallback;
import com.sma.proiect.R;
import com.sma.proiect.librarian.ChooseBookLibrarianActivity;
import com.sma.proiect.reader.ChooseBookReaderActivity;
import com.sma.proiect.user.Librarian;
import com.sma.proiect.user.Reader;
import com.sma.proiect.user.User;
import java.util.Objects;


public class CallbackHelper {

    public void waitForDatabaseToLoad(Context context, FirebaseCallback callback, String currentUserUID) {
        /**
         * Because of the asynchronous functioning of APIs, the program needs to wait for the
         * Firebase database to load, in order to function properly.
         * The function checks the current user that is logged in and loads the page according to
         * the user type.
         * When data is retrieved from the account information, it is transmitted using the callback
         * parameter back to the according context, in order to be processed.
         */
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
                        Intent i = new Intent(context, ChooseBookLibrarianActivity.class);
                        context.startActivity(i);
                    } else if (userType.equals("reader")) {
                        user = new Reader(firstName, lastName, userType, userID);
                        Intent i = new Intent(context, ChooseBookReaderActivity.class);
                        context.startActivity(i);
                    }
                    callback.onResponse(user);
                } else {
                    Log.d("DB attach", Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }

    public void createUser(Context context, FirebaseAuth mAuth, String email, String password, String firstName, String lastName, String userType) {
        /**
         * The function creates a new account with the given parameters, by adding the user
         * information to the database.
         */
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("createUser", "User account created successfully");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Toast.makeText(context, user.toString(), Toast.LENGTH_SHORT).show();
                                DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
                                if (userType.equals("librarian")) {
                                    databaseOperationHelper.addUserToDatabase(firstName, lastName, userType, user.getUid());
                                    Intent i = new Intent(context, ChooseBookLibrarianActivity.class);
                                    context.startActivity(i);
                                } else if (userType.equals("reader")) {
                                    databaseOperationHelper.addUserToDatabase(firstName, lastName, userType, user.getUid());
                                    Intent i = new Intent(context, ChooseBookReaderActivity.class);
                                    context.startActivity(i);
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Sign in failed.", Toast.LENGTH_SHORT).show();
                            Log.i("createUser", "User account creation failed");
                        }
                    }
                });
    }

    public void signInUser(Context context, FirebaseAuth mAuth, String email, String password) {
        /**
         * The function signs into a user's account and starts a new activity according to the user
         * type. If there is no user with given email and password, the function toasts a suggestive
         * message to the user.
         */
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                User currentUser = AppState.get().getCurrentUser();    // aici e greseala :(; nu se citeste din firebase si nu seteaza user-ul in appState
                                if (currentUser != null) {
                                    Log.i("MainActivity", "Signed in successfully");
                                    if (currentUser.getUserType().equals("librarian")) {
                                        Intent i = new Intent(context, ChooseBookLibrarianActivity.class);
                                        context.startActivity(i);
                                    } else if (currentUser.getUserType().equals("reader")) {
                                        Intent i = new Intent(context, ChooseBookReaderActivity.class);
                                        context.startActivity(i);
                                    }
                                    Toast.makeText(context, user.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("SignInUser", "Signing in failed");
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public ValueEventListener grantAccessToAccount(Context context, FirebaseBlockAccountCallback firebaseCallback, ImageView iNotification) {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        String currentUserUID = AppState.get().getUserID();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean blockAccount;
                long numDaysLateForBookRequest = 0;
                long maxNumDaysLateForBookRequest = 0;
                long fineValuePerDay = 10;    // fine for returning the book one day late
                for (DataSnapshot bookRequestSnapshot : dataSnapshot.getChildren()) {
                    if (bookRequestSnapshot.child("End date").exists()) {
                        String endDate = Objects.requireNonNull(bookRequestSnapshot.child("End date").getValue()).toString();
                        BookRequest bookRequestHandlerForFunctions = new BookRequest();
                        if (bookRequestHandlerForFunctions.remindBookRequest(endDate, 1)) {
                            Intent intent = new Intent(context, BroadcastNotificationManager.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
                        }
                        if (bookRequestHandlerForFunctions.isBookRequestLate(endDate)) {
                            // checks if reader didn't return books on time
                            numDaysLateForBookRequest = bookRequestHandlerForFunctions.getNumOfDaysLateForBookRequest(endDate);
                            // assign the fine according to the oldest book request
                            if (numDaysLateForBookRequest > maxNumDaysLateForBookRequest) {
                                maxNumDaysLateForBookRequest = numDaysLateForBookRequest;
                                databaseReference.child("bookRequests").child(currentUserUID).child("Forbid access").setValue(1);
                                databaseReference.child("bookRequests").child(currentUserUID).child("Fine").setValue(fineValuePerDay * numDaysLateForBookRequest);
                            }
                        } else {
                            // if the end date didn't expire, the notification bell if turned on
                            // to show the user that his book requests have been submitted
                            iNotification.setImageResource(R.drawable.bell_nofification);
                            iNotification.setTag(TAG_NEWS);
                        }
                    }
                }
                // checks if reader account is blocked
                if (dataSnapshot.child("Forbid access").exists()) {
                    String forbidAccess = Objects.requireNonNull(dataSnapshot.child("Forbid access").getValue()).toString();
                    blockAccount = forbidAccess.equals("1");
                } else {
                    blockAccount = false;
                }
                firebaseCallback.onCallback(blockAccount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
    }
}
