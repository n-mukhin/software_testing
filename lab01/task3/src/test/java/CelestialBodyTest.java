import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class CelestialBodyTest {

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
    void shouldCreateCelestialBodyWithDefaultCount() {
        CelestialBody body = new CelestialBody(
                "Sun",
                5000,
                LightSource.LightColor.WHITE
        );

        assertEquals("Sun", body.getName());
        assertEquals(5000, body.getIntensity());
        assertEquals(LightSource.LightColor.WHITE, body.getColor());
        assertEquals(1, body.getCount());
    }

    @Test
    void shouldCreateCelestialBodyWithCustomCount() {
        CelestialBody body = new CelestialBody(
                "Twin Suns",
                6000,
                LightSource.LightColor.ORANGE,
                2
        );

        assertEquals("Twin Suns", body.getName());
        assertEquals(6000, body.getIntensity());
        assertEquals(LightSource.LightColor.ORANGE, body.getColor());
        assertEquals(2, body.getCount());
    }

    @Test
    void shouldAppear() {
        CelestialBody body = new CelestialBody(
                "Sun",
                5000,
                LightSource.LightColor.WHITE
        );

        body.appear();

        assertTrue(outContent.toString().contains("Sun appears."));
    }

    @Test
    void shouldEmitRadiation() {
        CelestialBody body = new CelestialBody(
                "Sun",
                5000,
                LightSource.LightColor.WHITE
        );

        body.emitRadiation();

        assertTrue(outContent.toString().contains("Sun emits radiation."));
    }

    @Test
    void shouldBurnHorizon() {
        CelestialBody body = new CelestialBody(
                "Sun",
                5000,
                LightSource.LightColor.WHITE
        );
        Horizon horizon = new Horizon("Black", 0.2);

        body.burnHorizon(horizon);

        String output = outContent.toString();
        assertTrue(output.contains("Horizon is illuminated."));
        assertTrue(output.contains("Sun burns the horizon."));
    }

    @Test
    void shouldSetColor() {
        CelestialBody body = new CelestialBody(
                "Sun",
                5000,
                LightSource.LightColor.WHITE
        );

        body.setColor(LightSource.LightColor.RED);

        assertEquals(LightSource.LightColor.RED, body.getColor());
    }

    @Test
    void shouldSetCount() {
        CelestialBody body = new CelestialBody(
                "Sun",
                5000,
                LightSource.LightColor.WHITE
        );

        body.setCount(3);

        assertEquals(3, body.getCount());
    }

    @Test
    void shouldCreatePlanetFromCelestialBodyAndAtmosphere() {
        CelestialBody body = new CelestialBody(
                "Twin Suns",
                100,
                LightSource.LightColor.ORANGE,
                2
        );
        Atmosphere atmosphere = new Atmosphere(0.3, "Rarefied", 0.5);
        Horizon horizon = new Horizon("Black", 0.2);

        Planet planet = body.createPlanet(atmosphere, horizon);

        assertNotNull(planet);
        assertEquals(100.0, planet.getIllumination());
        assertEquals(1000.0, planet.getTemperature());
        assertEquals(0.3, planet.getAtmosphereDensity());
        assertEquals(2, planet.getVisibleSuns());
        assertEquals(LightSource.LightColor.ORANGE, planet.getColorEffect());
    }
}