package Lower.mod;

import Lower.BaseL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class BaseLTest {

    private static final BigDecimal ACCURACY_6 = new BigDecimal("0.000001");

    private BaseL base2;
    private BaseL base3;
    private BaseL base5;

    @BeforeEach
    void setUp() {
        base2 = new BaseL(2);
        base3 = new BaseL(3);
        base5 = new BaseL(5);
    }

    @Nested
    class ConstructorTests {

        @Test
        void shouldThrowForInvalidBase_zero() {
            assertThrows(IllegalArgumentException.class, () -> new BaseL(0));
        }

        @Test
        void shouldThrowForInvalidBase_one() {
            assertThrows(IllegalArgumentException.class, () -> new BaseL(1));
        }

        @Test
        void shouldThrowForInvalidBase_negative() {
            assertThrows(IllegalArgumentException.class, () -> new BaseL(-5));
        }

        @Test
        void shouldCreateForValidBase() {
            assertDoesNotThrow(() -> new BaseL(2));
            assertDoesNotThrow(() -> new BaseL(3));
            assertDoesNotThrow(() -> new BaseL(5));
        }
    }

    @Nested
    class ComputeTests {

        @ParameterizedTest
        @CsvFileSource(resources = "/mod/log2.csv", numLinesToSkip = 1)
        void shouldComputeLog2FromCsv(String x, String expectedY) {
            BigDecimal actual = base2.compute(new BigDecimal(x), ACCURACY_6)
                    .setScale(6, RoundingMode.HALF_UP);

            BigDecimal expected = new BigDecimal(expectedY)
                    .setScale(6, RoundingMode.HALF_UP);

            assertEquals(expected, actual);
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/mod/log3.csv", numLinesToSkip = 1)
        void shouldComputeLog3FromCsv(String x, String expectedY) {
            BigDecimal actual = base3.compute(new BigDecimal(x), ACCURACY_6)
                    .setScale(6, RoundingMode.HALF_UP);

            BigDecimal expected = new BigDecimal(expectedY)
                    .setScale(6, RoundingMode.HALF_UP);

            assertEquals(expected, actual);
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/mod/log5.csv", numLinesToSkip = 1)
        void shouldComputeLog5FromCsv(String x, String expectedY) {
            BigDecimal actual = base5.compute(new BigDecimal(x), ACCURACY_6)
                    .setScale(6, RoundingMode.HALF_UP);

            BigDecimal expected = new BigDecimal(expectedY)
                    .setScale(6, RoundingMode.HALF_UP);

            assertEquals(expected, actual);
        }

        @Test
        void shouldThrowArithmeticExceptionForZero() {
            assertThrows(ArithmeticException.class,
                    () -> base2.compute(BigDecimal.ZERO, ACCURACY_6));
        }

        @Test
        void shouldThrowArithmeticExceptionForNegative() {
            assertThrows(ArithmeticException.class,
                    () -> base2.compute(new BigDecimal("-1"), ACCURACY_6));
        }

        @Test
        void shouldReturnZeroForValueOne() {
            BigDecimal actual = base2.compute(BigDecimal.ONE, ACCURACY_6)
                    .setScale(6, RoundingMode.HALF_UP);

            assertEquals(new BigDecimal("0.000000"), actual);
        }

        @Test
        void shouldThrowForNullValue() {
            assertThrows(RuntimeException.class,
                    () -> base2.compute(null, ACCURACY_6));
        }

        @Test
        void shouldThrowForNullAccuracy() {
            assertThrows(RuntimeException.class,
                    () -> base2.compute(BigDecimal.ONE, null));
        }

        @Test
        void shouldThrowForNegativeAccuracy() {
            assertThrows(RuntimeException.class,
                    () -> base2.compute(new BigDecimal("2"), new BigDecimal("-0.001")));
        }

        @Test
        void shouldThrowForZeroAccuracy() {
            assertThrows(RuntimeException.class,
                    () -> base2.compute(new BigDecimal("2"), BigDecimal.ZERO));
        }
    }
}