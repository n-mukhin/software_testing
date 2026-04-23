package Upper.integration;

import Upper.Cos;
import Upper.Sin;
import Upper.Tan;
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

class TanINTest {

    private static final BigDecimal ACCURACY = new BigDecimal("0.000001");

    @ParameterizedTest
    @CsvFileSource(resources = "/int/tanINT.csv", numLinesToSkip = 1)
    void shouldComputeUsingSinAndCosDependencies(String xRadians, String expectedY) {
        Sin sin = Mockito.mock(Sin.class);
        Cos cos = Mockito.mock(Cos.class);

        when(sin.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> {
                    BigDecimal x = invocation.getArgument(0);
                    return BigDecimal.valueOf(Math.sin(x.doubleValue()));
                });

        when(cos.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> {
                    BigDecimal x = invocation.getArgument(0);
                    return BigDecimal.valueOf(Math.cos(x.doubleValue()));
                });

        Tan tan = new Tan();
        setField(tan, "sin", sin);
        setField(tan, "cos", cos);

        BigDecimal actual = tan.compute(new BigDecimal(xRadians), ACCURACY)
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal expected = new BigDecimal(expectedY)
                .setScale(6, RoundingMode.HALF_UP);

        assertEquals(expected, actual);

        verify(sin, atLeastOnce()).compute(any(BigDecimal.class), any(BigDecimal.class));
        verify(cos, atLeastOnce()).compute(any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    void shouldCallSinAndCosDependencies() {
        Sin sin = Mockito.mock(Sin.class);
        Cos cos = Mockito.mock(Cos.class);

        when(sin.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(BigDecimal.ONE);

        when(cos.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(BigDecimal.ONE);

        Tan tan = new Tan();
        setField(tan, "sin", sin);
        setField(tan, "cos", cos);

        tan.compute(new BigDecimal("0.5"), ACCURACY);

        verify(sin, atLeastOnce()).compute(any(BigDecimal.class), any(BigDecimal.class));
        verify(cos, atLeastOnce()).compute(any(BigDecimal.class), any(BigDecimal.class));
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