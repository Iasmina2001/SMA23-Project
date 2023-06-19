package com.sma.proiect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class BookRequest {
    private String fine;
    private String ISBN10;
    private String title;
    private String currentUID;
    private String submitStatus;    // if the reader submitted his book request
    private String requestStatus;    // if the librarian submitted the reader's book request
    private String endDate;

    public BookRequest() {}

    public BookRequest(String fine, String ISBN10, String title, String currentUID, String submitStatus, String requestStatus) {
        this.fine = fine;
        this.ISBN10 = ISBN10;
        this.title = title;
        this.currentUID = currentUID;
        this.submitStatus = submitStatus;
        this.requestStatus = requestStatus;    // currently not approved by librarian
        this.endDate = "";
    }

    public BookRequest(String fine, String ISBN10, String title, String currentUID, String submitStatus, String requestStatus, String endDate) {
        this.fine = fine;
        this.ISBN10 = ISBN10;
        this.title = title;
        this.currentUID = currentUID;
        this.submitStatus = submitStatus;
        this.requestStatus = requestStatus;    // currently not approved by librarian
        this.endDate = endDate;
    }

    public BookRequest(String ISBN10, String title, String endDate) {
        this.fine = "";
        this.ISBN10 = ISBN10;
        this.title = title;
        this.currentUID = "";
        this.submitStatus = "";
        this.requestStatus = "0";    // currently not approved by librarian
        this.endDate = endDate;
    }

    public String getTitle() {
        return this.title;
    }

    public String getISBN10() {
        return this.ISBN10;
    }

    public String getFine() { return this.fine; }

    public String getCurrentUID() { return this.currentUID; }

    public String getSubmitStatus() { return this.submitStatus; }

    public void setSubmitStatus(String submitStatus) { this.submitStatus = submitStatus; }

    public String getRequestStatus() { return this.requestStatus; }

    public void setRequestStatus(String requestStatus) { this.requestStatus = requestStatus; }

    public String getEndDate() { return this.endDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookRequest bookRequest = (BookRequest) o;
        return ISBN10.equals(bookRequest.getISBN10()) &&
                currentUID.equals(bookRequest.getCurrentUID());
    }

    public boolean isBookRequestLate(String sEndDate) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String sCurrentDate = formatter.format(new Date(cal.getTimeInMillis()));
        Date endDate, currentDate;
        try {
            endDate = formatter.parse(sEndDate);
            currentDate = formatter.parse(sCurrentDate);
            return (currentDate.getTime() - endDate.getTime()) > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getCurrentYearString() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return String.valueOf(currentYear);
    }

    public long getNumOfDaysLateForBookRequest(String sEndDate) {
        /**
         * Function calculates the number of days between two dates. The first date represents the
         * final day when the reader can return the book without penalty and the second date is the
         * current date.
         * @param sEndDate: end date when the book lend expires
         * @return: returns integer, which indicates the number of days of penalty
         */
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String sCurrentDate = formatter.format(new Date(cal.getTimeInMillis()));
        Date currentDate, endDate;
        try {
            endDate = formatter.parse(sEndDate);
            currentDate = formatter.parse(sCurrentDate);
            return TimeUnit.DAYS.convert(currentDate.getTime() - endDate.getTime(), TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean remindBookRequest(String sEndDate, int nDays) {
        /**
         * Function reminds the user to return the book nDays before the end date expires, by displaying
         * a notification.
         * @param sEndDate: end date when the book lend expires
         * @return: returns boolean, which indicates whether the remind notification should display
         */
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        String sRemindDate;
        cal.add(Calendar.DAY_OF_YEAR, -nDays);

        String sCurrentDate = formatter.format(new Date(cal.getTimeInMillis()));
        Date currentDate, remindDate;
        try {
            sRemindDate = formatter.format(new Date(Objects.requireNonNull(formatter.parse(sEndDate)).getTime()));
            remindDate = formatter.parse(sRemindDate);
            currentDate = formatter.parse(sCurrentDate);
            return (remindDate.getTime() - currentDate.getTime()) > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
