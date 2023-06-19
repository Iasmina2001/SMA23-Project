package com.sma.proiect;

import android.net.Uri;
import com.google.firebase.database.DatabaseReference;
import com.sma.proiect.user.User;


public class AppState {

    private static String userID;
    private static AppState singletonObject;
    private DatabaseReference databaseReference;    // reference to Firebase used for reading and writing data
    private User currentUser;    // current user to be edited or deleted
    private Book currentBook;
    private BookRequest currentBookRequest;
    private boolean blockedAccount;
    private boolean isAnnualReadingChallenge;
    private long numOfBooksForAnnualReadingChallenge;
    private String eBookCoverPathString;
    private String eBookFilePathString;
    private Uri downloadedEBookFilePathUri;
    private Uri browsedEBookCoverPathUri;
    private Uri browsedEBookFilePathUri;
    private Uri downloadedEBookCoverPathUri;
    private long numOfReadBooks = 0;
    private long readingChallengeGoal = 0;

    public static synchronized AppState get() {
        if (singletonObject == null) {
            singletonObject = new AppState();
        }
        return singletonObject;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public Book getCurrentBook() {
        return this.currentBook;
    }
    public void setCurrentBook(Book currentBook) {
        this.currentBook = currentBook;
    }

    public BookRequest getCurrentBookRequest() { return this.currentBookRequest; }
    public void setCurrentBookRequest(BookRequest currentBookRequest) { this.currentBookRequest = currentBookRequest; }

    public User getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getUserID() { return userID; }
    public void setUserId(String uid) {
        userID = uid;
    }

    public boolean isAccountBlocked() { return this.blockedAccount; }
    public void blockAccount(boolean blockedAccount) { this.blockedAccount = blockedAccount; }

    public Uri getDownloadedEBookFilePathUri() { return this.downloadedEBookFilePathUri; }
    public void setDownloadedEBookFilePathUri(Uri uri) { this.downloadedEBookFilePathUri = uri; }

    public Uri getBrowsedEBookFilePathUri() { return this.browsedEBookFilePathUri; }
    public void setBrowsedEBookFilePathUri(Uri browsedEBookFilePathUri) { this.browsedEBookFilePathUri = browsedEBookFilePathUri; }

    public Uri getDownloadedEBookCoverPathUri() { return this.downloadedEBookCoverPathUri; }
    public void setDownloadedEBookCoverPathUri(Uri downloadedEBookCoverPathUri) { this.downloadedEBookCoverPathUri = downloadedEBookCoverPathUri; }

    public Uri getBrowsedEBookCoverPathUri() { return this.browsedEBookCoverPathUri; }
    public void setBrowsedEBookCoverPathUri(Uri eBookCoverPath) { this.browsedEBookCoverPathUri = eBookCoverPath; }

    public boolean isAnnualReadingChallenge() { return this.isAnnualReadingChallenge; }
    public void setAnnualReadingChallenge(boolean annualReadingChallenge) { this.isAnnualReadingChallenge = annualReadingChallenge; }

    public long getNumOfBooksForAnnualReadingChallenge() { return this.numOfBooksForAnnualReadingChallenge; }

    public String getEBookCoverPathString() { return this.eBookCoverPathString; }
    public void setEBookCoverPathString(String eBookCoverPathString) { this.eBookCoverPathString = eBookCoverPathString; }

    public String getEBookFilePathString() { return this.eBookFilePathString; }
    public void setEBookFilePathString(String eBookFilePathString) { this.eBookFilePathString = eBookFilePathString; }

    public void initializeNumOfReadBooks() { this.numOfReadBooks = 0; }
    public void incrementNumOfReadBooks() { this.numOfReadBooks++; }
    public long getNumOfReadBooks() { return this.numOfReadBooks; }

    public void setReadingChallengeGoal(long readingChallengeGoal) { this.readingChallengeGoal = readingChallengeGoal; }
    public long getReadingChallengeGoal() { return this.readingChallengeGoal; }
}