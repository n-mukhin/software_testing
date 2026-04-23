package Lower.mod;

import Lower.NaturalL;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class NaturalLTest {

    private static final BigDecimal ACCURACY_6 = new BigDecimal("0.000001");

    private final NaturalL ln = new NaturalL();

    @ParameterizedTest
    @CsvFileSource(resources = "/mod/ln.csv", numLinesToSkip = 1)
    void shouldComputeLnFromCsv(String x, String expectedY) {
        BigDecimal actual = ln.compute(new BigDecimal(x), ACCURACY_6)
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal expected = new BigDecimal(expectedY)
                .setScale(6, RoundingMode.HALF_UP);

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnZeroForOne() {
        BigDecimal actual = ln.compute(BigDecimal.ONE, ACCURACY_6)
                .setScale(6, RoundingMode.HALF_UP);

        assertEquals(new BigDecimal("0.000000"), actual);
    }

    @Test
    void shouldThrowForZero() {
        assertThrows(ArithmeticException.class,
                () -> ln.compute(BigDecimal.ZERO, ACCURACY_6));
    }

    @Test
    void shouldThrowForNegative() {
        assertThrows(ArithmeticException.class,
                () -> ln.compute(new BigDecimal("-1"), ACCURACY_6));
    }

    @Test
    void shouldThrowForNullValue() {
        assertThrows(RuntimeException.class,
                () -> ln.compute(null, ACCURACY_6));
    }

    @Test
    void shouldThrowForNullAccuracy() {
        assertThrows(RuntimeException.class,
                () -> ln.compute(BigDecimal.ONE, null));
    }

    @Test
    void shouldThrowForNegativeAccuracy() {
        assertThrows(RuntimeException.class,
                () -> ln.compute(new BigDecimal("2"), new BigDecimal("-0.001")));
    }

    @Test
    void shouldThrowForZeroAccuracy() {
        assertThrows(RuntimeException.class,
                () -> ln.compute(new BigDecimal("2"), BigDecimal.ZERO));
    }
}