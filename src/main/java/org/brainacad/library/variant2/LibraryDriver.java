package org.brainacad.library.variant2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public final class LibraryDriver {
    private static final Path FILE_PATH = Path.of("data", "variant2-library.ser");

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
        Library library = createDemoLibrary();
        issueDemoBooks(library);

        System.out.println("ВЕРСІЯ 2: custom Serializable + transient");
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

    private static Library createDemoLibrary() {
        Author martin = new Author("Robert", "Martin");
        Author bloch = new Author("Joshua", "Bloch");
        Author fowler = new Author("Martin", "Fowler");
        Author beck = new Author("Kent", "Beck");

        Book cleanCode = new Book("Clean Code", List.of(martin), 2008, 1);
        Book effectiveJava = new Book("Effective Java", List.of(bloch), 2018, 3);
        Book refactoring = new Book("Refactoring", List.of(fowler, beck), 2018, 2);

        Reader anna = new Reader("Anna", "Koval", 2001);
        Reader petro = new Reader("Petro", "Shevchenko", 2002);

        Library library = new Library("BrainAcad Library");
        library.addBook(cleanCode);
        library.addBook(effectiveJava);
        library.addBook(refactoring);
        library.registerReader(anna);
        library.registerReader(petro);
        return library;
    }

    private static void issueDemoBooks(Library library) {
        LocalDate today = LocalDate.now();
        library.issueBook("Clean Code", 2001, today, today.plusDays(10));
        library.issueBook("Refactoring", 2002, today, today.plusDays(18));
    }
}

