import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class PlanetTest {

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
    void shouldClassifyAsMercury() {
        Planet planet = new Planet(500, 900, 0.2, 1, LightSource.LightColor.WHITE);
        assertEquals(Planet.PlanetType.MERCURY, planet.getType());
    }

    @Test
    void shouldClassifyAsVenus() {
        Planet planet = new Planet(700, 750, 0.9, 1, LightSource.LightColor.ORANGE);
        assertEquals(Planet.PlanetType.VENUS, planet.getType());
    }

    @Test
    void shouldClassifyAsEarth() {
        Planet planet = new Planet(400, 300, 0.8, 1, LightSource.LightColor.WHITE);
        assertEquals(Planet.PlanetType.EARTH, planet.getType());
    }

    @Test
    void shouldClassifyAsMars() {
        Planet planet = new Planet(200, 150, 0.2, 1, LightSource.LightColor.RED);
        assertEquals(Planet.PlanetType.MARS, planet.getType());
    }

    @Test
    void shouldClassifyAsJupiter() {
        Planet planet = new Planet(1000, 500, 2.5, 2, LightSource.LightColor.MULTICOLOR);
        assertEquals(Planet.PlanetType.JUPITER, planet.getType());
    }

    @Test
    void shouldClassifyAsSaturnByDefault() {
        Planet planet = new Planet(300, 500, 1.0, 1, LightSource.LightColor.ORANGE);
        assertEquals(Planet.PlanetType.SATURN, planet.getType());
    }

    @Test
    void shouldReturnAllPlanetParameters() {
        Planet planet = new Planet(450, 280, 0.7, 1, LightSource.LightColor.WHITE);

        assertEquals(450, planet.getIllumination());
        assertEquals(280, planet.getTemperature());
        assertEquals(0.7, planet.getAtmosphereDensity());
        assertEquals(1, planet.getVisibleSuns());
        assertEquals(LightSource.LightColor.WHITE, planet.getColorEffect());
    }

    @Test
    void shouldDescribePlanet() {
        Planet planet = new Planet(400, 300, 0.8, 1, LightSource.LightColor.WHITE);

        planet.describe();

        assertTrue(outContent.toString().contains("Planet type: EARTH"));
    }
}