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
    private String ISBN13;

    public Book() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Book(String title, String author, String publisher, String publicationDate, String genre, String ISBN10, String ISBN13) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.genre = genre;
        this.ISBN10 = ISBN10;
        this.ISBN13 = ISBN13;
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

    public String getISBN13() {
        return this.ISBN13;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return title.equals(book.title) && author.equals(book.author) &&
                publisher.equals(book.publisher) && publicationDate.equals(book.publicationDate) &&
                genre.equals(book.genre) && ISBN10.equals(book.ISBN10) && ISBN13.equals(book.ISBN13);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, publisher, publicationDate, genre, ISBN10, ISBN13);
    }
}
