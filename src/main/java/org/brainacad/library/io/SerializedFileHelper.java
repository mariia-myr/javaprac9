package org.brainacad.library.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SerializedFileHelper {
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9._-]+\\.ser");
    private static final Path BASE_DIRECTORY = Path.of("data");

    private SerializedFileHelper() {
    }

    public static Path resolveForSave(String fileName) throws IOException {
        String normalized = normalizeFileName(fileName);
        Files.createDirectories(BASE_DIRECTORY);
        return BASE_DIRECTORY.resolve(normalized).normalize();
    }

    public static Path resolveForLoad(String fileName) throws IOException {
        Path path = resolveForSave(fileName);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IOException("Файл не існує: " + path.toAbsolutePath());
        }
        return path;
    }

    public static List<String> listSerializedFiles() throws IOException {
        if (!Files.exists(BASE_DIRECTORY)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(BASE_DIRECTORY)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> FILE_NAME_PATTERN.matcher(name).matches())
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    public static String normalizeFileName(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("Ім'я файлу не може бути null");
        }

        String normalized = fileName.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Ім'я файлу не може бути порожнім");
        }

        if (normalized.contains("/") || normalized.contains("\\")) {
            throw new IllegalArgumentException("Дозволено вказувати тільки ім'я файлу без папок");
        }

        if (!FILE_NAME_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Некоректна структура імені файлу. Приклад: library-data.ser");
        }

        return normalized;
    }
}

