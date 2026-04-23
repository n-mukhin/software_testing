import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SurfaceTest {

    @Test
    void constructorAndGettersShouldWork() {
        Surface surface = new Surface(0.3, 0.8);

        assertEquals(0.3, surface.getAlbedo());
        assertEquals(0.8, surface.getWaterLevel());
    }

    @Test
    void absorbEnergyShouldWork() {
        Surface surface = new Surface(0.25, 0.8);

        double result = surface.absorbEnergy(200.0);

        assertEquals(150.0, result, 1e-9);
    }
}