package org.brainacad.library.variant3;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Reader implements Externalizable {
    private String firstName;
    private String lastName;
    private int registrationNumber;
    private List<Book> borrowedBooks;

    public Reader() {
        this.borrowedBooks = new ArrayList<>();
    }

    public Reader(String firstName, String lastName, int registrationNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.registrationNumber = registrationNumber;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(int registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(List<Book> borrowedBooks) {
        this.borrowedBooks = new ArrayList<>(borrowedBooks);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void borrowBook(Book book) {
        borrowedBooks.add(book);
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(firstName != null);
        if (firstName != null) {
            out.writeUTF(firstName);
        }
        out.writeBoolean(lastName != null);
        if (lastName != null) {
            out.writeUTF(lastName);
        }
        out.writeInt(registrationNumber);
        out.writeInt(borrowedBooks.size());
        for (Book borrowedBook : borrowedBooks) {
            out.writeObject(borrowedBook);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        firstName = in.readBoolean() ? in.readUTF() : null;
        lastName = in.readBoolean() ? in.readUTF() : null;
        registrationNumber = in.readInt();
        int borrowedCount = in.readInt();
        borrowedBooks = new ArrayList<>();
        for (int i = 0; i < borrowedCount; i++) {
            borrowedBooks.add((Book) in.readObject());
        }
    }

    @Override
    public String toString() {
        String books = borrowedBooks.isEmpty()
                ? "немає"
                : borrowedBooks.stream().map(Book::getTitle).collect(Collectors.joining(", "));
        return "Reader{" +
                "fullName='" + getFullName() + '\'' +
                ", registrationNumber=" + registrationNumber +
                ", borrowedBooks=" + books +
                '}';
    }
}

