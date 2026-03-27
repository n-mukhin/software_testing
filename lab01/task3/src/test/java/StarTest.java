import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StarTest {

    @Test
    void constructorAndGettersShouldWork() {
        Star star = new Star(1000.0, 50.0);

        assertEquals(1000.0, star.getLuminosity());
        assertEquals(50.0, star.getActivity());
    }

    @Test
    void calculateFluxShouldWork() {
        Star star = new Star(1000.0, 50.0);

        double result = star.calculateFlux(2.0);

        assertEquals(1000.0 / (4.0 * Math.PI * 4.0), result, 1e-9);
    }

    @Test
    void calculateRadiationShouldWork() {
        Star star = new Star(1000.0, 80.0);

        double result = star.calculateRadiation(4.0);

        assertEquals(5.0, result, 1e-9);
    }
}