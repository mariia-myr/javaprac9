package org.brainacad.library.variant1;

import org.brainacad.library.console.ConsoleUtils;
import org.brainacad.library.io.SerializedFileHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public final class LibraryConsoleManager {
    private final Scanner scanner;
    private Library library;
    private String currentFileName;

    public LibraryConsoleManager(Scanner scanner) {
        this.scanner = scanner;
        this.library = LibraryDriver.createDemoLibraryWithLoans();
        this.currentFileName = LibraryDriver.getDefaultFilePath().getFileName().toString();
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = ConsoleUtils.readInt(scanner, "Оберіть дію: ");
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> showLibrary();
                    case 2 -> createNewLibrary();
                    case 3 -> addBook();
                    case 4 -> editBook();
                    case 5 -> removeBook();
                    case 6 -> addReader();
                    case 7 -> editReader();
                    case 8 -> removeReader();
                    case 9 -> issueBook();
                    case 10 -> returnBook();
                    case 11 -> extendLoan();
                    case 12 -> saveLibrary();
                    case 13 -> loadLibrary();
                    case 14 -> listFiles();
                    case 15 -> runAllDemos();
                    case 0 -> running = false;
                    default -> System.out.println("Невідома команда. Спробуйте ще раз.");
                }
            } catch (Exception exception) {
                System.out.println("Помилка: " + exception.getMessage());
            }

            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("================ ІНТЕРАКТИВНИЙ РЕЖИМ БІБЛІОТЕКИ ================");
        System.out.println("Поточна бібліотека: " + library.getName());
        System.out.println("Поточний файл: " + currentFileName);
        System.out.println("1  - Показати стан бібліотеки");
        System.out.println("2  - Створити нову бібліотеку");
        System.out.println("3  - Додати книгу");
        System.out.println("4  - Редагувати книгу");
        System.out.println("5  - Видалити книгу");
        System.out.println("6  - Додати читача");
        System.out.println("7  - Редагувати читача");
        System.out.println("8  - Видалити читача");
        System.out.println("9  - Видати книгу");
        System.out.println("10 - Повернути книгу");
        System.out.println("11 - Продовжити прокат");
        System.out.println("12 - Зберегти бібліотеку у файл");
        System.out.println("13 - Завантажити бібліотеку з файла");
        System.out.println("14 - Показати доступні .ser файли");
        System.out.println("15 - Показати демо всіх 3 версій серіалізації");
        System.out.println("0  - Вихід");
    }

    private void showLibrary() {
        System.out.println(library);
    }

    private void createNewLibrary() {
        String name = ConsoleUtils.readNonBlank(scanner, "Введіть назву нової бібліотеки: ");
        library = new Library(name);
        System.out.println("Створено нову порожню бібліотеку: " + name);
    }

    private void addBook() {
        String title = ConsoleUtils.readNonBlank(scanner, "Назва книги: ");
        String authorsInput = ConsoleUtils.readNonBlank(scanner, "Автори (через кому, формат Ім'я Прізвище): ");
        int year = ConsoleUtils.readInt(scanner, "Рік видання: ");
        int edition = ConsoleUtils.readInt(scanner, "Номер видання: ");

        library.addBook(new Book(title, parseAuthors(authorsInput), year, edition));
        System.out.println("Книгу додано.");
    }

    private void editBook() {
        String currentTitle = ConsoleUtils.readNonBlank(scanner, "Поточна назва книги: ");
        String newTitle = ConsoleUtils.readNonBlank(scanner, "Нова назва книги: ");
        String authorsInput = ConsoleUtils.readNonBlank(scanner, "Нові автори (через кому): ");
        int year = ConsoleUtils.readInt(scanner, "Новий рік видання: ");
        int edition = ConsoleUtils.readInt(scanner, "Новий номер видання: ");

        library.updateBook(currentTitle, newTitle, parseAuthors(authorsInput), year, edition);
        System.out.println("Книгу оновлено.");
    }

    private void removeBook() {
        String title = ConsoleUtils.readNonBlank(scanner, "Назва книги для видалення: ");
        library.removeBook(title);
        System.out.println("Книгу видалено.");
    }

    private void addReader() {
        String firstName = ConsoleUtils.readNonBlank(scanner, "Ім'я читача: ");
        String lastName = ConsoleUtils.readNonBlank(scanner, "Прізвище читача: ");
        int registrationNumber = ConsoleUtils.readInt(scanner, "Реєстраційний номер: ");

        library.registerReader(new Reader(firstName, lastName, registrationNumber));
        System.out.println("Читача додано.");
    }

    private void editReader() {
        int currentNumber = ConsoleUtils.readInt(scanner, "Поточний реєстраційний номер: ");
        String firstName = ConsoleUtils.readNonBlank(scanner, "Нове ім'я: ");
        String lastName = ConsoleUtils.readNonBlank(scanner, "Нове прізвище: ");
        int newNumber = ConsoleUtils.readInt(scanner, "Новий реєстраційний номер: ");

        library.updateReader(currentNumber, firstName, lastName, newNumber);
        System.out.println("Читача оновлено.");
    }

    private void removeReader() {
        int registrationNumber = ConsoleUtils.readInt(scanner, "Реєстраційний номер читача для видалення: ");
        library.removeReader(registrationNumber);
        System.out.println("Читача видалено.");
    }

    private void issueBook() {
        String title = ConsoleUtils.readNonBlank(scanner, "Назва книги: ");
        int registrationNumber = ConsoleUtils.readInt(scanner, "Реєстраційний номер читача: ");
        LocalDate loanDate = ConsoleUtils.readDate(scanner, "Дата видачі (YYYY-MM-DD): ");
        LocalDate dueDate = ConsoleUtils.readDate(scanner, "Дата повернення (YYYY-MM-DD): ");

        library.issueBook(title, registrationNumber, loanDate, dueDate);
        System.out.println("Книгу видано.");
    }

    private void returnBook() {
        String title = ConsoleUtils.readNonBlank(scanner, "Назва книги: ");
        int registrationNumber = ConsoleUtils.readInt(scanner, "Реєстраційний номер читача: ");

        library.returnBook(title, registrationNumber);
        System.out.println("Книгу повернено.");
    }

    private void extendLoan() {
        String title = ConsoleUtils.readNonBlank(scanner, "Назва книги: ");
        int registrationNumber = ConsoleUtils.readInt(scanner, "Реєстраційний номер читача: ");
        LocalDate dueDate = ConsoleUtils.readDate(scanner, "Нова дата повернення (YYYY-MM-DD): ");

        library.extendLoan(title, registrationNumber, dueDate);
        System.out.println("Прокат продовжено.");
    }

    private void saveLibrary() throws IOException {
        String fileName = ConsoleUtils.readOptional(scanner,
                "Ім'я файлу для збереження (*.ser, без папок) [Enter = " + currentFileName + "]: ");
        if (fileName.isBlank()) {
            fileName = currentFileName;
        }
        Path filePath = SerializedFileHelper.resolveForSave(fileName);
        LibraryDriver.saveLibrary(library, filePath);
        currentFileName = filePath.getFileName().toString();
        System.out.println("Бібліотеку збережено у файл: " + filePath.toAbsolutePath());
    }

    private void loadLibrary() throws IOException, ClassNotFoundException {
        String fileName = ConsoleUtils.readNonBlank(scanner, "Ім'я файлу для завантаження (*.ser, без папок): ");
        Path filePath = SerializedFileHelper.resolveForLoad(fileName);
        library = LibraryDriver.loadLibrary(filePath);
        currentFileName = filePath.getFileName().toString();
        System.out.println("Бібліотеку завантажено з файла: " + filePath.toAbsolutePath());
    }

    private void listFiles() throws IOException {
        List<String> files = SerializedFileHelper.listSerializedFiles();
        if (files.isEmpty()) {
            System.out.println("У папці data поки немає збережених .ser файлів.");
            return;
        }

        System.out.println("Доступні файли:");
        for (String file : files) {
            System.out.println(" - " + file);
        }
    }

    private void runAllDemos() throws Exception {
        org.brainacad.library.variant1.LibraryDriver.runDemo();
        System.out.println();
        org.brainacad.library.variant2.LibraryDriver.runDemo();
        System.out.println();
        org.brainacad.library.variant3.LibraryDriver.runDemo();
    }

    private List<Author> parseAuthors(String authorsInput) {
        List<Author> authors = Arrays.stream(authorsInput.split(","))
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .map(this::parseAuthor)
                .toList();

        if (authors.isEmpty()) {
            throw new IllegalArgumentException("Потрібно вказати хоча б одного автора");
        }

        return authors;
    }

    private Author parseAuthor(String value) {
        String[] parts = value.trim().split("\\s+");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Автор має містити щонайменше ім'я та прізвище: " + value);
        }

        String firstName = parts[0];
        String lastName = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
        return new Author(firstName, lastName);
    }
}


