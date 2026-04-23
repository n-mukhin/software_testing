import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class EquationTest {

    private static final BigDecimal ACCURACY_6 = new BigDecimal("0.000001");

    private final Equation equation = new Equation();

    @ParameterizedTest
    @CsvFileSource(resources = "/mod/equation.csv", numLinesToSkip = 1)
    void shouldComputeFromCsv(String xValue, String expectedY) {
        BigDecimal x = new BigDecimal(xValue);

        if (x.compareTo(BigDecimal.ZERO) == 0 || "NaN".equals(expectedY)) {
            ArithmeticException ex = assertThrows(
                    ArithmeticException.class,
                    () -> equation.compute(x, ACCURACY_6)
            );
            assertEquals("У функции нет значения при x = " + x.toString(), ex.getMessage());
            return;
        }

        BigDecimal actual = equation.compute(x, ACCURACY_6)
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal expected = new BigDecimal(expectedY)
                .setScale(6, RoundingMode.HALF_UP);

        assertEquals(
                expected,
                actual,
                () -> "x=" + x.toPlainString()
                        + ", expected=" + expected.toPlainString()
                        + ", actual=" + actual.toPlainString()
        );
    }

    @Test
    void shouldThrowForNullX() {
        assertThrows(RuntimeException.class,
                () -> equation.compute(null, ACCURACY_6));
    }

    @Test
    void shouldThrowForNullAccuracy() {
        assertThrows(RuntimeException.class,
                () -> equation.compute(BigDecimal.ONE, null));
    }

    @Test
    void shouldThrowForNegativeAccuracy() {
        assertThrows(RuntimeException.class,
                () -> equation.compute(BigDecimal.ONE, new BigDecimal("-0.001")));
    }

    @Test
    void shouldThrowForZeroAccuracy() {
        assertThrows(RuntimeException.class,
                () -> equation.compute(BigDecimal.ONE, BigDecimal.ZERO));
    }

    @Test
    void shouldThrowForZeroBecauseTrigBranchIsUndefined() {
        ArithmeticException ex = assertThrows(
                ArithmeticException.class,
                () -> equation.compute(BigDecimal.ZERO, ACCURACY_6)
        );

        assertEquals("У функции нет значения при x = 0", ex.getMessage());
    }

    @Test
    void shouldThrowForOneBecauseLogBranchIsUndefined() {
        ArithmeticException ex = assertThrows(
                ArithmeticException.class,
                () -> equation.compute(BigDecimal.ONE, ACCURACY_6)
        );

        assertEquals("У функции нет значения при x = 1", ex.getMessage());
    }
}
