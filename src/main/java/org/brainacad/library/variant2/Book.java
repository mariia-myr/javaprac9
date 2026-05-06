package org.brainacad.library.variant2;

import org.brainacad.library.status.BookStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Book {
    private String title;
    private List<Author> authors;
    private int publicationYear;
    private int editionNumber;
    private boolean available;

    public Book() {
        this.authors = new ArrayList<>();
        this.available = true;
    }

    public Book(String title, List<Author> authors, int publicationYear, int editionNumber) {
        this.title = title;
        this.authors = new ArrayList<>(authors);
        this.publicationYear = publicationYear;
        this.editionNumber = editionNumber;
        this.available = true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = new ArrayList<>(authors);
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getEditionNumber() {
        return editionNumber;
    }

    public void setEditionNumber(int editionNumber) {
        this.editionNumber = editionNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void addAuthor(Author author) {
        authors.add(author);
    }

    public BookStatus getStatus() {
        return available ? BookStatus.AVAILABLE : BookStatus.ISSUED;
    }

    public String getAuthorNames() {
        return authors.stream().map(Author::getFullName).collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", authors=" + getAuthorNames() +
                ", publicationYear=" + publicationYear +
                ", editionNumber=" + editionNumber +
                ", status=" + getStatus() +
                '}';
    }
}

