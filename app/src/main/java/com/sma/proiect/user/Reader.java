package com.sma.proiect.user;

import com.sma.proiect.user.User;

public class Reader extends User {
    public Reader(String firstName, String lastName, String userType, String uniqueUserID) {
        super(firstName, lastName, userType, uniqueUserID);
        super.userType = "reader";
    }

    @Override
    public String getUserType() {
        return userType;
    }
}
