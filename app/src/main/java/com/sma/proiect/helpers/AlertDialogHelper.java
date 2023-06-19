package com.sma.proiect.helpers;

import static com.sma.proiect.reader.ChooseBookReaderActivity.TAG_CHILL;
import static com.sma.proiect.reader.ChooseBookReaderActivity.TAG_NEWS;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.R;
import com.sma.proiect.reader.ReadingChallengeActivity;

import java.util.List;


public class AlertDialogHelper {

    public void addAlertDialogToReaderNotification(Context context, ImageView imageView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        String greeting = "";
        String imageViewNotificationTag = String.valueOf(imageView.getTag());
        if (imageViewNotificationTag.equals(TAG_CHILL)) {
            greeting = "You currently don't have approved book requests. :'(";
        } else if (imageViewNotificationTag.equals(TAG_NEWS)) {
            greeting = "You have approved book requests. :D Check them in your fines tab.";
        }

        builder.setCancelable(true);
        builder.setMessage(greeting);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // message to display
                int duration = Toast.LENGTH_SHORT;
                String text = "I understood! XD";
                Toast toast = Toast.makeText(context, text, duration);
                // to show the toast
                toast.show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void addAlertDialogToAccountInfo(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String currentUserUID = AppState.get().getUserID();
        String message = "Your account user ID is:\n" + currentUserUID;

        builder.setCancelable(true);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void addAlertDialogForReadingChallenge(Context context, ProgressBar progressBar, Button bAddReadingChallenge, Button bEditReadingChallenge, TextView message, ArrayAdapter<Book> gridAdapter, List<Book> books) {
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.activity_add_reading_challenge, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogLayout);
        builder.setTitle("Add number of books for the reading challenge.");
        // add a button
        builder.setPositiveButton("OK", (dialog, which) -> {
            // send data from the AlertDialog to the Activity
            EditText editText = dialogLayout.findViewById(R.id.editText);
            long numOfBooksForReadingChallenge = Long.parseLong(editText.getText().toString());
            databaseOperationHelper.addReadingChallenge(numOfBooksForReadingChallenge, progressBar, bAddReadingChallenge, bEditReadingChallenge, message);

            databaseOperationHelper.checkIfUserHasReadingChallenge(message, bAddReadingChallenge, bEditReadingChallenge, progressBar, gridAdapter, books);
        }).setNegativeButton( "Cancel", (dialog, which) -> {

        });
        builder.show();
    }

    public void addAlertDialogForAddingRating(Context context, Book book) {
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View dialogLayout = inflater.inflate(R.layout.activity_add_rating, null);
        RatingBar ratingBar = dialogLayout.findViewById(R.id.ratingBar);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogLayout);
        builder.setTitle("Give the book a grade! :)");

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            String sRatingValue = String.valueOf(ratingBar.getRating());
            float fRatingValue = 0;
            try {
                fRatingValue = Float.parseFloat(sRatingValue);
            } catch (NumberFormatException nfe) {
                // :)
            }
            if (fRatingValue > 0) {
                databaseOperationHelper.setRatingToBook(book, fRatingValue);
            } else {
                Toast.makeText(context.getApplicationContext(), "Rating must be at least 1 star.", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", (dialog, which) -> {

        });

        builder.show();
    }

    public void addAlertDialogToEBookBorrow(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String message = "You can borrow maximum 3 eBooks at a time. The borrow period is of two weeks. After the period expires, you cannot access the same eBooks unless you request them again and there are available eBooks.";
        builder.setCancelable(true);
        builder.setMessage(message);

        builder.setPositiveButton("Borrow eBook", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setCancelable(true);
    }
}
