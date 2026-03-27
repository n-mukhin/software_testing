import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PlanetTest {

    @Test
    void constructorAndGettersShouldWorkInitially() {
        Planet planet = new Planet(
                "Nova",
                new Orbit(10.0, 0.1),
                new Atmosphere(0.3, 0.2, 0.21),
                new Surface(0.25, 0.7),
                new HabitabilityCalculator()
        );

        assertEquals("Nova", planet.getName());
        assertEquals(0.0, planet.getSurfaceTemperature(), 1e-9);
        assertEquals(0.0, planet.getRadiationLevel(), 1e-9);
        assertEquals(0.0, planet.getHabitabilityIndex(), 1e-9);
        assertNull(planet.getPlanetClass());
        assertNull(planet.getClimateType());
    }

    @Test
    void calculateTemperatureShouldUpdateValue() {
        Planet planet = new Planet(
                "Nova",
                new Orbit(2.0, 0.0),
                new Atmosphere(0.5, 0.2, 0.21),
                new Surface(0.25, 0.7),
                new HabitabilityCalculator()
        );

        double result = planet.calculateTemperature(new Star(1000.0, 50.0));

        assertEquals(result, planet.getSurfaceTemperature(), 1e-9);
    }

    @Test
    void updateStateShouldSetNonHabitable() {
        Planet planet = new Planet(
                "Dead",
                new Orbit(1.0, 0.0),
                new Atmosphere(0.1, 0.9, 0.01),
                new Surface(0.9, 0.01),
                new HabitabilityCalculator()
        );

        planet.updateState(new Star(1.0, 1000.0));

        assertEquals(PlanetClass.NON_HABITABLE, planet.getPlanetClass());
    }

    @Test
    void updateStateShouldSetPotentiallyHabitable() {
        Planet planet = new Planet(
                "Mid",
                new Orbit(5000.0, 0.0),
                new Atmosphere(0.0, 1.0, 1.0),
                new Surface(0.0, 0.6),
                new HabitabilityCalculator()
        );

        planet.updateState(new Star(4172762.3854984045, 0.0));

        assertEquals(PlanetClass.POTENTIALLY_HABITABLE, planet.getPlanetClass());
    }

    @Test
    void updateStateShouldSetHabitable() {
        Planet planet = new Planet(
                "Eden",
                new Orbit(5000.0, 0.0),
                new Atmosphere(0.0, 1.0, 1.0),
                new Surface(0.0, 1.0),
                new HabitabilityCalculator()
        );

        planet.updateState(new Star(4172762.3854984045, 0.0));

        assertEquals(PlanetClass.HABITABLE, planet.getPlanetClass());
    }
}