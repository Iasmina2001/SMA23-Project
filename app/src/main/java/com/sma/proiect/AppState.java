package com.sma.proiect;

import com.google.firebase.database.DatabaseReference;
import com.sma.proiect.user.User;


public class AppState {

    private static boolean canAttachDBListener;
    private static String userID;
    private static AppState singletonObject;
    private DatabaseReference databaseReference;    // reference to Firebase used for reading and writing data
    private User currentUser;    // current user to be edited or deleted
    private Book currentBook;

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

    public void setCurrentBook(Book currentBook) {
        this.currentBook = currentBook;
    }

    public Book getCurrentBook() {
        return this.currentBook;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setUserId(String uid) {
        userID = uid;
    }

    public String getUserID() { return userID; }

    public void setCanAttachDBListener(boolean canAttachListener) { canAttachDBListener = canAttachListener; }

    public boolean canAttachDBListener() {return canAttachDBListener;}
}