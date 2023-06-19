package com.sma.proiect.helpers;

import static com.sma.proiect.MainActivity.SI_CODE;
import static com.sma.proiect.MainActivity.SU_CODE;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sma.proiect.AppState;
import com.sma.proiect.FirebaseCallback;
import com.sma.proiect.user.User;
import java.io.IOException;


public class ActivityForResultHelper {

    private Context currentContext;

    public ActivityForResultHelper(Context context) {
        currentContext = context;
    }

    public FirebaseAuth.AuthStateListener checkAuthStatus() {
        /**
         * The function checks if there is a current user logged in the application and sets the
         * current user ID to the application state.
         */
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                CallbackHelper callbackHelper = new CallbackHelper();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    callbackHelper.waitForDatabaseToLoad(currentContext,
                            new FirebaseCallback() {
                                @Override
                                public void onResponse(User currentUser) {
                                    AppState.get().setUserId(user.getUid());
                                    AppState.get().setCurrentUser(currentUser);
                                }
                            }, user.getUid());
                } else {
                    // User is signed out
                    AppState.get().setCurrentUser(null);
                    AppState.get().setUserId(null);
                }
            }
        };
    }

    public ActivityResultCallback<ActivityResult> setBrowsedEBookPath(TextView eBookPath) {
        return new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                Intent intent = result.getData();
                if (resultCode == Activity.RESULT_OK) {
                    if (intent != null) {
                        Uri uri = intent.getData();
                        String sEBookPath = intent.getDataString();
                        AppState.get().setBrowsedEBookFilePathUri(uri);
                        eBookPath.setText(sEBookPath);
                    }
                }
            }
        };
    }

    public ActivityResultCallback<ActivityResult> setBrowsedEBookCover(Context context, ImageView cover) {
        return new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                Intent intent = result.getData();
                if (resultCode == Activity.RESULT_OK) {
                    if (intent != null) {
                        Uri uEBoorCoverPath = intent.getData();
                        Bitmap bitmap;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uEBoorCoverPath);
                            AppState.get().setBrowsedEBookCoverPathUri(uEBoorCoverPath);
                            cover.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            AppState.get().setBrowsedEBookCoverPathUri(null);
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    public ActivityResultCallback<ActivityResult> signUpOrLogIn(Context context, FirebaseAuth mAuth) {
        /**
         * The function returns a callback that retrieves the result code from the intent that
         * is sent from LogInActivity or SignUpActivity and establishes the type of action required
         * to be done according to the information from the intent.
         * In this case:
         * - LogInActivity sends the user's username and password through the intent to the activity
         *   indicated by the context parameter and logs in the user.
         * - SignUpActivity sends the account information through the intent to the activity
         *   indicated by the context parameter and signs up the user.
         */
        return new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                Intent intent = result.getData();
                CallbackHelper callbackHelper = new CallbackHelper();

                String firstName;
                String lastName;
                String email;
                String userType;
                String password;
                String password2;

                if (resultCode == SU_CODE) {
                    if (intent != null) {
                        email = intent.getStringExtra("email");
                        password = intent.getStringExtra("password");
                        password2 = intent.getStringExtra("password2");
                        firstName = intent.getStringExtra("first name");
                        lastName = intent.getStringExtra("last name");
                        userType = intent.getStringExtra("user type");

                        if (email != null && password != null) {
                            if (password.equals(password2)) {
                                callbackHelper.createUser(context, mAuth, email, password, firstName, lastName, userType);
                            } else {
                                Toast.makeText(context, "Passwords don't coincide", Toast.LENGTH_SHORT).show();
                                Log.i("signUpOrLogIn", "Passwords don't coincide");
                            }
                        }
                    }
                } else if (resultCode == SI_CODE) {
                    if (intent != null) {
                        email = intent.getStringExtra("email");
                        password = intent.getStringExtra("password");

                        if (email != null && password != null) {
                            callbackHelper.signInUser(context, mAuth, email, password);
                        }
                    }
                }
            }
        };
    }
}
