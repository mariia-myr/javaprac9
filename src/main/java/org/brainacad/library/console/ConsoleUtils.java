package org.brainacad.library.console;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class ConsoleUtils {
    private ConsoleUtils() {
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Помилка: потрібно ввести ціле число.");
            }
        }
    }

    public static String readNonBlank(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Помилка: рядок не може бути порожнім.");
        }
    }

    public static String readOptional(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static LocalDate readDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException exception) {
                System.out.println("Помилка: введіть дату у форматі YYYY-MM-DD.");
            }
        }
    }

    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String value = scanner.nextLine().trim().toLowerCase();
            if (value.equals("y") || value.equals("yes") || value.equals("т") || value.equals("так")) {
                return true;
            }
            if (value.equals("n") || value.equals("no") || value.equals("н") || value.equals("ні")) {
                return false;
            }
            System.out.println("Помилка: введіть y/n.");
        }
    }
}

