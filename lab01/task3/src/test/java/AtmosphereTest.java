import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AtmosphereTest {

    @Test
    void constructorAndGettersShouldWork() {
        Atmosphere atmosphere = new Atmosphere(0.4, 0.25, 0.21);

        assertEquals(0.4, atmosphere.getDensity());
        assertEquals(0.25, atmosphere.getTransparency());
        assertEquals(0.21, atmosphere.getOxygenLevel());
    }

    @Test
    void calculateInsulationShouldWork() {
        Atmosphere atmosphere = new Atmosphere(0.5, 0.2, 0.21);

        double result = atmosphere.calculateInsulation();

        assertEquals(0.4, result, 1e-9);
    }

    @Test
    void calculateBreathabilityShouldReturnOxygenLevel() {
        Atmosphere atmosphere = new Atmosphere(0.5, 0.2, 0.18);

        double result = atmosphere.calculateBreathability();

        assertEquals(0.18, result, 1e-9);
    }
}