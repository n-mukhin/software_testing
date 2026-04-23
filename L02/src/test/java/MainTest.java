import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private static final Path OUTPUT_DIR = Paths.get("lab2", "result");

    @AfterEach
    void cleanup() throws IOException {
        if (Files.exists(OUTPUT_DIR)) {
            Files.walk(OUTPUT_DIR)
            .sorted(Comparator.reverseOrder())
            .forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Test
    void main_shouldCreateDirectoryAndAllCsvFiles_whenDirectoryDoesNotExist() throws Exception {
        assertFalse(Files.exists(OUTPUT_DIR), "Перед тестом папка не должна существовать");

        Main.main(new String[0]);

        assertOutputCreated();
    }

    @Test
    void main_shouldCreateAllCsvFiles_whenDirectoryAlreadyExists() throws Exception {
        Files.createDirectories(OUTPUT_DIR);
        assertTrue(Files.exists(OUTPUT_DIR), "Перед тестом папка должна существовать");

        Main.main(new String[0]);

        assertOutputCreated();
    }

    private void assertOutputCreated() throws IOException {
        assertTrue(Files.exists(OUTPUT_DIR), "Папка result должна существовать");
        assertTrue(Files.isDirectory(OUTPUT_DIR), "result должна быть директорией");

        List<String> expectedFiles = List.of(
                "sin-output.csv",
                "cos-output.csv",
                "tan-output.csv",
                "cot-output.csv",
                "sec-output.csv",
                "ln-output.csv",
                "log2-output.csv",
                "log3-output.csv",
                "log5-output.csv",
                "equation-output.csv"
        );

        for (String fileName : expectedFiles) {
            Path file = OUTPUT_DIR.resolve(fileName);
            assertTrue(Files.exists(file), "Файл " + fileName + " должен быть создан");
            assertTrue(Files.isRegularFile(file), fileName + " должен быть обычным файлом");
            assertTrue(Files.size(file) > 0, "Файл " + fileName + " не должен быть пустым");
        }
    }
}