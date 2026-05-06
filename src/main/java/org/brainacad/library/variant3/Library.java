package org.brainacad.library.variant3;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library implements Externalizable {
    private String name;
    private List<Book> books;
    private List<Reader> registeredReaders;
    private List<Loan> loans;

    public Library() {
        this.books = new ArrayList<>();
        this.registeredReaders = new ArrayList<>();
        this.loans = new ArrayList<>();
    }

    public Library(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = new ArrayList<>(books);
    }

    public List<Reader> getRegisteredReaders() {
        return registeredReaders;
    }

    public void setRegisteredReaders(List<Reader> registeredReaders) {
        this.registeredReaders = new ArrayList<>(registeredReaders);
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = new ArrayList<>(loans);
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void registerReader(Reader reader) {
        registeredReaders.add(reader);
    }

    public Book findAvailableBookByTitle(String title) {
        return books.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title) && book.isAvailable())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Книга недоступна: " + title));
    }

    public Reader findReaderByRegistrationNumber(int registrationNumber) {
        return registeredReaders.stream()
                .filter(reader -> reader.getRegistrationNumber() == registrationNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Читача не знайдено: " + registrationNumber));
    }

    public Loan issueBook(String title, int registrationNumber, LocalDate loanDate, LocalDate dueDate) {
        Book book = findAvailableBookByTitle(title);
        Reader reader = findReaderByRegistrationNumber(registrationNumber);

        book.setAvailable(false);
        reader.borrowBook(book);

        Loan loan = new Loan(book, reader, loanDate, dueDate);
        loans.add(loan);
        return loan;
    }

    public void returnBook(String title, int registrationNumber) {
        Loan activeLoan = loans.stream()
                .filter(loan -> !loan.isReturned())
                .filter(loan -> loan.getBook().getTitle().equalsIgnoreCase(title))
                .filter(loan -> loan.getReader().getRegistrationNumber() == registrationNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Активний прокат не знайдено"));

        activeLoan.markReturned();
        activeLoan.getBook().setAvailable(true);
        activeLoan.getReader().returnBook(activeLoan.getBook());
    }

    public long getActiveLoansCount() {
        return loans.stream().filter(loan -> !loan.isReturned()).count();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(name != null);
        if (name != null) {
            out.writeUTF(name);
        }

        out.writeInt(books.size());
        for (Book book : books) {
            out.writeObject(book);
        }

        out.writeInt(registeredReaders.size());
        for (Reader reader : registeredReaders) {
            out.writeObject(reader);
        }

        out.writeInt(loans.size());
        for (Loan loan : loans) {
            out.writeObject(loan);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = in.readBoolean() ? in.readUTF() : null;

        int bookCount = in.readInt();
        books = new ArrayList<>();
        for (int i = 0; i < bookCount; i++) {
            books.add((Book) in.readObject());
        }

        int readerCount = in.readInt();
        registeredReaders = new ArrayList<>();
        for (int i = 0; i < readerCount; i++) {
            registeredReaders.add((Reader) in.readObject());
        }

        int loanCount = in.readInt();
        loans = new ArrayList<>();
        for (int i = 0; i < loanCount; i++) {
            loans.add((Loan) in.readObject());
        }
    }

    @Override
    public String toString() {
        String bookBlock = books.stream().map(book -> "    - " + book).collect(Collectors.joining(System.lineSeparator()));
        String readerBlock = registeredReaders.stream().map(reader -> "    - " + reader).collect(Collectors.joining(System.lineSeparator()));
        String loanBlock = loans.stream().map(loan -> "    - " + loan).collect(Collectors.joining(System.lineSeparator()));

        return "Library{" + System.lineSeparator() +
                "  name='" + name + '\'' + ',' + System.lineSeparator() +
                "  books=" + System.lineSeparator() + (bookBlock.isEmpty() ? "    - немає" : bookBlock) + System.lineSeparator() +
                "  readers=" + System.lineSeparator() + (readerBlock.isEmpty() ? "    - немає" : readerBlock) + System.lineSeparator() +
                "  loans=" + System.lineSeparator() + (loanBlock.isEmpty() ? "    - немає" : loanBlock) + System.lineSeparator() +
                "  activeLoansCount=" + getActiveLoansCount() + System.lineSeparator() +
                '}';
    }
}

