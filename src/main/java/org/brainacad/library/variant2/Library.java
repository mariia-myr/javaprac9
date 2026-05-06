package org.brainacad.library.variant2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Library implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private transient List<Book> books;
    private transient List<Reader> registeredReaders;
    private transient List<Loan> loans;

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

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(books.size());
        for (Book book : books) {
            writeBook(out, book);
        }

        out.writeInt(registeredReaders.size());
        for (Reader reader : registeredReaders) {
            writeReader(out, reader);
        }

        out.writeInt(loans.size());
        for (Loan loan : loans) {
            out.writeInt(books.indexOf(loan.getBook()));
            out.writeInt(loan.getReader().getRegistrationNumber());
            writeLocalDate(out, loan.getLoanDate());
            writeLocalDate(out, loan.getDueDate());
            out.writeBoolean(loan.isReturned());
        }
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        books = new ArrayList<>();
        registeredReaders = new ArrayList<>();
        loans = new ArrayList<>();

        int bookCount = in.readInt();
        for (int i = 0; i < bookCount; i++) {
            books.add(readBook(in));
        }

        int readerCount = in.readInt();
        Map<Integer, Reader> readersByNumber = new HashMap<>();
        for (int i = 0; i < readerCount; i++) {
            Reader reader = readReader(in);
            registeredReaders.add(reader);
            readersByNumber.put(reader.getRegistrationNumber(), reader);
        }

        int loanCount = in.readInt();
        for (int i = 0; i < loanCount; i++) {
            int bookIndex = in.readInt();
            int registrationNumber = in.readInt();
            LocalDate loanDate = readLocalDate(in);
            LocalDate dueDate = readLocalDate(in);
            boolean returned = in.readBoolean();

            Book book = books.get(bookIndex);
            Reader reader = readersByNumber.get(registrationNumber);

            Loan loan = new Loan(book, reader, loanDate, dueDate);
            loan.setReturned(returned);
            loans.add(loan);

            if (!returned) {
                book.setAvailable(false);
                reader.borrowBook(book);
            }
        }
    }

    private void writeBook(ObjectOutputStream out, Book book) throws IOException {
        out.writeUTF(book.getTitle());
        out.writeInt(book.getPublicationYear());
        out.writeInt(book.getEditionNumber());
        out.writeBoolean(book.isAvailable());
        out.writeInt(book.getAuthors().size());
        for (Author author : book.getAuthors()) {
            writeAuthor(out, author);
        }
    }

    private Book readBook(ObjectInputStream in) throws IOException {
        String title = in.readUTF();
        int publicationYear = in.readInt();
        int editionNumber = in.readInt();
        boolean available = in.readBoolean();
        int authorCount = in.readInt();

        List<Author> authors = new ArrayList<>();
        for (int i = 0; i < authorCount; i++) {
            authors.add(readAuthor(in));
        }

        Book book = new Book(title, authors, publicationYear, editionNumber);
        book.setAvailable(available);
        return book;
    }

    private void writeReader(ObjectOutputStream out, Reader reader) throws IOException {
        out.writeUTF(reader.getFirstName());
        out.writeUTF(reader.getLastName());
        out.writeInt(reader.getRegistrationNumber());
    }

    private Reader readReader(ObjectInputStream in) throws IOException {
        Reader reader = new Reader(in.readUTF(), in.readUTF(), in.readInt());
        reader.setBorrowedBooks(new ArrayList<>());
        return reader;
    }

    private void writeAuthor(ObjectOutputStream out, Author author) throws IOException {
        out.writeUTF(author.getFirstName());
        out.writeUTF(author.getLastName());
    }

    private Author readAuthor(ObjectInputStream in) throws IOException {
        return new Author(in.readUTF(), in.readUTF());
    }

    private void writeLocalDate(ObjectOutputStream out, LocalDate date) throws IOException {
        out.writeBoolean(date != null);
        if (date != null) {
            out.writeUTF(date.toString());
        }
    }

    private LocalDate readLocalDate(ObjectInputStream in) throws IOException {
        return in.readBoolean() ? LocalDate.parse(in.readUTF()) : null;
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

