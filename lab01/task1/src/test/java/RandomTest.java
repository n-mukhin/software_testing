import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import utils.Cosine;

import java.util.Random;
import java.util.stream.DoubleStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RandomTest {

    static DoubleStream randomValues() {
        Random random = new Random(42);
        return random.doubles(20, -50, 50);
    }

    @ParameterizedTest(name = "cos({0})")
    @MethodSource("randomValues")
    void checkRandomValues(double x) {
        assertEquals(Math.cos(x), Cosine.cos(x), 0.00001);
    }
}