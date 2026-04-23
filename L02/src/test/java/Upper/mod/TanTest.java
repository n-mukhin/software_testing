package Upper.mod;

import Upper.Cos;
import Upper.Tan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TanTest {

    private static final BigDecimal PI = BigDecimal.valueOf(Math.PI);
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal ACCURACY = new BigDecimal("0.000001");

    private final Tan tan = new Tan();

    @ParameterizedTest(name = "[{index}] x(rad)={0}")
    @CsvFileSource(resources = "/mod/tan.csv", numLinesToSkip = 1)
    void compute_shouldMatchExpectedValues_fromCsv(String xRadians, String expectedY) {
        if ("NaN".equals(expectedY) || "inf".equalsIgnoreCase(expectedY)) {
            ArithmeticException ex = assertThrows(ArithmeticException.class, () ->
                    tan.compute(new BigDecimal(xRadians), ACCURACY)
            );

            assertEquals("inf", ex.getMessage());
            return;
        }

        BigDecimal actual = tan.compute(new BigDecimal(xRadians), ACCURACY)
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal expected = new BigDecimal(expectedY)
                .setScale(6, RoundingMode.HALF_UP);

        assertEquals(expected, actual);
    }

    @Test
    void compute_shouldThrowInf_whenCosIsZero_atPositiveHalfPi() {
        BigDecimal halfPi = PI.divide(TWO, 20, RoundingMode.HALF_UP);

        ArithmeticException ex = assertThrows(ArithmeticException.class, () ->
                tan.compute(halfPi, ACCURACY)
        );

        assertEquals("inf", ex.getMessage());
    }

    @Test
    void compute_shouldThrowInf_whenCosIsZero_atNegativeHalfPi() {
        BigDecimal negativeHalfPi = PI.divide(TWO, 20, RoundingMode.HALF_UP).negate();

        ArithmeticException ex = assertThrows(ArithmeticException.class, () ->
                tan.compute(negativeHalfPi, ACCURACY)
        );

        assertEquals("inf", ex.getMessage());
    }

    @Test
    void compute_shouldThrowInf_whenComputedCosValueIsNearZero() {
        Tan tan = new Tan();
        Cos cos = Mockito.mock(Cos.class);

        when(cos.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("0.0000001"));

        setField(tan, "cos", cos);

        ArithmeticException ex = assertThrows(ArithmeticException.class, () ->
                tan.compute(new BigDecimal("0.5"), ACCURACY)
        );

        assertEquals("inf", ex.getMessage());
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Field findField(Class<?> type, String fieldName) throws NoSuchFieldException {
        Class<?> current = type;

        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }

        throw new NoSuchFieldException(fieldName);
    }
}