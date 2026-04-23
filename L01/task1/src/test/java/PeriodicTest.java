import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import utils.Cosine;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PeriodicTest {

    @ParameterizedTest
    @ValueSource(doubles = {
            0.0,
            Math.PI / 6,
            Math.PI / 4,
            Math.PI / 3,
            1.0,
            -2.5,
            10.0
    })
    void cosineShouldBePeriodic(double x) {
        double period = 2 * Math.PI;

        assertEquals(Cosine.cos(x), Cosine.cos(x + period), 1e-6);
        assertEquals(Cosine.cos(x), Cosine.cos(x - period), 1e-6);
        assertEquals(Cosine.cos(x), Cosine.cos(x + 2 * period), 1e-6);
    }
}