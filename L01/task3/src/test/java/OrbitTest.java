import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrbitTest {

    @Test
    void constructorAndGettersShouldWork() {
        Orbit orbit = new Orbit(100.0, 0.3);

        assertEquals(100.0, orbit.getDistance());
        assertEquals(0.3, orbit.getEccentricity());
    }

    @Test
    void getAverageDistanceShouldWorkForNonZeroEccentricity() {
        Orbit orbit = new Orbit(100.0, 0.2);

        double result = orbit.getAverageDistance();

        assertEquals(102.0, result, 1e-9);
    }

    @Test
    void getAverageDistanceShouldWorkForZeroEccentricity() {
        Orbit orbit = new Orbit(100.0, 0.0);

        double result = orbit.getAverageDistance();

        assertEquals(100.0, result, 1e-9);
    }
}