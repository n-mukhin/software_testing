import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HorizonTest {

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
    void shouldCreateHorizonWithGivenValues() {
        Horizon horizon = new Horizon("Black", 0.2);

        assertEquals("Black", horizon.getColor());
        assertEquals(0.2, horizon.getVisibility());
    }

    @Test
    void shouldSetVisibility() {
        Horizon horizon = new Horizon("Red", 0.5);

        horizon.setVisibility(0.9);

        assertEquals(0.9, horizon.getVisibility());
    }

    @Test
    void shouldIlluminate() {
        Horizon horizon = new Horizon("Black", 0.2);

        horizon.illuminate();

        assertTrue(outContent.toString().contains("Horizon is illuminated."));
    }

    @Test
    void shouldDarken() {
        Horizon horizon = new Horizon("Black", 0.2);

        horizon.darken();

        assertTrue(outContent.toString().contains("Horizon darkens."));
    }
}