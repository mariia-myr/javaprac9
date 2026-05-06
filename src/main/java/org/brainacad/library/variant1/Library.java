package org.brainacad.library.variant1;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
        if (books.stream().anyMatch(existing -> existing.getTitle().equalsIgnoreCase(book.getTitle()))) {
            throw new IllegalArgumentException("Книга з такою назвою вже існує: " + book.getTitle());
        }
        books.add(book);
    }

    public void registerReader(Reader reader) {
        if (registeredReaders.stream().anyMatch(existing -> existing.getRegistrationNumber() == reader.getRegistrationNumber())) {
            throw new IllegalArgumentException("Читач з таким номером уже зареєстрований: " + reader.getRegistrationNumber());
        }
        registeredReaders.add(reader);
    }

    public Book findBookByTitle(String title) {
        return books.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Книгу не знайдено: " + title));
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
        if (dueDate.isBefore(loanDate)) {
            throw new IllegalArgumentException("Дата повернення не може бути раніше дати видачі");
        }

        Book book = findAvailableBookByTitle(title);
        Reader reader = findReaderByRegistrationNumber(registrationNumber);

        book.setAvailable(false);
        reader.borrowBook(book);

        Loan loan = new Loan(book, reader, loanDate, dueDate);
        loans.add(loan);
        return loan;
    }

    public void returnBook(String title, int registrationNumber) {
        Loan activeLoan = findActiveLoan(title, registrationNumber);

        activeLoan.markReturned();
        activeLoan.getBook().setAvailable(true);
        activeLoan.getReader().returnBook(activeLoan.getBook());
    }

    public Loan findActiveLoan(String title, int registrationNumber) {
        return loans.stream()
                .filter(loan -> !loan.isReturned())
                .filter(loan -> loan.getBook().getTitle().equalsIgnoreCase(title))
                .filter(loan -> loan.getReader().getRegistrationNumber() == registrationNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Активний прокат не знайдено"));
    }

    public void extendLoan(String title, int registrationNumber, LocalDate newDueDate) {
        Loan loan = findActiveLoan(title, registrationNumber);
        if (newDueDate.isBefore(loan.getLoanDate())) {
            throw new IllegalArgumentException("Нова дата повернення не може бути раніше дати видачі");
        }
        loan.setDueDate(newDueDate);
    }

    public void updateBook(String currentTitle, String newTitle, List<Author> authors, int publicationYear, int editionNumber) {
        Book book = findBookByTitle(currentTitle);
        boolean titleTaken = books.stream()
                .anyMatch(existing -> existing != book && existing.getTitle().equalsIgnoreCase(newTitle));
        if (titleTaken) {
            throw new IllegalArgumentException("Інша книга вже має назву: " + newTitle);
        }

        book.setTitle(newTitle);
        book.setAuthors(authors);
        book.setPublicationYear(publicationYear);
        book.setEditionNumber(editionNumber);
    }

    public void removeBook(String title) {
        Book book = findBookByTitle(title);
        boolean hasActiveLoan = loans.stream()
                .anyMatch(loan -> !loan.isReturned() && loan.getBook().getTitle().equalsIgnoreCase(title));
        if (hasActiveLoan) {
            throw new IllegalArgumentException("Неможливо видалити книгу з активним прокатом");
        }
        books.remove(book);
    }

    public void updateReader(int registrationNumber, String firstName, String lastName, int newRegistrationNumber) {
        Reader reader = findReaderByRegistrationNumber(registrationNumber);
        boolean numberTaken = registeredReaders.stream()
                .anyMatch(existing -> existing != reader && existing.getRegistrationNumber() == newRegistrationNumber);
        if (numberTaken) {
            throw new IllegalArgumentException("Інший читач уже має номер: " + newRegistrationNumber);
        }

        reader.setFirstName(firstName);
        reader.setLastName(lastName);
        reader.setRegistrationNumber(newRegistrationNumber);
    }

    public void removeReader(int registrationNumber) {
        Reader reader = findReaderByRegistrationNumber(registrationNumber);
        boolean hasActiveLoan = loans.stream()
                .anyMatch(loan -> !loan.isReturned() && loan.getReader().getRegistrationNumber() == registrationNumber);
        if (hasActiveLoan) {
            throw new IllegalArgumentException("Неможливо видалити читача з активним прокатом");
        }
        registeredReaders.remove(reader);
    }

    public long getActiveLoansCount() {
        return loans.stream().filter(loan -> !loan.isReturned()).count();
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

