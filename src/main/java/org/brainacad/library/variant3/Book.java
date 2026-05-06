package org.brainacad.library.variant3;

import org.brainacad.library.status.BookStatus;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Book implements Externalizable {
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
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(title != null);
        if (title != null) {
            out.writeUTF(title);
        }
        out.writeInt(publicationYear);
        out.writeInt(editionNumber);
        out.writeBoolean(available);
        out.writeInt(authors.size());
        for (Author author : authors) {
            out.writeObject(author);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        title = in.readBoolean() ? in.readUTF() : null;
        publicationYear = in.readInt();
        editionNumber = in.readInt();
        available = in.readBoolean();
        int authorCount = in.readInt();
        authors = new ArrayList<>();
        for (int i = 0; i < authorCount; i++) {
            authors.add((Author) in.readObject());
        }
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

