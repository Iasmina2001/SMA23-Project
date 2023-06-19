package com.sma.proiect.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.BookRequest;
import com.sma.proiect.MainActivity;
import com.sma.proiect.R;
import com.sma.proiect.UploadCoverToStorageCallback;
import com.sma.proiect.UploadFileToStorageCallback;
import com.sma.proiect.librarian.AddBookLibrarianActivity;
import com.sma.proiect.librarian.BookRequestsAdapterLibrarianActivity;
import com.sma.proiect.reader.BookRequestsAdapterActivity;
import com.sma.proiect.reader.FinesAdapterActivity;
import com.sma.proiect.reader.PdfViewActivity;
import com.sma.proiect.user.User;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class DatabaseOperationHelper {

    public void addUserToDatabase(String firstName, String lastName, String userType, String userID) {
        /**
         * Function is used at account creation to add the account information to the database.
         */
        Map<String, Object> map = new HashMap<>();

        map.put("First name", firstName);
        map.put("Last name", lastName);
        map.put("User type", userType);
        map.put("User ID", userID);

        AppState.get().getDatabaseReference().child("users").child(userID).updateChildren(map);
    }

    public void deleteUserFromDatabase(String userID) {
        AppState.get().getDatabaseReference().child("users").child(userID).removeValue();
    }

    public void signOutUser(Context context) {
        AppState.get().setCurrentUser(null);
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public void deleteBook(Context context, String ISBN10, String realtimeDatabasePath) {
        /**
         * Function deletes the book indicated by ISBN10 from the books database.
         * It is used by the librarian to manage book entries.
         */
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        StorageOperationHelper storageOperationHelper = new StorageOperationHelper();
        Book bookForFunction = new Book();
        String storageImagePath = bookForFunction.getStorageImagePathAccordingToRealtimeDatabasePath(realtimeDatabasePath);

        // delete book from realtime database
        databaseReference.child(realtimeDatabasePath).child(ISBN10).removeValue();

        // delete book from storage
        if (realtimeDatabasePath.equals("eBooks")) {
            storageOperationHelper.deleteEBookFileFromStorage(context, ISBN10);
        }
        storageOperationHelper.deleteBookCoverFromStorage(context, ISBN10, storageImagePath);

        Toast.makeText(context, "Book deleted :'(", Toast.LENGTH_SHORT).show();
    }

    public void editBook(Context context) {
        /**
         * @param databasePath: String which indicates whether the book information belong to the
         *                      "books" or "eBooks" realtime database
         */
        Intent intent = new Intent(context.getApplicationContext(), AddBookLibrarianActivity.class);
        ((Activity) context).startActivity(intent);
    }

    public void saveBook(Context context, String title, String author, String publisher, String publicationDate, String genre, String ISBN10, String numOfBooks, String type, String eBookFilePath, TextView tEBookFilePath, ImageView iEBookCover) {
        /**
         * Function adds book to the database, with its corresponding informative attributes.
         * It is used by the librarian to manage book entries.
         */
        Map<String, Object> map = new HashMap<>();
        map.put("Title", title);
        map.put("Author", author);
        map.put("Publisher", publisher);
        map.put("Publication date", publicationDate);
        map.put("Genre", genre);
        map.put("Number of books", numOfBooks);

        Book bookBeforeEditing = AppState.get().getCurrentBook();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        // check if book was previously edited
        if (bookBeforeEditing != null) {
            String databasePathOfBookBeforeEditing = bookBeforeEditing.getRealtimeDatabasePath();
            // check if book type changed
            if ((databasePathOfBookBeforeEditing.equals("books") && type.equals("eBook")) ||
                    (databasePathOfBookBeforeEditing.equals("eBooks") && type.equals("paper book"))) {
                // delete previous book
                databaseReference.child(databasePathOfBookBeforeEditing).child(ISBN10).removeValue();
            }
        }

        if (title.length() > 0 && author.length() > 0 && publisher.length() > 0 && publicationDate.length() > 0 && genre.length() > 0 && numOfBooks.length() > 0) {
            StorageOperationHelper storageOperationHelper = new StorageOperationHelper();
            DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
            if (type.equals("paper book")) {
                if (eBookFilePath.length() == 0) {
                    databaseReference.child("books").child(ISBN10).updateChildren(map);

                    // upload book cover
                    storageOperationHelper.uploadBookCover(ISBN10, "bookCovers", new UploadCoverToStorageCallback() {
                        @Override
                        public void onUploaded(Uri downloadUrl, String storageImagePath) {
                            if (storageImagePath.equals("bookCovers")) {
                                databaseReference.child("books").child(ISBN10).child("Book cover link").setValue(downloadUrl.toString());
                            } else if (storageImagePath.equals("eBookCovers")) {
                                databaseReference.child("eBooks").child(ISBN10).child("Book cover link").setValue(downloadUrl.toString());
                            }
                        }
                    });
                    databaseOperationHelper.setDownloadedBookCoverLinkLibrarian(context, iEBookCover);

                    Toast.makeText(context, "Saved book to database! :D", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context.getApplicationContext(), "Paper books don't have file link.", Toast.LENGTH_SHORT).show();
                }
            } else if (type.equals("eBook")) {
                if (eBookFilePath.length() > 0) {
                    databaseReference.child("eBooks").child(ISBN10).updateChildren(map);

                    // upload eBook file
                    storageOperationHelper.uploadEBookFile(ISBN10, new UploadFileToStorageCallback() {
                        @Override
                        public void onUploaded(Uri downloadUrl) {
                            databaseReference.child("eBooks").child(ISBN10).child("Book file link").setValue(downloadUrl.toString());
                        }
                    });

                    // upload eBook cover
                    storageOperationHelper.uploadBookCover(ISBN10, "eBookCovers", new UploadCoverToStorageCallback() {
                        @Override
                        public void onUploaded(Uri downloadUrl, String storageImagePath) {
                            if (storageImagePath.equals("bookCovers")) {
                                databaseReference.child("books").child(ISBN10).child("Book cover link").setValue(downloadUrl.toString());
                            } else if (storageImagePath.equals("eBookCovers")) {
                                databaseReference.child("eBooks").child(ISBN10).child("Book cover link").setValue(downloadUrl.toString());
                            }
                        }
                    });

                    Toast.makeText(context, "Saved book to database! :D", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context.getApplicationContext(), "eBooks must have file link.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(context.getApplicationContext(), "All fields must be filled!", Toast.LENGTH_SHORT).show();
        }
    }

    public void requestBook(Context context, User currentUser, String ISBN10, String title) {
        /**
         * Function adds the book request made by the current user to the database.
         */
        String currentUserUID = currentUser.getUserID();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();

        // check if book has already been requested
        databaseReference.child("bookRequests").child(currentUserUID).child(ISBN10).child("Submitted").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String sSubmitted = null;
                if (snapshot.exists()) {
                    sSubmitted = Objects.requireNonNull(snapshot.getValue()).toString();
                }

                if (sSubmitted != null) {
                    if (sSubmitted.equals("1")) {
                        Toast.makeText(context.getApplicationContext(), "Book request already submitted", Toast.LENGTH_SHORT).show();
                    } else if (sSubmitted.equals("0")) {
                        Toast.makeText(context.getApplicationContext(), "Book request not submitted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Title", title);
                    map.put("ISBN10", ISBN10);
                    map.put("Submitted", 0);
                    databaseReference.child("bookRequests").child(currentUserUID).child(ISBN10).updateChildren(map);
                    Toast.makeText(context.getApplicationContext(), "Book request added. Submit it! ;)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void openEBook(Context context, String ISBN10) {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        databaseReference.child("eBooks").child(ISBN10).child("Book file link").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // File found
                String eBookFileLink = null;

                if (snapshot.exists()) {
                    eBookFileLink = Objects.requireNonNull(snapshot.getValue()).toString();
                    AppState.get().setEBookFilePathString(eBookFileLink);
                    Intent intent = new Intent(context, PdfViewActivity.class);
                    intent.putExtra("url", eBookFileLink);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void returnFinishedEBook(String ISBN10) {
        /**
         * Deletes the eBook request and increments the number of available eBooks in the inventory.
         */
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        String currentUID = AppState.get().getUserID();
        databaseReference.child("eBookBorrows").child(currentUID).child(ISBN10).removeValue();
        databaseReference.child("eBooks").child(ISBN10).child("Number of books").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String sNumOfBooks = Objects.requireNonNull(snapshot.getValue()).toString();
                    long lNumOfBooks = 0;
                    try {
                        lNumOfBooks = Long.parseLong(sNumOfBooks);
                    } catch (NumberFormatException nfe) {
                        // :)
                    }
                    databaseReference.child("eBooks").child(ISBN10).child("Number of books").setValue(lNumOfBooks + 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public ValueEventListener retrieveLateEBooksFromUsers() {
        /**
         * Deletes from the perspective of the librarian the borrowed eBooks whose due date expired.
         * Increments the number of eBooks in the inventory.
         */
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference databaseReference = AppState.get().getDatabaseReference();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String currentUID = userSnapshot.getKey();
                    for (DataSnapshot eBookBorrowSnapshot : userSnapshot.getChildren()) {
                        String ISBN10 = eBookBorrowSnapshot.getKey();
                        if (eBookBorrowSnapshot.child("End date").exists()) {
                            String sEndDate = Objects.requireNonNull(eBookBorrowSnapshot.child("End date").getValue()).toString();
                            BookRequest bookRequestForFunction = new BookRequest();
                            if (bookRequestForFunction.isBookRequestLate(sEndDate)) {
                                databaseReference.child("eBookBorrows").child(currentUID).child(ISBN10).removeValue();
                                databaseReference.child("eBooks").child(ISBN10).child("Number of books").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String sNumOfBooks = Objects.requireNonNull(snapshot.getValue()).toString();
                                            long lNumOfBooks = 0;
                                            try {
                                                lNumOfBooks = Long.parseLong(sNumOfBooks);
                                            } catch (NumberFormatException nfe) {
                                                System.out.println("Could not parse " + nfe);
                                            }
                                            databaseReference.child("eBooks").child(ISBN10).child("Number of books").setValue(lNumOfBooks + 1);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    public void setDownloadedFileLinkLibrarian(String ISBN10, TextView eBookPath) {
        /**
         * Sets the eBook file link to the textView.
         */
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        if (ISBN10 != null) {
            databaseReference.child("eBooks").child(ISBN10).child("Book file link").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.getValue() != null) {
                            String sEBookFilePath = Objects.requireNonNull(snapshot.getValue()).toString();
                            AppState.get().setEBookFilePathString(sEBookFilePath);
                            eBookPath.setText(sEBookFilePath);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    AppState.get().setEBookFilePathString(null);
                }
            });
        }
    }

    public void setDownloadedBookCoverLinkLibrarian(Context context, ImageView imageView) {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        Book currentBook = AppState.get().getCurrentBook();
        String realtimeDatabasePath = null;
        String ISBN10 = null;

        if (currentBook != null) {
            realtimeDatabasePath = currentBook.getRealtimeDatabasePath();
            ISBN10 = currentBook.getISBN10();
        }

        if (realtimeDatabasePath != null && ISBN10 != null) {
            databaseReference.child(realtimeDatabasePath).child(ISBN10).child("Book cover link").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.getValue() != null && imageView != null && context != null) {
                            String sEBookCoverUrl = Objects.requireNonNull(snapshot.getValue()).toString();
                            AppState.get().setEBookCoverPathString(sEBookCoverUrl);
                            Glide.with(context).load(sEBookCoverUrl).into(imageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    AppState.get().setEBookCoverPathString(null);
                    imageView.setImageResource(R.drawable.book_cover);
                }
            });
        }
    }

    public void setBookCoverReader(Context context, ImageView imageView, Book currentBook) {
        if (currentBook != null) {
            String eBookCoverPath = currentBook.getEBookCoverPath();
            if (context != null && eBookCoverPath != null && imageView != null) {
                Glide.with(context).load(eBookCoverPath).into(imageView);
            } else {
                if (imageView != null) {
                    imageView.setImageResource(R.drawable.book_cover);
                }
            }
        }
    }

    public ValueEventListener returnLateEBooks() {
        /**
         * Deletes from the perspective of the reader the borrowed eBooks whose due date expired.
         * Increments the number of eBooks in the inventory.
         */
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference databaseReference = AppState.get().getDatabaseReference();
                String currentUID = AppState.get().getUserID();
                if (snapshot.exists()) {
                    for (DataSnapshot eBookBorrowSnapshot : snapshot.getChildren()) {
                        String ISBN10 = eBookBorrowSnapshot.getKey();
                        if (eBookBorrowSnapshot.child("End date").exists()) {
                            String sEndDate = Objects.requireNonNull(eBookBorrowSnapshot.child("End date").getValue()).toString();
                            BookRequest bookRequestForFunction = new BookRequest();
                            if (bookRequestForFunction.isBookRequestLate(sEndDate)) {
                                databaseReference.child("eBookBorrows").child(currentUID).child(ISBN10).removeValue();
                                databaseReference.child("eBooks").child(ISBN10).child("Number of books").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String sNumOfBooks = Objects.requireNonNull(snapshot.getValue()).toString();
                                            long lNumOfBooks = 0;
                                            try {
                                                lNumOfBooks = Long.parseLong(sNumOfBooks);
                                            } catch (NumberFormatException nfe) {
                                                System.out.println("Could not parse " + nfe);
                                            }
                                            databaseReference.child("eBooks").child(ISBN10).child("Number of books").setValue(lNumOfBooks + 1);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    public void addReadingChallenge(long numOfBooksForReadingChallenge, ProgressBar progressBar, Button bAddReadingChallenge, Button bEditReadingChallenge, TextView message) {
        String currentUID = AppState.get().getUserID();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();

        databaseReference.child("users").child(currentUID).child("NumOfBooksForReadingChallenge").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child("users").child(currentUID).child("NumOfBooksForReadingChallenge").child("numOfBooks").setValue(numOfBooksForReadingChallenge);
                BookRequest bookRequestForFunction = new BookRequest();
                String currentYearString = bookRequestForFunction.getCurrentYearString();
                databaseReference.child("users").child(currentUID).child("NumOfBooksForReadingChallenge").child("year").setValue(currentYearString);
                message.setText("Your goal is of " + numOfBooksForReadingChallenge + " books");
                bAddReadingChallenge.setVisibility(View.INVISIBLE);
                bEditReadingChallenge.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                AppState.get().setAnnualReadingChallenge(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkIfUserHasReadingChallenge(TextView message, Button bAddReadingChallenge, Button bEditReadingChallenge, ProgressBar progressBar, ArrayAdapter<Book> gridAdapter, List<Book> books) {
        String currentUID = AppState.get().getUserID();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        databaseReference.child("users").child(currentUID).child("NumOfBooksForReadingChallenge").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BookRequest bookRequestForFunction = new BookRequest();
                String currentYearString = bookRequestForFunction.getCurrentYearString();
                boolean isReadingChallenge = false;
                if (snapshot.child("year").exists()) {
                    String yearFromDatabaseString = Objects.requireNonNull(snapshot.child("year").getValue()).toString();
                    if (currentYearString.equals(yearFromDatabaseString)) {
                        isReadingChallenge = true;
                    }
                }

                if (isReadingChallenge) {
                    databaseReference.child("users").child(currentUID).child("NumOfBooksForReadingChallenge").child("numOfBooks").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String sNumOfBooksForReadingChallenge = Objects.requireNonNull(snapshot.getValue()).toString();
                                long lNumOfBooksForReadingChallenge = 0;
                                try {
                                    lNumOfBooksForReadingChallenge = Long.parseLong(sNumOfBooksForReadingChallenge);
                                } catch (NumberFormatException nfe) {
                                    // :)
                                }
                                AppState.get().setReadingChallengeGoal(lNumOfBooksForReadingChallenge);
                                message.setText("Your goal is of " + sNumOfBooksForReadingChallenge + " books");
                            }
                            bAddReadingChallenge.setVisibility(View.INVISIBLE);
                            bEditReadingChallenge.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            AppState.get().setAnnualReadingChallenge(true);

                            AppState.get().initializeNumOfReadBooks();
                            books.clear();
                            databaseOperationHelper.displayBooksReadThisYear(gridAdapter, books, "eBooks", message, progressBar);
                            databaseOperationHelper.displayBooksReadThisYear(gridAdapter, books, "books", message, progressBar);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    message.setText("There is no reading challenge added for this year.");
                    bAddReadingChallenge.setVisibility(View.VISIBLE);
                    bEditReadingChallenge.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    AppState.get().setAnnualReadingChallenge(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void borrowEBookIfNotAlreadyBorrowed(Context context, String title, String author, String ISBN10) {
        DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        String currentUID = AppState.get().getUserID();

        // check if there are eBooks available in inventory
        databaseReference.child("eBooks").child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long lNumOfBooksAvailableInInventory = 0;
                    try {
                        String sNumOfBooksAvailableInInventory = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                        lNumOfBooksAvailableInInventory = Long.parseLong(sNumOfBooksAvailableInInventory);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Number format exception: " + nfe);
                    }

                    if (lNumOfBooksAvailableInInventory > 0) {
                        // check if eBook is not already borrowed by the reader
                        databaseReference.child("eBookBorrows").child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean isEBookBorrowed = snapshot.child(ISBN10).exists();
                                long numOfBorrowedEBooks = snapshot.getChildrenCount();
                                if (isEBookBorrowed) {
                                    Toast.makeText(context.getApplicationContext(), "E-Book is already borrowed.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // check if the reader borrowed more than 3 eBooks simultaneously
                                    if (numOfBorrowedEBooks >= 0 && numOfBorrowedEBooks < 3) {
                                        databaseOperationHelper.borrowEBook(ISBN10, title, author, 14);
                                        Toast.makeText(context.getApplicationContext(), "E-Book borrowed successfully! :D", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context.getApplicationContext(), "Maximum 3 eBooks can be borrowed simultaneously.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void borrowEBook(String ISBN10, String title, String author, int days) {
        /**
         * Function adds the eBook borrow made by the current user to the database.
         */

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String startDate = s.format(new Date(cal.getTimeInMillis()));
        cal.add(Calendar.DAY_OF_YEAR, days);
        String endDate = s.format(new Date(cal.getTimeInMillis()));

        Map<String, Object> map = new HashMap<>();
        map.put("Title", title);
        map.put("Author", author);
        map.put("Start date", startDate);
        map.put("End date", endDate);

        String currentUserUID = AppState.get().getUserID();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        databaseReference.child("eBookBorrows").child(currentUserUID).child(ISBN10).updateChildren(map);

        databaseReference.child("eBooks").child(ISBN10).child("Number of books").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String sNumOfBooks = Objects.requireNonNull(snapshot.getValue()).toString();
                int iNumOfBooks;

                try {
                    iNumOfBooks = Integer.parseInt(sNumOfBooks);
                    // if a book request has been accepted by the librarian, the number of
                    // books available to be borrowed decreases with one
                    if (iNumOfBooks > 0) {
                        databaseReference.child("eBooks").child(ISBN10).child("Number of books").setValue(iNumOfBooks - 1);
                    }
                } catch (NumberFormatException nfe) {
                    // :)
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deleteBookRequest(User currentUser, String ISBN10) {
        /**
         * Function deletes the book request made by the current user from the database.
         */
        String currentUserUID = currentUser.getUserID();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        databaseReference.child("bookRequests").child(currentUserUID).child(ISBN10).removeValue();
    }

    public void acceptRequest(int days) {
        /**
         * The function calculates the start date and the end date for a book borrow and adds the
         * start date and the end date to Firebase.
         * When the librarian accepts a book request, the number of available books in the inventory
         * decreases.
         */
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String startDate = s.format(new Date(cal.getTimeInMillis()));
        cal.add(Calendar.DAY_OF_YEAR, days);
        String endDate = s.format(new Date(cal.getTimeInMillis()));

        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        BookRequest currentBookRequest = AppState.get().getCurrentBookRequest();
        String currentReader = currentBookRequest.getCurrentUID();
        String currentISBN10 = currentBookRequest.getISBN10();

        databaseReference.child("bookRequests").child(currentReader).child(currentISBN10).child("Start date").setValue(startDate);
        databaseReference.child("bookRequests").child(currentReader).child(currentISBN10).child("End date").setValue(endDate);
        databaseReference.child("books").child(currentISBN10).child("Number of books").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String sNumOfBooks = Objects.requireNonNull(snapshot.getValue()).toString();
                int iNumOfBooks;

                try {
                    iNumOfBooks = Integer.parseInt(sNumOfBooks);
                    // if a book request has been accepted by the librarian, the number of
                    // books available to be borrowed decreases with one
                    if (iNumOfBooks > 0) {
                        databaseReference.child("books").child(currentISBN10).child("Number of books").setValue(iNumOfBooks - 1);
                    }
                } catch (NumberFormatException nfe) {
                    // :)
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkFineAndAccessForUsers() {
        /**
         Function checks if the current users have not returned books in time. If so, it blocks the
         accounts and sets the fines according to the number of late days.
         */
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();

        databaseReference.child("bookRequests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BookRequest bookRequestHandlerForFunctions = new BookRequest();
                long numDaysLateForBookRequest;
                long fineValuePerDay = 10;
                for (DataSnapshot user : snapshot.getChildren()) {
                    // checks all readers if they did not return books on time
                    String currentUID = user.getKey();
                    long maxNumDaysLateForBookRequest = 0;
                    for (DataSnapshot userBookRequest : user.getChildren()) {
                        if (userBookRequest.child("End date").exists()) {
                            String endDate = Objects.requireNonNull(userBookRequest.child("End date").getValue()).toString();
                            if (bookRequestHandlerForFunctions.isBookRequestLate(endDate)) {
                                numDaysLateForBookRequest = bookRequestHandlerForFunctions.getNumOfDaysLateForBookRequest(endDate);
                                // assign the fine according to the oldest book request
                                if (numDaysLateForBookRequest > maxNumDaysLateForBookRequest) {
                                    maxNumDaysLateForBookRequest = numDaysLateForBookRequest;
                                    long fine = maxNumDaysLateForBookRequest * fineValuePerDay;
                                    databaseReference.child("bookRequests").child(currentUID).child("Forbid access").setValue(1);
                                    databaseReference.child("bookRequests").child(currentUID).child("Fine").setValue(fine);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void putBookBackInInventory(String ISBN10) {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();

        // get number of books available and increment the number of books in the inventory
        // corresponding to ISBN10 with one
        databaseReference.child("books").child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Number of books").exists()) {
                    String sNumOfBooks = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                    int iNumOfBooks = 0;

                    try {
                        iNumOfBooks = Integer.parseInt(sNumOfBooks);
                    } catch (NumberFormatException nfe) {
                        // :)
                    }
                    databaseReference.child("books").child(ISBN10).child("Number of books").setValue(iNumOfBooks + 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void clearBookRequestReturnedInTime(Context context, String readerID, String ISBN10) {
        /**
         * Function clears the book request of a specific reader, corresponding to the book, whose
         * due date did not expire. The function also puts the book back into inventory.
         * @param readerID: String representing the ID of the reader, who requests a book borrow
         * @param ISBN10: String representing uniquely the book
         */
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();

        databaseReference.child("bookRequests").child(readerID).child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("End date").exists() &&
                        snapshot.child("Start date").exists()) {
                    // if the book has been requested and accepted by the librarian
                    String sEndDate = Objects.requireNonNull(snapshot.child("End date").getValue()).toString();
                    BookRequest bookRequestForFunction = new BookRequest();
                    if (!bookRequestForFunction.isBookRequestLate(sEndDate)) {
                        // if the book has not been returned and it's due date is not late
                        databaseReference.child("bookRequests").child(readerID).child(ISBN10).removeValue();
                        putBookBackInInventory(ISBN10);
                    } else {
                        Toast.makeText(context.getApplicationContext(), "Cannot delete late book request if the books are not returned and the fines paid.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context.getApplicationContext(), "Cannot delete book requests that have not been accepted by the librarian.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void clearUnreturnedLateBookRequestsForCurrentUser(Context context, String readerID) {
        /**
         * Function clears the book requests of a specific reader, corresponding to the books, that
         * have not yet been returned to the librarian in time. The function also puts these books
         * back into inventory.
         * @param readerID: String representing the ID of the reader, who requests a book borrow
         */
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        databaseReference.child("bookRequests").child(readerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean areBooksLate = false;
                for (DataSnapshot bookRequest : snapshot.getChildren()) {
                    if (bookRequest.child("End date").exists() &&
                            bookRequest.child("Start date").exists()) {
                        // if the book has been requested and accepted by the librarian
                        String sEndDate = Objects.requireNonNull(bookRequest.child("End date").getValue()).toString();
                        BookRequest bookRequestForFunction = new BookRequest();
                        if (bookRequestForFunction.isBookRequestLate(sEndDate)) {
                            // if the book hasn't been returned and it's due date is late
                            if (bookRequest.getKey() != null) {
                                // remove the late book request
                                String sISBN10 = bookRequest.getKey();
                                areBooksLate = true;
                                databaseReference.child("bookRequests").child(readerID).child(sISBN10).removeValue();
                                putBookBackInInventory(sISBN10);
                            }
                        }
                    }
                }
                if (!areBooksLate) {
                    // if there are no late returned books
                    Toast.makeText(context.getApplicationContext(), "There are no late returned books.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void clearFinesAndAllowAccessToAccount(String readerID) {
        /**
         * The librarian clears the fines for a specific reader and gives him access to his account
         * after returning late the borrowed books and paying the fine.
         * @param readerID: String representing the ID of the reader, who requests a book borrow
         */
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        databaseReference.child("bookRequests").child(readerID).child("Fine").setValue(0);
        databaseReference.child("bookRequests").child(readerID).child("Forbid access").removeValue();
    }

    public void submitRequestToDB(Context context, List<Book> books) {
        /**
         * The function submits the requested books and writes the submission to database.
         * The submit status of each book from the list is changed to "1".
         */
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        String currentUserUID = AppState.get().getUserID();

        for (int i = 0; i < books.size(); i++) {
            Book currentBook = books.get(i);
            String currentBookSubmitStatus = currentBook.getSubmitStatus();
            // checks if book is not submitted
            if (currentBookSubmitStatus.equals("0")) {
                currentBook.setSubmitStatus("1");
                books.set(i, currentBook);    // change submit status for book at position i
                String ISBN10 = currentBook.getISBN10();
                databaseReference.child("bookRequests").child(currentUserUID).child(ISBN10).child("Submitted").setValue(1);
            }
        }

        Toast.makeText(context, "Your book requests have been submitted.", Toast.LENGTH_SHORT).show();
    }

    public void removeBookWithGivenISBN10(List<Book> books, String ISBN10) {
        /**
         * The function deletes the not updated books from the arraylist.
         * @param ISBN10: Given ISBN10, the books that have not been submitted are deleted from the
         *              book list.
         */
        for (int i = 0; i < books.size(); i++) {
            Book currentBook = books.get(i);
            String currentBookISBN10 = currentBook.getISBN10();
            if (currentBookISBN10.equals(ISBN10)) {
                books.remove(currentBook);
            }
        }
    }

    public void setRatingToBook(Book book, float ratingValue) {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        String currentUID = AppState.get().getUserID();
        if (book != null) {
            String realtimeDatabasePath = book.getRealtimeDatabasePath();
            String ISBN10 = book.getISBN10();
            databaseReference.child(realtimeDatabasePath).child(ISBN10).child("Rating").child(currentUID).setValue(ratingValue);
            // calculate average rating for the book
            databaseReference.child(realtimeDatabasePath).child(ISBN10).child("Rating").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    float fRatingSum = 0;
                    long lNumOfRatings = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            String sDataSnapshot = dataSnapshot.getKey();
                            if (sDataSnapshot != null) {
                                if (!sDataSnapshot.equals("Average")) {
                                    String sRatingValue = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                    float fRatingValue = 0;
                                    try {
                                        fRatingValue = Float.parseFloat(sRatingValue);
                                        fRatingSum += fRatingValue;
                                        lNumOfRatings++;
                                    } catch (NumberFormatException nfe) {
                                        // :)
                                    }
                                }
                            }
                        }
                    }
                    databaseReference.child(realtimeDatabasePath).child(ISBN10).child("Rating").child("Average").setValue(fRatingSum / lNumOfRatings);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void addBookToRead(Context context, String ISBN10) {
        String currentUID = AppState.get().getUserID();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        BookRequest bookRequestForFunction = new BookRequest();
        String currentYearString = bookRequestForFunction.getCurrentYearString();
        // check if user is currently reading the book
        databaseReference.child("users").child(currentUID).child("CurrentlyReading").child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    databaseReference.child("users").child(currentUID).child("CurrentlyReading").child(ISBN10).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("users").child(currentUID).child("Read").child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(context.getApplicationContext(), "This book has already been read", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.child("users").child(currentUID).child("Read").child(ISBN10).setValue(currentYearString);
                    Toast.makeText(context.getApplicationContext(), "Book added to read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addBookToCurrentlyReading(Context context, String ISBN10) {
        String currentUID = AppState.get().getUserID();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        BookRequest bookRequestForFunction = new BookRequest();
        String currentYearString = bookRequestForFunction.getCurrentYearString();
        // check if book has already been read
        databaseReference.child("users").child(currentUID).child("Read").child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(context.getApplicationContext(), "This book has already been read.", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.child("users").child(currentUID).child("CurrentlyReading").child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(context.getApplicationContext(), "This book is already set as \"currently reading\".", Toast.LENGTH_SHORT).show();
                            } else {
                                databaseReference.child("users").child(currentUID).child("CurrentlyReading").child(ISBN10).setValue(currentYearString);
                                Toast.makeText(context.getApplicationContext(), "Book added to currently reading", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadBooks(ArrayAdapter<Book> adapter, List<Book> books, String databasePath) {
        /**
         * Function loads the books / ebooks from the given path.
         */
        if (!AppState.get().isAccountBlocked()) {
            DatabaseReference databaseReference = AppState.get().getDatabaseReference();
            DatabaseOperationHelper databaseOperationHelper = new DatabaseOperationHelper();
            databaseReference.child(databasePath).addChildEventListener(
                    databaseOperationHelper.displayBooks(adapter, books, databasePath)
            );
        }
    }

    public ValueEventListener displayFines(FinesAdapterActivity adapter, TextView tFines, List<BookRequest> bookRequests) {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot bookRequestSnapshot) {
                for (DataSnapshot bookRequest : bookRequestSnapshot.getChildren()) {
                    String ISBN10 = bookRequest.getKey();
                    if (ISBN10 != null) {
                        databaseReference.child("books").child(ISBN10).child("Title").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String ISBN10 = null;
                                    String sTitle = null;
                                    String startDate = null;
                                    String endDate = null;
                                    String requestStatus = null;
                                    String fines = null;
                                    BookRequest bookRequestObject;

                                    sTitle = Objects.requireNonNull(snapshot.getValue()).toString();

                                    ISBN10 = bookRequest.getKey();

                                    if (bookRequestSnapshot.child("Fine").exists()) {
                                        fines = Objects.requireNonNull(bookRequestSnapshot.child("Fine").getValue()).toString();
                                        tFines.setText(fines);
                                    } else {
                                        tFines.setText("0");
                                    }

                                    if (bookRequest.child("Start date").exists()) {
                                        startDate = Objects.requireNonNull(bookRequest.child("Start date").getValue()).toString();
                                    }

                                    if (bookRequest.child("End date").exists()) {
                                        endDate = Objects.requireNonNull(bookRequest.child("End date").getValue()).toString();
                                    }

                                    if (startDate != null && endDate != null) {
                                        requestStatus = "1";
                                    } else {
                                        requestStatus = "0";
                                    }

                                    if (requestStatus.equals("1")) {
                                        bookRequestObject = new BookRequest(ISBN10, sTitle, endDate);
                                        bookRequests.add(bookRequestObject);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    public void displayCurrentlyReadingBooks(ArrayAdapter<Book> adapter, List<Book> books, String realtimeDatabasePath) {
        String currentUID = AppState.get().getUserID();
        BookRequest bookRequestForFunction = new BookRequest();
        String currentYearString = bookRequestForFunction.getCurrentYearString();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        databaseReference.child("users").child(currentUID).child("CurrentlyReading").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    String ISBN10 = null;
                    String yearWhenTheBookHasBeenSetAsCurrentlyReading = null;

                    if (snapshot.getKey() != null) {
                        ISBN10 = snapshot.getKey();
                    }

                    if (snapshot.getValue() != null) {
                        yearWhenTheBookHasBeenSetAsCurrentlyReading = Objects.requireNonNull(snapshot.getValue()).toString();
                    }

                    if (ISBN10 != null && yearWhenTheBookHasBeenSetAsCurrentlyReading != null) {
                        if (yearWhenTheBookHasBeenSetAsCurrentlyReading.equals(currentYearString)) {
                            databaseReference.child(realtimeDatabasePath).child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String title = null;
                                    String author = null;
                                    String publisher = null;
                                    String publicationDate = null;
                                    String genre = null;
                                    String ISBN10 = null;
                                    String numOfBooks = null;
                                    String bookCoverLink = null;
                                    String bookFileLink = null;
                                    String averageRating = null;
                                    String userRating = null;

                                    if (snapshot.exists()) {

                                        // average rating
                                        if (snapshot.child("Rating").child("Average").exists()) {
                                            averageRating = Objects.requireNonNull(snapshot.child("Rating").child("Average").getValue()).toString();
                                        } else {
                                            averageRating = "0";
                                        }

                                        // user rating
                                        if (snapshot.child("Rating").child(currentUID).exists()) {
                                            userRating = Objects.requireNonNull(snapshot.child("Rating").child(currentUID).getValue()).toString();
                                        } else {
                                            userRating = "0";
                                        }

                                        if (snapshot.child("Title").exists()) {
                                            title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                                        }

                                        if (snapshot.child("Author").exists()) {
                                            author = Objects.requireNonNull(snapshot.child("Author").getValue()).toString();
                                        }

                                        if (snapshot.child("Publisher").exists()) {
                                            publisher = Objects.requireNonNull(snapshot.child("Publisher").getValue()).toString();
                                        }

                                        if (snapshot.child("Publication date").exists()) {
                                            publicationDate = Objects.requireNonNull(snapshot.child("Publication date").getValue()).toString();
                                        }

                                        if (snapshot.child("Genre").exists()) {
                                            genre = Objects.requireNonNull(snapshot.child("Genre").getValue()).toString();
                                        }

                                        ISBN10 = snapshot.getKey();

                                        if (snapshot.child("Number of books").exists()) {
                                            numOfBooks = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                                        }

                                        if (snapshot.child("Book cover link").exists()) {
                                            bookCoverLink = Objects.requireNonNull(snapshot.child("Book cover link").getValue()).toString();
                                        }

                                        if (snapshot.child("Book file link").exists()) {
                                            bookFileLink = Objects.requireNonNull(snapshot.child("Book file link").getValue()).toString();
                                        }

                                        if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && numOfBooks != null) {
                                            Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks, realtimeDatabasePath, bookCoverLink, bookFileLink, userRating, averageRating);
                                            books.add(book);
                                        }

                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // remove book corresponding to ISBN10 from list "books"
                int i = 0;
                String snapshotCurrentISBN10 = snapshot.getKey();
                while (i < books.size()) {
                    Book currentBook = books.get(i);
                    String currentISBN10 = currentBook.getISBN10();
                    if (currentISBN10.equals(snapshotCurrentISBN10)) {
                        books.remove(currentBook);
                        break;
                    } else {
                        i++;
                    }
                }

                if (snapshot.exists()) {
                    String ISBN10 = null;
                    String yearWhenTheBookHasBeenSetAsCurrentlyReading = null;

                    if (snapshot.getKey() != null) {
                        ISBN10 = snapshot.getKey();
                    }

                    if (snapshot.getValue() != null) {
                        yearWhenTheBookHasBeenSetAsCurrentlyReading = Objects.requireNonNull(snapshot.getValue()).toString();
                    }

                    if (ISBN10 != null && yearWhenTheBookHasBeenSetAsCurrentlyReading != null) {
                        if (yearWhenTheBookHasBeenSetAsCurrentlyReading.equals(currentYearString)) {
                            databaseReference.child(realtimeDatabasePath).child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String title = null;
                                    String author = null;
                                    String publisher = null;
                                    String publicationDate = null;
                                    String genre = null;
                                    String ISBN10 = null;
                                    String numOfBooks = null;
                                    String bookCoverLink = null;
                                    String bookFileLink = null;

                                    if (snapshot.exists()) {
                                        if (snapshot.child("Title").exists()) {
                                            title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                                        }

                                        if (snapshot.child("Author").exists()) {
                                            author = Objects.requireNonNull(snapshot.child("Author").getValue()).toString();
                                        }

                                        if (snapshot.child("Publisher").exists()) {
                                            publisher = Objects.requireNonNull(snapshot.child("Publisher").getValue()).toString();
                                        }

                                        if (snapshot.child("Publication date").exists()) {
                                            publicationDate = Objects.requireNonNull(snapshot.child("Publication date").getValue()).toString();
                                        }

                                        if (snapshot.child("Genre").exists()) {
                                            genre = Objects.requireNonNull(snapshot.child("Genre").getValue()).toString();
                                        }

                                        ISBN10 = snapshot.getKey();

                                        if (snapshot.child("Number of books").exists()) {
                                            numOfBooks = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                                        }

                                        if (snapshot.child("Book cover link").exists()) {
                                            bookCoverLink = Objects.requireNonNull(snapshot.child("Book cover link").getValue()).toString();
                                        }

                                        if (snapshot.child("Book file link").exists()) {
                                            bookFileLink = Objects.requireNonNull(snapshot.child("Book file link").getValue()).toString();
                                        }

                                        if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && numOfBooks != null) {
                                            Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks, realtimeDatabasePath, bookCoverLink, bookFileLink);
                                            books.add(book);
                                        }

                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                int i = 0;
                String snapshotCurrentISBN10 = snapshot.getKey();
                while (i < books.size()) {
                    Book currentBook = books.get(i);
                    String currentISBN10 = currentBook.getISBN10();
                    if (currentISBN10.equals(snapshotCurrentISBN10)) {
                        books.remove(currentBook);
                        break;
                    } else {
                        i++;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void displayBookRatingsForUser(ArrayAdapter<Book> adapter, List<Book> books, String realtimeDatabasePath) {
        String currentUID = AppState.get().getUserID();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        databaseReference.child(realtimeDatabasePath).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot booksSnapshot, @Nullable String previousChildName) {
                try {
                    if (booksSnapshot.getKey() != null) {
                        databaseReference.child("users").child(currentUID).child("Read").child(booksSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String ratingValue = null;
                                    String title = null;
                                    String author = null;
                                    String publisher = null;
                                    String publicationDate = null;
                                    String genre = null;
                                    String ISBN10;
                                    String numOfBooks = null;
                                    String bookCoverLink = null;
                                    String bookFileLink = null;

                                    if (booksSnapshot.child("Rating").child(currentUID).exists()) {
                                        ratingValue = Objects.requireNonNull(booksSnapshot.child("Rating").child(currentUID).getValue()).toString();
                                    } else {
                                        ratingValue = "0";
                                    }

                                    if (booksSnapshot.child("Title").exists()) {
                                        title = Objects.requireNonNull(booksSnapshot.child("Title").getValue()).toString();
                                    }

                                    if (booksSnapshot.child("Author").exists()) {
                                        author = Objects.requireNonNull(booksSnapshot.child("Author").getValue()).toString();
                                    }

                                    if (booksSnapshot.child("Publisher").exists()) {
                                        publisher = Objects.requireNonNull(booksSnapshot.child("Publisher").getValue()).toString();
                                    }

                                    if (booksSnapshot.child("Publication date").exists()) {
                                        publicationDate = Objects.requireNonNull(booksSnapshot.child("Publication date").getValue()).toString();
                                    }

                                    if (booksSnapshot.child("Genre").exists()) {
                                        genre = Objects.requireNonNull(booksSnapshot.child("Genre").getValue()).toString();
                                    }

                                    ISBN10 = booksSnapshot.getKey();

                                    if (booksSnapshot.child("Number of books").exists()) {
                                        numOfBooks = Objects.requireNonNull(booksSnapshot.child("Number of books").getValue()).toString();
                                    }

                                    if (booksSnapshot.child("Book cover link").exists()) {
                                        bookCoverLink = Objects.requireNonNull(booksSnapshot.child("Book cover link").getValue()).toString();
                                    }

                                    if (booksSnapshot.child("Book file link").exists()) {
                                        bookFileLink = Objects.requireNonNull(booksSnapshot.child("Book file link").getValue()).toString();
                                    }

                                    if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && numOfBooks != null) {
                                        Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks, realtimeDatabasePath, bookCoverLink, bookFileLink, ratingValue);
                                        books.add(book);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    String ratingValue = null;
                    String title = null;
                    String author = null;
                    String publisher = null;
                    String publicationDate = null;
                    String genre = null;
                    String ISBN10;
                    String numOfBooks = null;
                    String bookCoverLink = null;
                    String bookFileLink = null;

                    // remove book corresponding to ISBN10 from list "books"
                    int i = 0;
                    String snapshotCurrentISBN10 = snapshot.getKey();
                    while (i < books.size()) {
                        Book currentBook = books.get(i);
                        String currentISBN10 = currentBook.getISBN10();
                        if (currentISBN10.equals(snapshotCurrentISBN10)) {
                            books.remove(currentBook);
                            break;
                        } else {
                            i++;
                        }
                    }

                    if (snapshot.child("Rating").child(currentUID).exists()) {
                        ratingValue = Objects.requireNonNull(snapshot.child("Rating").child(currentUID).getValue()).toString();
                    } else {
                        ratingValue = "0";
                    }

                    if (snapshot.child("Title").exists()) {
                        title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                    }

                    if (snapshot.child("Author").exists()) {
                        author = Objects.requireNonNull(snapshot.child("Author").getValue()).toString();
                    }

                    if (snapshot.child("Publisher").exists()) {
                        publisher = Objects.requireNonNull(snapshot.child("Publisher").getValue()).toString();
                    }

                    if (snapshot.child("Publication date").exists()) {
                        publicationDate = Objects.requireNonNull(snapshot.child("Publication date").getValue()).toString();
                    }

                    if (snapshot.child("Genre").exists()) {
                        genre = Objects.requireNonNull(snapshot.child("Genre").getValue()).toString();
                    }

                    ISBN10 = snapshot.getKey();

                    if (snapshot.child("Number of books").exists()) {
                        numOfBooks = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                    }

                    if (snapshot.child("Book cover link").exists()) {
                        bookCoverLink = Objects.requireNonNull(snapshot.child("Book cover link").getValue()).toString();
                    }

                    if (snapshot.child("Book file link").exists()) {
                        bookFileLink = Objects.requireNonNull(snapshot.child("Book file link").getValue()).toString();
                    }

                    if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && numOfBooks != null) {
                        Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks, realtimeDatabasePath, bookCoverLink, bookFileLink, ratingValue);
                        books.add(book);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    // :D
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // remove book corresponding to ISBN10 from list "books"
                int i = 0;
                String snapshotCurrentISBN10 = snapshot.getKey();
                while (i < books.size()) {
                    Book currentBook = books.get(i);
                    String currentISBN10 = currentBook.getISBN10();
                    if (currentISBN10.equals(snapshotCurrentISBN10)) {
                        books.remove(currentBook);
                        break;
                    } else {
                        i++;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void displayBooksReadThisYear(ArrayAdapter<Book> adapter, List<Book> books, String realtimeDatabasePath, TextView tMessage, ProgressBar progressBar) {
        String currentUID = AppState.get().getUserID();
        BookRequest bookRequestForFunction = new BookRequest();
        String currentYearString = bookRequestForFunction.getCurrentYearString();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        databaseReference.child("users").child(currentUID).child("Read").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String ISBN10 = dataSnapshot.getKey();
                    String yearWhenTheBookWasRead = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    if (yearWhenTheBookWasRead.equals(currentYearString) && ISBN10 != null) {
                        databaseReference.child(realtimeDatabasePath).child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String title = null;
                                String author = null;
                                String publisher = null;
                                String publicationDate = null;
                                String genre = null;
                                String ISBN10 = null;
                                String numOfBooks = null;
                                String averageRating = null;
                                String userRating = null;
                                String bookCoverLink = null;
                                String bookFileLink = null;

                                if (snapshot.exists()) {
                                    // average rating
                                    if (snapshot.child("Rating").child("Average").exists()) {
                                        averageRating = Objects.requireNonNull(snapshot.child("Rating").child("Average").getValue()).toString();
                                    } else {
                                        averageRating = "0";
                                    }

                                    // user rating
                                    if (snapshot.child("Rating").child(currentUID).exists()) {
                                        userRating = Objects.requireNonNull(snapshot.child("Rating").child(currentUID).getValue()).toString();
                                    } else {
                                        userRating = "0";
                                    }

                                    if (snapshot.child("Title").exists()) {
                                        title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                                    }

                                    if (snapshot.child("Author").exists()) {
                                        author = Objects.requireNonNull(snapshot.child("Author").getValue()).toString();
                                    }

                                    if (snapshot.child("Publisher").exists()) {
                                        publisher = Objects.requireNonNull(snapshot.child("Publisher").getValue()).toString();
                                    }

                                    if (snapshot.child("Publication date").exists()) {
                                        publicationDate = Objects.requireNonNull(snapshot.child("Publication date").getValue()).toString();
                                    }

                                    if (snapshot.child("Genre").exists()) {
                                        genre = Objects.requireNonNull(snapshot.child("Genre").getValue()).toString();
                                    }

                                    ISBN10 = snapshot.getKey();

                                    if (snapshot.child("Number of books").exists()) {
                                        numOfBooks = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                                    }

                                    if (snapshot.child("Book cover link").exists()) {
                                        bookCoverLink = Objects.requireNonNull(snapshot.child("Book cover link").getValue()).toString();
                                    }

                                    if (snapshot.child("Book file link").exists()) {
                                        bookFileLink = Objects.requireNonNull(snapshot.child("Book file link").getValue()).toString();
                                    }

                                    if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && numOfBooks != null) {
                                        Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks, realtimeDatabasePath, bookCoverLink, bookFileLink, userRating, averageRating);

                                        AppState.get().incrementNumOfReadBooks();
                                        long lReadingChallengeGoal = AppState.get().getReadingChallengeGoal();
                                        long lNumOfReadBooks = AppState.get().getNumOfReadBooks();
                                        int progressValue = (int) lNumOfReadBooks * 100 / (int) lReadingChallengeGoal;
                                        tMessage.setText("You've read " + lNumOfReadBooks + " out of " + lReadingChallengeGoal + " books.");
                                        progressBar.setProgress(progressValue);

                                        books.add(book);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public ChildEventListener displayBooks(ArrayAdapter<Book> adapter, List<Book> books, String realtimeDatabasePath) {
        /**
         * Function displays the books that are currently in the database from the perspective of
         * both the librarian and user account. The listener reads the books from the database and
         * adds all books to a list of books, that is displayed using the book adapter for the
         * librarian.
         */
        String currentUID = AppState.get().getUserID();
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    String title = null;
                    String author = null;
                    String publisher = null;
                    String publicationDate = null;
                    String genre = null;
                    String ISBN10;
                    String numOfBooks = null;
                    String bookCoverLink = null;
                    String bookFileLink = null;
                    String averageRating = null;
                    String userRating = null;

                    // average rating
                    if (snapshot.child("Rating").child("Average").exists()) {
                        averageRating = Objects.requireNonNull(snapshot.child("Rating").child("Average").getValue()).toString();
                    } else {
                        averageRating = "0";
                    }

                    // user rating
                    if (snapshot.child("Rating").child(currentUID).exists()) {
                        userRating = Objects.requireNonNull(snapshot.child("Rating").child(currentUID).getValue()).toString();
                    } else {
                        userRating = "0";
                    }

                    if (snapshot.child("Title").exists()) {
                        title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                    }

                    if (snapshot.child("Author").exists()) {
                        author = Objects.requireNonNull(snapshot.child("Author").getValue()).toString();
                    }

                    if (snapshot.child("Publisher").exists()) {
                        publisher = Objects.requireNonNull(snapshot.child("Publisher").getValue()).toString();
                    }

                    if (snapshot.child("Publication date").exists()) {
                        publicationDate = Objects.requireNonNull(snapshot.child("Publication date").getValue()).toString();
                    }

                    if (snapshot.child("Genre").exists()) {
                        genre = Objects.requireNonNull(snapshot.child("Genre").getValue()).toString();
                    }

                    ISBN10 = snapshot.getKey();

                    if (snapshot.child("Number of books").exists()) {
                        numOfBooks = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                    }

                    if (snapshot.child("Book cover link").exists()) {
                        bookCoverLink = Objects.requireNonNull(snapshot.child("Book cover link").getValue()).toString();
                    }

                    if (snapshot.child("Book file link").exists()) {
                        bookFileLink = Objects.requireNonNull(snapshot.child("Book file link").getValue()).toString();
                    }

                    if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && numOfBooks != null) {
                        Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks, realtimeDatabasePath, bookCoverLink, bookFileLink, userRating, averageRating);
                        books.add(book);
                    }

                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    String title = null;
                    String author = null;
                    String publisher = null;
                    String publicationDate = null;
                    String genre = null;
                    String ISBN10 = null;
                    String numOfBooks = null;
                    String bookCoverLink = null;
                    String bookFileLink = null;
                    String averageRating = null;
                    String userRating = null;

                    // remove book corresponding to ISBN10 from list "books"
                    int i = 0;
                    String snapshotCurrentISBN10 = snapshot.getKey();
                    while (i < books.size()) {
                        Book currentBook = books.get(i);
                        String currentISBN10 = currentBook.getISBN10();
                        if (currentISBN10.equals(snapshotCurrentISBN10)) {
                            books.remove(currentBook);
                            break;
                        } else {
                            i++;
                        }
                    }

                    // average rating
                    if (snapshot.child("Rating").child("Average").exists()) {
                        averageRating = Objects.requireNonNull(snapshot.child("Rating").child("Average").getValue()).toString();
                    } else {
                        averageRating = "0";
                    }

                    // user rating
                    if (snapshot.child("Rating").child(currentUID).exists()) {
                        userRating = Objects.requireNonNull(snapshot.child("Rating").child(currentUID).getValue()).toString();
                    } else {
                        userRating = "0";
                    }

                    if (snapshot.child("Title").exists()) {
                        title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                    }

                    if (snapshot.child("Author").exists()) {
                        author = Objects.requireNonNull(snapshot.child("Author").getValue()).toString();
                    }

                    if (snapshot.child("Publisher").exists()) {
                        publisher = Objects.requireNonNull(snapshot.child("Publisher").getValue()).toString();
                    }

                    if (snapshot.child("Publication date").exists()) {
                        publicationDate = Objects.requireNonNull(snapshot.child("Publication date").getValue()).toString();
                    }

                    if (snapshot.child("Genre").exists()) {
                        genre = Objects.requireNonNull(snapshot.child("Genre").getValue()).toString();
                    }

                    ISBN10 = snapshot.getKey();

                    if (snapshot.child("Number of books").exists()) {
                        numOfBooks = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                    }

                    if (snapshot.child("Book cover link").exists()) {
                        bookCoverLink = Objects.requireNonNull(snapshot.child("Book cover link").getValue()).toString();
                    }

                    if (snapshot.child("Book file link").exists()) {
                        bookFileLink = Objects.requireNonNull(snapshot.child("Book file link").getValue()).toString();
                    }

                    if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && numOfBooks != null) {
                        Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks, realtimeDatabasePath, bookCoverLink, bookFileLink, userRating, averageRating);
                        books.add(book);
                    }

                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    // :D
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    // remove book corresponding to ISBN10 from list "books"
                    int i = 0;
                    String snapshotCurrentISBN10 = snapshot.getKey();
                    while (i < books.size()) {
                        Book currentBook = books.get(i);
                        String currentISBN10 = currentBook.getISBN10();
                        if (currentISBN10.equals(snapshotCurrentISBN10)) {
                            books.remove(currentBook);
                            break;
                        } else {
                            i++;
                        }
                    }
                } catch (Exception e) {
                    // :)
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                adapter.notifyDataSetChanged();
            }
        };
    }

    public void displayBorrowedEBooks(ArrayAdapter<Book> adapter, List<Book> eBooks) {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        String currentUID = AppState.get().getUserID();
        databaseReference.child("eBookBorrows").child(currentUID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot borrowedEBookSnapshot, @Nullable String previousChildName) {
                try {
                    String ISBN10 = borrowedEBookSnapshot.getKey();

                    if (ISBN10 != null) {
                        databaseReference.child("eBooks").child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String title = null;
                                    String author = null;
                                    String publisher = null;
                                    String publicationDate = null;
                                    String genre = null;
                                    String numOfBooks = null;
                                    String bookCoverLink = null;
                                    String bookFileLink = null;
                                    String averageRating = null;
                                    String userRating = null;
                                    String ISBN10 = borrowedEBookSnapshot.getKey();

                                    // average rating
                                    if (snapshot.child("Rating").child("Average").exists()) {
                                        averageRating = Objects.requireNonNull(snapshot.child("Rating").child("Average").getValue()).toString();
                                    } else {
                                        averageRating = "0";
                                    }

                                    // user rating
                                    if (snapshot.child("Rating").child(currentUID).exists()) {
                                        userRating = Objects.requireNonNull(snapshot.child("Rating").child(currentUID).getValue()).toString();
                                    } else {
                                        userRating = "0";
                                    }

                                    if (snapshot.child("Title").exists()) {
                                        title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                                    }

                                    if (snapshot.child("Author").exists()) {
                                        author = Objects.requireNonNull(snapshot.child("Author").getValue()).toString();
                                    }

                                    if (snapshot.child("Publisher").exists()) {
                                        publisher = Objects.requireNonNull(snapshot.child("Publisher").getValue()).toString();
                                    }

                                    if (snapshot.child("Publication date").exists()) {
                                        publicationDate = Objects.requireNonNull(snapshot.child("Publication date").getValue()).toString();
                                    }

                                    if (snapshot.child("Genre").exists()) {
                                        genre = Objects.requireNonNull(snapshot.child("Genre").getValue()).toString();
                                    }

                                    if (snapshot.child("Number of books").exists()) {
                                        numOfBooks = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                                    }

                                    if (snapshot.child("Book cover link").exists()) {
                                        bookCoverLink = Objects.requireNonNull(snapshot.child("Book cover link").getValue()).toString();
                                    }

                                    if (snapshot.child("Book file link").exists()) {
                                        bookFileLink = Objects.requireNonNull(snapshot.child("Book file link").getValue()).toString();
                                    }

                                    if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && numOfBooks != null) {
                                        Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks, "eBooks", bookCoverLink, bookFileLink, userRating, averageRating);
                                        eBooks.add(book);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot borrowedEBookSnapshot, @Nullable String previousChildName) {
                try {
                    String ISBN10 = borrowedEBookSnapshot.getKey();

                    if (ISBN10 != null) {
                        // remove book corresponding to ISBN10 from list "eBooks"
                        int i = 0;
                        while (i < eBooks.size()) {
                            Book currentBook = eBooks.get(i);
                            String currentISBN10 = currentBook.getISBN10();
                            if (currentISBN10.equals(ISBN10)) {
                                eBooks.remove(currentBook);
                                break;
                            } else {
                                i++;
                            }
                        }

                        databaseReference.child("eBooks").child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String title = null;
                                    String author = null;
                                    String publisher = null;
                                    String publicationDate = null;
                                    String genre = null;
                                    String numOfBooks = null;
                                    String bookCoverLink = null;
                                    String bookFileLink = null;
                                    String averageRating = null;
                                    String userRating = null;
                                    String ISBN10 = borrowedEBookSnapshot.getKey();

                                    // average rating
                                    if (snapshot.child("Rating").child("Average").exists()) {
                                        averageRating = Objects.requireNonNull(snapshot.child("Rating").child("Average").getValue()).toString();
                                    } else {
                                        averageRating = "0";
                                    }

                                    // user rating
                                    if (snapshot.child("Rating").child(currentUID).exists()) {
                                        userRating = Objects.requireNonNull(snapshot.child("Rating").child(currentUID).getValue()).toString();
                                    } else {
                                        userRating = "0";
                                    }

                                    if (snapshot.child("Title").exists()) {
                                        title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                                    }

                                    if (snapshot.child("Author").exists()) {
                                        author = Objects.requireNonNull(snapshot.child("Author").getValue()).toString();
                                    }

                                    if (snapshot.child("Publisher").exists()) {
                                        publisher = Objects.requireNonNull(snapshot.child("Publisher").getValue()).toString();
                                    }

                                    if (snapshot.child("Publication date").exists()) {
                                        publicationDate = Objects.requireNonNull(snapshot.child("Publication date").getValue()).toString();
                                    }

                                    if (snapshot.child("Genre").exists()) {
                                        genre = Objects.requireNonNull(snapshot.child("Genre").getValue()).toString();
                                    }

                                    if (snapshot.child("Number of books").exists()) {
                                        numOfBooks = Objects.requireNonNull(snapshot.child("Number of books").getValue()).toString();
                                    }

                                    if (snapshot.child("Book cover link").exists()) {
                                        bookCoverLink = Objects.requireNonNull(snapshot.child("Book cover link").getValue()).toString();
                                    }

                                    if (snapshot.child("Book file link").exists()) {
                                        bookFileLink = Objects.requireNonNull(snapshot.child("Book file link").getValue()).toString();
                                    }

                                    if (title != null && author != null && publisher != null && publicationDate != null && genre != null && ISBN10 != null && numOfBooks != null) {
                                        Book book = new Book(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks, "eBooks", bookCoverLink, bookFileLink, userRating, averageRating);
                                        eBooks.add(book);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot eBookBorrowSnapshot) {
                // remove book corresponding to ISBN10 from list "books"
                int i = 0;
                String snapshotCurrentISBN10 = eBookBorrowSnapshot.getKey();
                while (i < eBooks.size()) {
                    Book currentBook = eBooks.get(i);
                    String currentISBN10 = currentBook.getISBN10();
                    if (currentISBN10.equals(snapshotCurrentISBN10)) {
                        eBooks.remove(currentBook);
                        adapter.notifyDataSetChanged();
                        break;
                    } else {
                        i++;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot eBookBorrowSnapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public ChildEventListener displayBookRequests(DatabaseReference databaseReference, List<BookRequest> bookRequests, BookRequestsAdapterLibrarianActivity adapter) {
        /**
         * Function displays the book requests that are currently in the database.
         * The listener reads the book requests from the database and adds them to a list, that is
         * displayed using the book request adapter for the librarian.
         */
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot bookRequestSnapshot, @Nullable String previousChildName) {
                try {
                    if (bookRequestSnapshot.exists()) {
                        String sCurrentUserID = bookRequestSnapshot.getKey();
                        if (sCurrentUserID != null) {
                            for (DataSnapshot bookRequest : bookRequestSnapshot.getChildren()) {
                                String sFine;
                                String startDate = null;
                                String endDate = null;
                                String requestStatus;
                                String sSubmitted = null;
                                String title = null;
                                BookRequest bookRequestObject;

                                String ISBN10 = bookRequest.getKey();

                                if (bookRequest.child("Submitted").exists()) {
                                    sSubmitted = Objects.requireNonNull(bookRequest.child("Submitted").getValue()).toString();
                                }

                                if (sSubmitted != null) {
                                    if (sSubmitted.equals("1")) {

                                        if (bookRequestSnapshot.child("Fine").exists()) {
                                            sFine = Objects.requireNonNull(bookRequestSnapshot.child("Fine").getValue()).toString();
                                        } else {
                                            sFine = "0";
                                            databaseReference.child("bookRequests").child(sCurrentUserID).child("Fine").setValue(0);
                                        }

                                        if (bookRequest.child("Start date").exists()) {
                                            startDate = Objects.requireNonNull(bookRequest.child("Start date").getValue()).toString();
                                        }

                                        if (bookRequest.child("End date").exists()) {
                                            endDate = Objects.requireNonNull(bookRequest.child("End date").getValue()).toString();
                                        }

                                        if (bookRequest.child("Title").exists()) {
                                            title = Objects.requireNonNull(bookRequest.child("Title").getValue()).toString();
                                        }

                                        if (startDate != null && endDate != null) {
                                            // the submitted book has been approved by the librarian
                                            requestStatus = "1";
                                        } else {
                                            // the submitted book has not been approved by the librarian
                                            requestStatus = "0";
                                        }

                                        if (ISBN10 != null && title != null) {
                                            bookRequestObject = new BookRequest(sFine, ISBN10, title, sCurrentUserID, sSubmitted, requestStatus, endDate);
                                            if (!bookRequests.contains(bookRequestObject)) {
                                                bookRequests.add(bookRequestObject);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot bookRequestSnapshot, @Nullable String previousChildName) {
                try {
                    String sCurrentUserID;

                    // remove book requests of current user from list "bookRequests"
                    int i = 0;
                    String snapshotCurrentUID = bookRequestSnapshot.getKey();
                    while (i < bookRequests.size()) {
                        BookRequest currentBookRequest = bookRequests.get(i);
                        String currentUID = currentBookRequest.getCurrentUID();
                        if (currentUID.equals(snapshotCurrentUID)) {
                            bookRequests.remove(currentBookRequest);
                            adapter.notifyDataSetChanged();
                        } else {
                            i++;
                        }
                    }

                    if (bookRequestSnapshot.exists()) {
                        sCurrentUserID = bookRequestSnapshot.getKey();
                        if (sCurrentUserID != null) {
                            for (DataSnapshot bookRequest : bookRequestSnapshot.getChildren()) {
                                String sFine;
                                String startDate = null;
                                String endDate = null;
                                String requestStatus;
                                String sSubmitted = null;
                                String title = null;
                                BookRequest bookRequestObject;

                                String ISBN10 = bookRequest.getKey();

                                if (bookRequest.child("Submitted").exists()) {
                                    sSubmitted = Objects.requireNonNull(bookRequest.child("Submitted").getValue()).toString();
                                }

                                if (sSubmitted != null) {
                                    if (sSubmitted.equals("1")) {

                                        if (bookRequestSnapshot.child("Fine").exists()) {
                                            sFine = Objects.requireNonNull(bookRequestSnapshot.child("Fine").getValue()).toString();
                                        } else {
                                            sFine = "0";
                                            databaseReference.child("bookRequests").child(sCurrentUserID).child("Fine").setValue(0);
                                        }

                                        if (bookRequest.child("Start date").exists()) {
                                            startDate = Objects.requireNonNull(bookRequest.child("Start date").getValue()).toString();
                                        }

                                        if (bookRequest.child("End date").exists()) {
                                            endDate = Objects.requireNonNull(bookRequest.child("End date").getValue()).toString();
                                        }

                                        if (bookRequest.child("Title").exists()) {
                                            title = Objects.requireNonNull(bookRequest.child("Title").getValue()).toString();
                                        }

                                        if (startDate != null && endDate != null) {
                                            // the submitted book has been approved by the librarian
                                            requestStatus = "1";
                                        } else {
                                            // the submitted book has not been approved by the librarian
                                            requestStatus = "0";
                                        }

                                        if (ISBN10 != null && title != null) {
                                            bookRequestObject = new BookRequest(sFine, ISBN10, title, sCurrentUserID, sSubmitted, requestStatus, endDate);
                                            if (!bookRequests.contains(bookRequestObject)) {
                                                bookRequests.add(bookRequestObject);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }
                            // if current user doesn't have book requests
                            if (bookRequestSnapshot.getChildrenCount() == 0) {
                                // delete the user from "bookRequests" database
                                databaseReference.child("bookRequests").child(sCurrentUserID).removeValue();
                            }
                        }
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    // remove book requests of current user from list "bookRequests"
                    int i = 0;
                    String snapshotCurrentUID = snapshot.getKey();
                    while (i < bookRequests.size()) {
                        BookRequest currentBookRequest = bookRequests.get(i);
                        String currentUID = currentBookRequest.getCurrentUID();
                        if (currentUID.equals(snapshotCurrentUID)) {
                            bookRequests.remove(currentBookRequest);
                            adapter.notifyDataSetChanged();
                        } else {
                            i++;
                        }
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    public ChildEventListener displayIfRequestsSubmitted(BookRequestsAdapterActivity adapter, List<Book> books) {
        /**
         * Function displays if the book requests are submitted or not.
         */
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot bookRequestSnapshot, @Nullable String previousChildName) {
                try {
                    // if book request is submitted but not accepted by librarian
                    if (!bookRequestSnapshot.child("Start date").exists() && !bookRequestSnapshot.child("End date").exists()) {
                        String bookRequestSnapshotKey = bookRequestSnapshot.getKey();
                        if (bookRequestSnapshotKey != null) {
                            if (!bookRequestSnapshotKey.equals("Fine") && !bookRequestSnapshotKey.equals("Forbid access")) {
                                String ISBN10 = bookRequestSnapshotKey;
                                databaseReference.child("books").child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String title = null;
                                            String ISBN10 = null;
                                            String submittedBook = null;

                                            if (snapshot.child("Title").exists()) {
                                                title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                                            }

                                            if (bookRequestSnapshot.child("Submitted").exists()) {
                                                submittedBook = Objects.requireNonNull(bookRequestSnapshot.child("Submitted").getValue()).toString();
                                            }

                                            ISBN10 = bookRequestSnapshot.getKey();

                                            if (title != null && ISBN10 != null) {
                                                Book book = new Book(title, ISBN10);
                                                book.setSubmitStatus(submittedBook);
                                                books.add(book);
                                            }

                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot bookRequestSnapshot, @Nullable String previousChildName) {
                try {
                    // remove book requests that changed from "books" list
                    int i = 0;
                    String bookRequestSnapshotKey = bookRequestSnapshot.getKey();
                    if (bookRequestSnapshotKey != null) {
                        if (!bookRequestSnapshotKey.equals("Fine") && !bookRequestSnapshotKey.equals("Forbid access")) {
                            String snapshotCurrentISBN10 = bookRequestSnapshotKey;
                            while (i < books.size()) {
                                Book currentBook = books.get(i);
                                String currentISBN10 = currentBook.getISBN10();
                                if (currentISBN10.equals(snapshotCurrentISBN10)) {
                                    books.remove(currentBook);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    i++;
                                }
                            }
                        }
                    }

                    // if book request is submitted but not accepted by librarian
                    if (!bookRequestSnapshot.child("Start date").exists() && !bookRequestSnapshot.child("End date").exists()) {
                        bookRequestSnapshotKey = bookRequestSnapshot.getKey();
                        if (bookRequestSnapshotKey != null) {
                            if (!bookRequestSnapshotKey.equals("Fine") && !bookRequestSnapshotKey.equals("Forbid access")) {
                                String ISBN10 = bookRequestSnapshotKey;
                                databaseReference.child("books").child(ISBN10).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String title = null;
                                            String ISBN10 = null;
                                            String submittedBook = null;

                                            if (snapshot.child("Title").exists()) {
                                                title = Objects.requireNonNull(snapshot.child("Title").getValue()).toString();
                                            }

                                            if (bookRequestSnapshot.child("Submitted").exists()) {
                                                submittedBook = Objects.requireNonNull(bookRequestSnapshot.child("Submitted").getValue()).toString();
                                            }

                                            ISBN10 = bookRequestSnapshot.getKey();

                                            if (title != null && ISBN10 != null) {
                                                Book book = new Book(title, ISBN10);
                                                book.setSubmitStatus(submittedBook);
                                                books.add(book);
                                            }

                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    // :)
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot bookRequestSnapshot) {
                // remove book requests that changed from "books" list
                int i = 0;
                String snapshotCurrentISBN10 = bookRequestSnapshot.getKey();
                while (i < books.size()) {
                    Book currentBook = books.get(i);
                    String currentISBN10 = currentBook.getISBN10();
                    if (currentISBN10.equals(snapshotCurrentISBN10)) {
                        books.remove(currentBook);
                        adapter.notifyDataSetChanged();
                    } else {
                        i++;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot bookRequestSnapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                adapter.notifyDataSetChanged();
            }
        };
    }
}
