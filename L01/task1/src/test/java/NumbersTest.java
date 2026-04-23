import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import utils.Cosine;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumbersTest {

    @ParameterizedTest(name = "cos({0})")
    @ValueSource(doubles = {
            0.0,
            Math.PI / 6,
            Math.PI / 4,
            Math.PI / 3,
            Math.PI / 2,
            Math.PI,
            3 * Math.PI / 2,
            2 * Math.PI,
            -Math.PI / 3,
            10,
            -10
    })
    void checkVariousValues(double x) {
        assertEquals(Math.cos(x), Cosine.cos(x), 0.00001);
    }
}