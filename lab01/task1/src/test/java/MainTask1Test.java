import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTask1Test {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldPrintUsageWhenNoArguments() {
        MainTask1.main(new String[]{});

        assertTrue(outContent.toString().contains("Usage: <x> [<n>]"));
    }

    @Test
    void shouldPrintErrorForInvalidX() {
        MainTask1.main(new String[]{"abc"});

        assertTrue(outContent.toString().contains(
                "Invalid input. Enter a valid number for x and an integer for n."
        ));
    }

    @Test
    void shouldPrintErrorForInvalidN() {
        MainTask1.main(new String[]{"1.0", "abc"});

        assertTrue(outContent.toString().contains(
                "Invalid input. Enter a valid number for x and an integer for n."
        ));
    }

    @Test
    void shouldPrintErrorForInvalidCosineInput() {
        MainTask1.main(new String[]{"NaN"});

        assertTrue(outContent.toString().contains(
                "Error: invalid input value."
        ));
    }

    @Test
    void shouldPrintResultForValidInput() {
        MainTask1.main(new String[]{"1.0", "10"});

        assertTrue(outContent.toString().contains("app.cos"));
    }

    @Test
    void shouldPrintExceptionMessageForInvalidNValue() {
        MainTask1.main(new String[]{"1.0", "0"});

        assertTrue(outContent.toString().contains(
                "n must be greater than 0."
        ));
    }
}