package Upper.mod;

import Upper.Cot;
import Upper.Sin;
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

class CotTest {

    private static final BigDecimal ACCURACY = new BigDecimal("0.000001");

    private final Cot cot = new Cot();

    @ParameterizedTest(name = "[{index}] x(rad)={0}")
    @CsvFileSource(resources = "/mod/cot.csv", numLinesToSkip = 1)
    void compute_shouldMatchExpectedValues_fromCsv(String xRadians, String expectedY) {
        if ("NaN".equals(expectedY)) {
            return;
        }

        if ("inf".equalsIgnoreCase(expectedY)) {
            ArithmeticException ex = assertThrows(ArithmeticException.class, () ->
                    cot.compute(new BigDecimal(xRadians), ACCURACY)
            );

            assertEquals("inf", ex.getMessage());
            return;
        }

        BigDecimal actual = cot.compute(new BigDecimal(xRadians), ACCURACY)
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal expected = new BigDecimal(expectedY)
                .setScale(6, RoundingMode.HALF_UP);

        assertEquals(expected, actual);
    }

    @Test
    void compute_shouldThrowInf_whenSinIsZero_atZero() {
        ArithmeticException ex = assertThrows(ArithmeticException.class, () ->
                cot.compute(BigDecimal.ZERO, ACCURACY)
        );

        assertEquals("inf", ex.getMessage());
    }

    @Test
    void compute_shouldThrowInf_whenComputedSinValueIsNearZero() {
        Cot cot = new Cot();
        Sin sin = Mockito.mock(Sin.class);

        when(sin.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("0.0000001"));

        setField(cot, "sin", sin);

        ArithmeticException ex = assertThrows(ArithmeticException.class, () ->
                cot.compute(new BigDecimal("0.5"), ACCURACY)
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