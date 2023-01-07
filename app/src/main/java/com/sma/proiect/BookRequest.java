package com.sma.proiect;


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

    public BookRequest(String ISBN10, String title, String endDate) {
        this.fine = "";
        this.ISBN10 = ISBN10;
        this.title = title;
        this.currentUID = "";
        this.submitStatus = "";
        this.requestStatus = "";    // currently not approved by librarian
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
}
