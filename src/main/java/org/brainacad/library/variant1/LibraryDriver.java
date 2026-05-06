package org.brainacad.library.variant1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public final class LibraryDriver {
    private static final Path FILE_PATH = Path.of("data", "variant1-library.ser");

    private LibraryDriver() {
    }

    public static void saveLibrary(Library library, Path filePath) throws IOException {
        Files.createDirectories(filePath.getParent());
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            outputStream.writeObject(library);
        }
    }

    public static Library loadLibrary(Path filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Library) inputStream.readObject();
        }
    }

    public static void runDemo() throws IOException, ClassNotFoundException {
        Library library = createDemoLibraryWithLoans();

        System.out.println("ВЕРСІЯ 1: усі класи Serializable");
        System.out.println("Стан бібліотеки до серіалізації:");
        System.out.println(library);

        saveLibrary(library, FILE_PATH);
        Library restoredLibrary = loadLibrary(FILE_PATH);

        System.out.println("Файл серіалізації: " + FILE_PATH.toAbsolutePath());
        System.out.println("Стан бібліотеки після десеріалізації:");
        System.out.println(restoredLibrary);
    }

    public static void main(String[] args) throws Exception {
        runDemo();
    }

    public static Path getDefaultFilePath() {
        return FILE_PATH;
    }

    public static Library createDemoLibrary() {
        Author martin = new Author("Robert", "Martin");
        Author bloch = new Author("Joshua", "Bloch");
        Author fowler = new Author("Martin", "Fowler");
        Author beck = new Author("Kent", "Beck");

        Book cleanCode = new Book("Clean Code", List.of(martin), 2008, 1);
        Book effectiveJava = new Book("Effective Java", List.of(bloch), 2018, 3);
        Book refactoring = new Book("Refactoring", List.of(fowler, beck), 2018, 2);

        Reader anna = new Reader("Anna", "Koval", 1001);
        Reader petro = new Reader("Petro", "Shevchenko", 1002);

        Library library = new Library("BrainAcad Library");
        library.addBook(cleanCode);
        library.addBook(effectiveJava);
        library.addBook(refactoring);
        library.registerReader(anna);
        library.registerReader(petro);
        return library;
    }

    public static Library createDemoLibraryWithLoans() {
        Library library = createDemoLibrary();
        issueDemoBooks(library);
        return library;
    }

    private static void issueDemoBooks(Library library) {
        LocalDate today = LocalDate.now();
        library.issueBook("Clean Code", 1001, today, today.plusDays(14));
        library.issueBook("Effective Java", 1002, today, today.plusDays(21));
    }
}

