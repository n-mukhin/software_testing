import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class LightSourceTest {

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
    void shouldCreateLightSourceWithGivenValues() {
        LightSource light = new LightSource(
                1000,
                LightSource.LightForm.POINT,
                LightSource.LightColor.WHITE
        );

        assertEquals(1000, light.getBrightness());
        assertEquals(LightSource.LightForm.POINT, light.getForm());
        assertEquals(LightSource.LightColor.WHITE, light.getColor());
        assertEquals(1.0, light.getSpreadSpeed());
    }

    @Test
    void shouldCreateLightSourceWithSpreadSpeed() {
        LightSource light = new LightSource(
                1200,
                LightSource.LightForm.CRESCENT,
                LightSource.LightColor.ORANGE,
                2.5
        );

        assertEquals(1200, light.getBrightness());
        assertEquals(LightSource.LightForm.CRESCENT, light.getForm());
        assertEquals(LightSource.LightColor.ORANGE, light.getColor());
        assertEquals(2.5, light.getSpreadSpeed());
    }

    @Test
    void shouldFlash() {
        LightSource light = new LightSource(
                1000,
                LightSource.LightForm.POINT,
                LightSource.LightColor.WHITE
        );

        light.flash();

        assertTrue(outContent.toString().contains("Light source flashes."));
    }

    @Test
    void shouldExpand() {
        LightSource light = new LightSource(
                1000,
                LightSource.LightForm.POINT,
                LightSource.LightColor.WHITE
        );

        light.expand();

        assertTrue(outContent.toString().contains("Light source expands."));
    }

    @Test
    void shouldTransformLightForm() {
        LightSource light = new LightSource(
                1000,
                LightSource.LightForm.POINT,
                LightSource.LightColor.WHITE
        );

        light.transform(LightSource.LightForm.SUN);

        assertEquals(LightSource.LightForm.SUN, light.getForm());
        assertTrue(outContent.toString().contains("Light source transformed into SUN"));
    }

    @Test
    void shouldSetProperties() {
        LightSource light = new LightSource(
                1000,
                LightSource.LightForm.POINT,
                LightSource.LightColor.WHITE
        );

        light.setBrightness(1500);
        light.setForm(LightSource.LightForm.CRESCENT);
        light.setColor(LightSource.LightColor.RED);
        light.setSpreadSpeed(3.0);

        assertEquals(1500, light.getBrightness());
        assertEquals(LightSource.LightForm.CRESCENT, light.getForm());
        assertEquals(LightSource.LightColor.RED, light.getColor());
        assertEquals(3.0, light.getSpreadSpeed());
    }

    @Test
    void shouldTransformToCelestialBody() {
        LightSource light = new LightSource(
                900,
                LightSource.LightForm.SUN,
                LightSource.LightColor.ORANGE
        );

        CelestialBody body = light.transformToCelestialBody("Twin Suns", 2);

        assertEquals("Twin Suns", body.getName());
        assertEquals(900, body.getIntensity());
        assertEquals(LightSource.LightColor.ORANGE, body.getColor());
        assertEquals(2, body.getCount());
    }
}