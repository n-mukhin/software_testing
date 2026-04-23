import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;

import Storing.*;

import static org.junit.jupiter.api.Assertions.*;

class GreatTest {

    private static final Path OUTPUT_DIR = Paths.get("test_output");
    private static final Path ROOT_FILE = Paths.get("root_test.csv");

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

        Files.deleteIfExists(ROOT_FILE);
    }

    @Test
    void write_shouldCreateFileAndWriteValues() throws IOException {
        CoreFunctions.Computable function = (x, acc) -> x.add(BigDecimal.ONE);

        Path filePath = OUTPUT_DIR.resolve("test.csv");
        GreatCSV csv = new GreatCSV(function, filePath.toString());

        csv.write(
                new BigDecimal("1"),
                new BigDecimal("3"),
                new BigDecimal("1"),
                new BigDecimal("0.001")
        );

        assertTrue(Files.exists(filePath));

        List<String> lines = Files.readAllLines(filePath);
        assertEquals("x;y", lines.get(0));
        assertEquals("1;2", lines.get(1));
        assertEquals("2;3", lines.get(2));
        assertEquals("3;4", lines.get(3));
    }

    @Test
    void write_shouldCreateParentDirectories_ifNotExist() {
        CoreFunctions.Computable function = (x, acc) -> x;

        Path nestedPath = OUTPUT_DIR.resolve("nested/dir/test.csv");
        GreatCSV csv = new GreatCSV(function, nestedPath.toString());

        csv.write(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ONE,
                new BigDecimal("0.001")
        );

        assertTrue(Files.exists(nestedPath));
    }

    @Test
    void write_shouldWork_whenFileHasNoParentDirectory() throws IOException {
        CoreFunctions.Computable function = (x, acc) -> x.multiply(new BigDecimal("2"));

        GreatCSV csv = new GreatCSV(function, ROOT_FILE.toString());

        csv.write(
                BigDecimal.ZERO,
                new BigDecimal("1"),
                BigDecimal.ONE,
                new BigDecimal("0.001")
        );

        assertTrue(Files.exists(ROOT_FILE));

        List<String> lines = Files.readAllLines(ROOT_FILE);
        assertEquals("x;y", lines.get(0));
        assertEquals("0;0", lines.get(1));
        assertEquals("1;2", lines.get(2));
    }

    @Test
    void write_shouldHandleArithmeticException_andWriteUndefined() throws IOException {
        CoreFunctions.Computable function = (x, acc) -> {
            if (x.compareTo(BigDecimal.ONE) == 0) {
                throw new ArithmeticException("test");
            }
            return x;
        };

        Path filePath = OUTPUT_DIR.resolve("test.csv");
        GreatCSV csv = new GreatCSV(function, filePath.toString());

        csv.write(
                BigDecimal.ZERO,
                new BigDecimal("2"),
                BigDecimal.ONE,
                new BigDecimal("0.001")
        );

        List<String> lines = Files.readAllLines(filePath);

        assertEquals("0;0", lines.get(1));
        assertEquals("1;undefined", lines.get(2));
        assertEquals("2;2", lines.get(3));
    }

    @Test
    void write_shouldThrowRuntimeException_whenIOExceptionOccurs() {
        CoreFunctions.Computable function = (x, acc) -> x;

        Path dirPath = OUTPUT_DIR.resolve("dir_as_file");
        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            fail(e);
        }

        GreatCSV csv = new GreatCSV(function, dirPath.toString());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                csv.write(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE)
        );

        assertTrue(ex.getMessage().contains("Ошибка записи в файл"));
    }
}