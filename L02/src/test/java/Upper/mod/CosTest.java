package Upper.mod;

import Upper.Cos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CosTest {

    private static final BigDecimal PI = BigDecimal.valueOf(Math.PI);
    private static final BigDecimal ONE_EIGHTY = new BigDecimal("180");
    private static final BigDecimal ACCURACY = new BigDecimal("0.000001");

    private final Cos cos = new Cos();

    @ParameterizedTest(name = "[{index}] x(rad)={0} -> y={1}")
    @CsvFileSource(resources = "/mod/cos.csv", numLinesToSkip = 1)
    void compute_shouldMatchExpectedValues_fromCsv(String xRadians, String expectedY) {

        BigDecimal radians = new BigDecimal(xRadians);
        BigDecimal actual = cos.compute(radians, ACCURACY)
                .setScale(6, RoundingMode.HALF_UP);
        BigDecimal expected = new BigDecimal(expectedY)
                .setScale(6, RoundingMode.HALF_UP);
        assertEquals(
                expected,
                actual,
                () -> "x(rad)=" + xRadians +
                        ", expected=" + expected.toPlainString() +
                        ", actual=" + actual.toPlainString()
        );
    }

    @Test
    void compute_shouldReachFinalReturnInSin_whenIterationsZero() throws Exception {
        Cos cos = new Cos();

        Field sinField = Cos.class.getDeclaredField("sin");
        sinField.setAccessible(true);
        Object sin = sinField.get(cos);

        Field iterationsField = findIterationsField(sin.getClass());
        iterationsField.setAccessible(true);
        iterationsField.setInt(sin, 0);

        BigDecimal actual = cos.compute(new BigDecimal("30"), ACCURACY)
                .setScale(6, RoundingMode.HALF_UP);

        assertEquals(new BigDecimal("0.000000"), actual);
    }

    private static Field findIterationsField(Class<?> type) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField("iterations");
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException();
    }
}