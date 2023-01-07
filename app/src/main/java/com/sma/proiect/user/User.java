package com.sma.proiect.user;

import java.io.Serializable;

public abstract class User implements Serializable {
    private String firstName;
    private String lastName;
    protected String userType;
    private String userID;

    public User(String firstName, String lastName, String userType, String uniqueUserID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = uniqueUserID;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public abstract String getUserType();

    public String getUserID() {
        return this.userID;
    }
}
