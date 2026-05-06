package org.brainacad.library.variant3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public final class LibraryDriver {
    private static final Path FILE_PATH = Path.of("data", "variant3-library.ser");

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

        System.out.println("ВЕРСІЯ 3: усі класи Externalizable");
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
        Author gamma = new Author("Erich", "Gamma");
        Author helm = new Author("Richard", "Helm");

        Book cleanArchitecture = new Book("Clean Architecture", List.of(martin), 2017, 1);
        Book effectiveJava = new Book("Effective Java", List.of(bloch), 2018, 3);
        Book designPatterns = new Book("Design Patterns", List.of(gamma, helm), 1994, 1);

        Reader olena = new Reader("Olena", "Bondar", 3001);
        Reader andrii = new Reader("Andrii", "Tkachenko", 3002);

        Library library = new Library("BrainAcad Library");
        library.addBook(cleanArchitecture);
        library.addBook(effectiveJava);
        library.addBook(designPatterns);
        library.registerReader(olena);
        library.registerReader(andrii);
        return library;
    }

    private static void issueDemoBooks(Library library) {
        LocalDate today = LocalDate.now();
        library.issueBook("Clean Architecture", 3001, today, today.plusDays(7));
        library.issueBook("Design Patterns", 3002, today, today.plusDays(20));
    }
}

