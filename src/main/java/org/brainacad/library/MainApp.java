package org.brainacad.library;

import org.brainacad.library.console.ConsoleUtils;
import org.brainacad.library.variant1.LibraryConsoleManager;

import java.util.Scanner;

public final class MainApp {
    private MainApp() {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Завдання №9. Облік читачів та книг у бібліотеці");
        System.out.println();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.println("1 - Інтерактивний режим (версія 1, Serializable)");
                System.out.println("2 - Показати демо всіх 3 версій");
                System.out.println("0 - Вихід");

                int choice = ConsoleUtils.readInt(scanner, "Оберіть режим: ");
                System.out.println();

                switch (choice) {
                    case 1 -> new LibraryConsoleManager(scanner).start();
                    case 2 -> {
                        org.brainacad.library.variant1.LibraryDriver.runDemo();
                        System.out.println();
                        org.brainacad.library.variant2.LibraryDriver.runDemo();
                        System.out.println();
                        org.brainacad.library.variant3.LibraryDriver.runDemo();
                        System.out.println();
                    }
                    case 0 -> running = false;
                    default -> System.out.println("Невідома команда. Спробуйте ще раз.\n");
                }
            }
        }
    }
}

