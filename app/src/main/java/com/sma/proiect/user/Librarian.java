package com.sma.proiect.user;

import com.sma.proiect.user.User;

public class Librarian extends User {
    public Librarian(String firstName, String lastName, String userType, String uniqueUserID) {
        super(firstName, lastName, userType, uniqueUserID);
        super.userType = "librarian";
    }

    @Override
    public String getUserType() {
        return userType;
    }
}
