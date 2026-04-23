package Lower.integration;

import Lower.BaseL;
import Lower.NaturalL;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BaseLINTest {

    private static final BigDecimal ACCURACY = new BigDecimal("0.000001");

    @ParameterizedTest
    @CsvFileSource(resources = "/int/log2INT.csv", numLinesToSkip = 1)
    void shouldComputeLog2UsingMockedLn(String xValue, String expectedY) {
        testForBase(2, xValue, expectedY);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/int/log3INT.csv", numLinesToSkip = 1)
    void shouldComputeLog3UsingMockedLn(String xValue, String expectedY) {
        testForBase(3, xValue, expectedY);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/int/log5INT.csv", numLinesToSkip = 1)
    void shouldComputeLog5UsingMockedLn(String xValue, String expectedY) {
        testForBase(5, xValue, expectedY);
    }

    private void testForBase(int base, String xValue, String expectedY) {
        NaturalL ln = Mockito.mock(NaturalL.class);

        when(ln.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> {
                    BigDecimal value = invocation.getArgument(0);
                    return BigDecimal.valueOf(Math.log(value.doubleValue()));
                });

        BaseL log = new BaseL(base);
        setField(log, "ln", ln);

        BigDecimal actual = log.compute(new BigDecimal(xValue), ACCURACY)
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal expected = new BigDecimal(expectedY)
                .setScale(6, RoundingMode.HALF_UP);

        assertEquals(
                expected,
                actual,
                () -> "base=" + base
                        + ", x=" + xValue
                        + ", expected=" + expected.toPlainString()
                        + ", actual=" + actual.toPlainString()
        );

        verify(ln, atLeastOnce()).compute(eq(new BigDecimal(xValue)), any(BigDecimal.class));
        verify(ln, atLeastOnce()).compute(eq(BigDecimal.valueOf(base)), any(BigDecimal.class));
    }

    @Test
    void shouldCallLnDependencyForValueAndBase() {
        NaturalL ln = Mockito.mock(NaturalL.class);

        when(ln.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> {
                    BigDecimal value = invocation.getArgument(0);
                    return BigDecimal.valueOf(Math.log(value.doubleValue()));
                });

        BaseL log2 = new BaseL(2);
        setField(log2, "ln", ln);

        log2.compute(new BigDecimal("8"), ACCURACY);

        verify(ln, atLeastOnce()).compute(eq(new BigDecimal("8")), any(BigDecimal.class));
        verify(ln, atLeastOnce()).compute(eq(BigDecimal.valueOf(2)), any(BigDecimal.class));
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