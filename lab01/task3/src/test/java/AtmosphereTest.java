import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtmosphereTest {

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
    void shouldCreateAtmosphereWithDefaultTransparency() {
        Atmosphere atmosphere = new Atmosphere(0.3, "Rarefied");

        assertEquals(0.3, atmosphere.getDensity());
        assertEquals("Rarefied", atmosphere.getComposition());
        assertEquals(1.0, atmosphere.getTransparency());
    }

    @Test
    void shouldCreateAtmosphereWithCustomTransparency() {
        Atmosphere atmosphere = new Atmosphere(0.8, "Dense", 0.6);

        assertEquals(0.8, atmosphere.getDensity());
        assertEquals("Dense", atmosphere.getComposition());
        assertEquals(0.6, atmosphere.getTransparency());
    }

    @Test
    void shouldSetTransparency() {
        Atmosphere atmosphere = new Atmosphere(0.5, "Mixed");
        atmosphere.setTransparency(0.4);

        assertEquals(0.4, atmosphere.getTransparency());
    }

    @Test
    void shouldTransmitLight() {
        Atmosphere atmosphere = new Atmosphere(0.3, "Rarefied");

        atmosphere.transmitLight();

        assertTrue(outContent.toString().contains("Atmosphere transmits light."));
    }

    @Test
    void shouldScatterLight() {
        Atmosphere atmosphere = new Atmosphere(0.3, "Rarefied");

        atmosphere.scatterLight();

        assertTrue(outContent.toString().contains("Atmosphere scatters light."));
    }
}