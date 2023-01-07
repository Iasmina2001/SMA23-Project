package com.sma.proiect;

import java.util.Date;
import java.util.Objects;

public class Book {
    private String title;
    private String author;
    private String publisher;
    private String publicationDate;
    private String genre;
    private String ISBN10;
    private String numOfBooks;
    private String requestStatus;

    public Book() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Book(String title, String ISBN10) {
        this.title = title;
        this.ISBN10 = ISBN10;
        this.author = "";
        this.publisher = "";
        this.publicationDate = "";
        this.genre = "";
        this.numOfBooks = "";
        this.requestStatus = "0";    // 0 is for not submitted
    }

    public Book(String title, String author, String publisher, String publicationDate, String genre, String ISBN10, String numOfBooks) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.genre = genre;
        this.ISBN10 = ISBN10;
        this.numOfBooks = numOfBooks;
        this.requestStatus = "0";    // 0 is for not submitted
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

    public String getRequestStatus() { return this.requestStatus; }

    public void setRequestStatus(String requestStatus) { this.requestStatus = requestStatus; }

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
}
