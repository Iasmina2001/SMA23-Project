package com.sma.proiect;

import java.util.Objects;


public class Book {
    private String title;
    private String author;
    private String publisher;
    private String publicationDate;
    private String genre;
    private String ISBN10;
    private String numOfBooks;
    private String submitStatus;
    private String realtimeDatabasePath;    // eBook or paper book
    private String eBookPath;
    private String eBookCoverPath;
    private String userRatingValue;
    private String averageRatingValue;
    private boolean requestStatus;

    public Book() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Book(String title, String ISBN10) {
        this.title = title;
        this.author = "";
        this.publisher = "";
        this.publicationDate = "";
        this.genre = "";
        this.ISBN10 = ISBN10;
        this.numOfBooks = "";
        this.submitStatus = "0";    // 0 is for not submitted
        this.realtimeDatabasePath = "";
        this.eBookPath = "";
        this.eBookCoverPath = "";
        this.userRatingValue = "0";
        this.averageRatingValue = "0";
        this.requestStatus = false;
    }

    public Book(String title, String author, String publisher, String publicationDate, String genre, String ISBN10, String numOfBooks, String realtimeDatabasePath) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.genre = genre;
        this.ISBN10 = ISBN10;
        this.numOfBooks = numOfBooks;
        this.submitStatus = "0";    // 0 is for not submitted
        this.realtimeDatabasePath = realtimeDatabasePath;
        this.eBookPath = "";
        this.eBookCoverPath = "";
        this.userRatingValue = "0";
        this.averageRatingValue = "0";
        this.requestStatus = false;
    }

    public Book(String title, String author, String publisher, String publicationDate, String genre, String ISBN10, String numOfBooks, String realtimeDatabasePath, String eBookCoverPath, String eBookPath) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.genre = genre;
        this.ISBN10 = ISBN10;
        this.numOfBooks = numOfBooks;
        this.submitStatus = "0";    // 0 is for not submitted
        this.realtimeDatabasePath = realtimeDatabasePath;
        this.eBookPath = eBookPath;
        this.eBookCoverPath = eBookCoverPath;
        this.userRatingValue = "0";
        this.averageRatingValue = "0";
        this.requestStatus = false;
    }

    public Book(String title, String author, String publisher, String publicationDate, String genre, String ISBN10, String numOfBooks, String realtimeDatabasePath, String eBookCoverPath, String eBookPath, String userRatingValue) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.genre = genre;
        this.ISBN10 = ISBN10;
        this.numOfBooks = numOfBooks;
        this.submitStatus = "0";    // 0 is for not submitted
        this.realtimeDatabasePath = realtimeDatabasePath;
        this.eBookPath = eBookPath;
        this.eBookCoverPath = eBookCoverPath;
        this.userRatingValue = userRatingValue;
        this.averageRatingValue = "0";
        this.requestStatus = false;
    }

    public Book(String title, String author, String publisher, String publicationDate, String genre, String ISBN10, String numOfBooks, String realtimeDatabasePath, String eBookCoverPath, String eBookPath, String userRatingValue, String averageRatingValue) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.genre = genre;
        this.ISBN10 = ISBN10;
        this.numOfBooks = numOfBooks;
        this.submitStatus = "0";    // 0 is for not submitted
        this.realtimeDatabasePath = realtimeDatabasePath;
        this.eBookPath = eBookPath;
        this.eBookCoverPath = eBookCoverPath;
        this.userRatingValue = userRatingValue;
        this.averageRatingValue = averageRatingValue;
        this.requestStatus = false;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public String getPublicationDate() {
        return this.publicationDate;
    }

    public String getGenre() {
        return this.genre;
    }

    public String getISBN10() {
        return this.ISBN10;
    }

    public String getNumOfBooks() {
        return this.numOfBooks;
    }

    public String getSubmitStatus() { return this.submitStatus; }

    public void setSubmitStatus(String submitted) { this.submitStatus = submitted; }

    public String getRealtimeDatabasePath() { return this.realtimeDatabasePath; }

    public String getEBookFilePath() { return this.eBookPath; }

    public String getEBookCoverPath() { return this.eBookCoverPath; }

    public String getUserRatingValue() { return this.userRatingValue; }

    public String getAverageRatingValue() { return this.averageRatingValue; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return title.equals(book.title) && author.equals(book.author) &&
                publisher.equals(book.publisher) && publicationDate.equals(book.publicationDate) &&
                genre.equals(book.genre) && ISBN10.equals(book.ISBN10) && numOfBooks.equals(book.numOfBooks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, publisher, publicationDate, genre, ISBN10, numOfBooks);
    }

    public String getRealtimeDatabasePathAccordingToBookType(String bookType) {
        if (bookType.equals("eBook")) {
            return "eBooks";
        } else {
            // if (bookType.equals("paper book"))
            return "books";
        }
    }

    public String getBookTypeAccordingToRealtimeDatabasePath(String realtimeDatabasePath) {
        if (realtimeDatabasePath.equals("eBooks")) {
            return "eBook";
        } else {
            // if (realtimeDatabasePath.equals("books"))
            return "paper book";
        }
    }

    public String getStorageFilePathAccordingToBookType(String bookType) {
        if (bookType.equals("eBook")) {
            return "eBooks";
        } else {
            // if (bookType.equals("paper book"))
            return null;
        }
    }

    public String getStorageImagePathAccordingToBookType(String bookType) {
        if (bookType.equals("eBook")) {
            return "eBookCovers";
        } else {
            // if (bookType.equals("paper book"))
            return "bookCovers";
        }
    }

    public String getStorageImagePathAccordingToRealtimeDatabasePath(String realtimeDatabasePath) {
        if (realtimeDatabasePath.equals("eBooks")) {
            return "eBookCovers";
        } else {
            // if (firebaseRealtimeDatabasePath.equals("books"))
            return "bookCovers";
        }
    }
}
