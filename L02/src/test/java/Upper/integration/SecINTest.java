package Upper.integration;

import Upper.Cos;
import Upper.Sec;
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

class SecINTest {

    private static final BigDecimal ACCURACY = new BigDecimal("0.000001");

    @ParameterizedTest
    @CsvFileSource(resources = "/int/secINT.csv", numLinesToSkip = 1)
    void shouldComputeUsingCosDependency(String xRadians, String expectedY) {
        Cos cos = Mockito.mock(Cos.class);

        when(cos.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> {
                    BigDecimal x = invocation.getArgument(0);
                    return BigDecimal.valueOf(Math.cos(x.doubleValue()));
                });

        Sec sec = new Sec();
        setField(sec, "cos", cos);

        BigDecimal actual = sec.compute(new BigDecimal(xRadians), ACCURACY)
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal expected = new BigDecimal(expectedY)
                .setScale(6, RoundingMode.HALF_UP);

        assertEquals(expected, actual);

        verify(cos, atLeastOnce()).compute(any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    void shouldCallCosDependency() {
        Cos cos = Mockito.mock(Cos.class);

        when(cos.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(BigDecimal.ONE);

        Sec sec = new Sec();
        setField(sec, "cos", cos);

        sec.compute(new BigDecimal("0.5"), ACCURACY);

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