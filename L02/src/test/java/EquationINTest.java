import Lower.BaseL;
import Lower.NaturalL;
import Upper.Cos;
import Upper.Cot;
import Upper.Sec;
import Upper.Tan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EquationINTest {

    private static final BigDecimal ACCURACY_6 = new BigDecimal("0.000001");

    @ParameterizedTest
    @CsvFileSource(resources = "/int/equationINT.csv", numLinesToSkip = 1)
    void shouldComputeUsingMockedDependencies(BigDecimal x, BigDecimal expectedY) {
        Tan tan = Mockito.mock(Tan.class);
        Cot cot = Mockito.mock(Cot.class);
        Sec sec = Mockito.mock(Sec.class);
        Cos cos = Mockito.mock(Cos.class);

        NaturalL ln = Mockito.mock(NaturalL.class);
        BaseL log2 = Mockito.mock(BaseL.class);
        BaseL log3 = Mockito.mock(BaseL.class);
        BaseL log5 = Mockito.mock(BaseL.class);

        when(tan.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> BigDecimal.valueOf(Math.tan(((BigDecimal) invocation.getArgument(0)).doubleValue())));

        when(cot.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> BigDecimal.valueOf(1.0 / Math.tan(((BigDecimal) invocation.getArgument(0)).doubleValue())));

        when(sec.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> BigDecimal.valueOf(1.0 / Math.cos(((BigDecimal) invocation.getArgument(0)).doubleValue())));

        when(cos.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> BigDecimal.valueOf(Math.cos(((BigDecimal) invocation.getArgument(0)).doubleValue())));

        when(ln.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> BigDecimal.valueOf(Math.log(((BigDecimal) invocation.getArgument(0)).doubleValue())));

        when(log2.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> BigDecimal.valueOf(Math.log(((BigDecimal) invocation.getArgument(0)).doubleValue()) / Math.log(2)));

        when(log3.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> BigDecimal.valueOf(Math.log(((BigDecimal) invocation.getArgument(0)).doubleValue()) / Math.log(3)));

        when(log5.compute(any(BigDecimal.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> BigDecimal.valueOf(Math.log(((BigDecimal) invocation.getArgument(0)).doubleValue()) / Math.log(5)));

        Equation equation = new Equation();

        setField(equation, "tan", tan);
        setField(equation, "cot", cot);
        setField(equation, "sec", sec);
        setField(equation, "cos", cos);
        setField(equation, "ln", ln);
        setField(equation, "log2", log2);
        setField(equation, "log3", log3);
        setField(equation, "log5", log5);

        BigDecimal actual = equation.compute(x, ACCURACY_6)
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal expected = expectedY.setScale(6, RoundingMode.HALF_UP);

        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "f({0}) = {1}")
    @CsvFileSource(resources = "/int/equationINT.csv", numLinesToSkip = 1)
    void shouldUseOnlyTrigDependenciesForNegativeX(BigDecimal x, BigDecimal expectedY) {
        assumeTrue(x.compareTo(BigDecimal.ZERO) <= 0);

        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);

        Tan tan = mock(Tan.class);
        Cot cot = mock(Cot.class);
        Sec sec = mock(Sec.class);
        Cos cos = mock(Cos.class);

        BigDecimal tanX = BigDecimal.valueOf(Math.tan(x.doubleValue()));
        BigDecimal cotX = BigDecimal.valueOf(1.0 / Math.tan(x.doubleValue()));
        BigDecimal secX = BigDecimal.valueOf(1.0 / Math.cos(x.doubleValue()));
        BigDecimal cosX = BigDecimal.valueOf(Math.cos(x.doubleValue()));

        when(tan.compute(eq(x), any())).thenReturn(tanX);
        BigDecimal level1 = tanX;

        when(cot.compute(eq(x), any())).thenReturn(cotX);
        BigDecimal level2 = level1
                .subtract(cotX, mc)
                .add(cotX, mc);

        when(sec.compute(eq(x), any())).thenReturn(secX);
        BigDecimal level3 = level2.divide(secX, mc);

        when(cos.compute(eq(x), any())).thenReturn(cosX);
        BigDecimal expected = level3
                .divide(cosX, mc)
                .subtract(cosX, mc)
                .setScale(6, RoundingMode.HALF_UP);

        Equation equation = new Equation();

        setField(equation, "tan", tan);
        setField(equation, "cot", cot);
        setField(equation, "sec", sec);
        setField(equation, "cos", cos);

        BigDecimal actual = equation.compute(x, ACCURACY_6);

        assertEquals(expectedY.setScale(6, RoundingMode.HALF_UP), expected);
        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "f({0}) = {1}")
    @CsvFileSource(resources = "/int/equationINT.csv", numLinesToSkip = 1)
    void TanForNegativeX(BigDecimal x, BigDecimal expectedY) {
        assumeTrue(x.compareTo(BigDecimal.ZERO) <= 0);

        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);

        Tan tan = new Tan();
        Cot cot = mock(Cot.class);
        Sec sec = mock(Sec.class);
        Cos cos = mock(Cos.class);

        BigDecimal tanX = tan.compute(x, BigDecimal.ONE.scaleByPowerOfTen(-20));

        BigDecimal cotX = BigDecimal.valueOf(1.0 / Math.tan(x.doubleValue()));
        BigDecimal secX = BigDecimal.valueOf(1.0 / Math.cos(x.doubleValue()));
        BigDecimal cosX = BigDecimal.valueOf(Math.cos(x.doubleValue()));

        BigDecimal level1 = tanX;

        when(cot.compute(eq(x), any())).thenReturn(cotX);
        BigDecimal level2 = level1
                .subtract(cotX, mc)
                .add(cotX, mc);

        when(sec.compute(eq(x), any())).thenReturn(secX);
        BigDecimal level3 = level2.divide(secX, mc);

        when(cos.compute(eq(x), any())).thenReturn(cosX);
        BigDecimal expected = level3
                .divide(cosX, mc)
                .subtract(cosX, mc)
                .setScale(6, RoundingMode.HALF_UP);

        Equation equation = new Equation();

        setField(equation, "tan", tan);
        setField(equation, "cot", cot);
        setField(equation, "sec", sec);
        setField(equation, "cos", cos);

        BigDecimal actual = equation.compute(x, ACCURACY_6);

        assertEquals(expectedY.setScale(6, RoundingMode.HALF_UP), expected);
        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "f({0}) = {1}")
    @CsvFileSource(resources = "/int/equationINT.csv", numLinesToSkip = 1)
    void SecAndTanForNegativeX(BigDecimal x, BigDecimal expectedY) {
        assumeTrue(x.compareTo(BigDecimal.ZERO) <= 0);

        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);
        BigDecimal p = BigDecimal.ONE.scaleByPowerOfTen(-20);

        Tan tan = new Tan();
        Cot cot = mock(Cot.class);
        Cos cos = mock(Cos.class);

        BigDecimal tanX = tan.compute(x, p);

        BigDecimal cotX = BigDecimal.valueOf(1.0 / Math.tan(x.doubleValue()));
        when(cot.compute(eq(x), any())).thenReturn(cotX);

        BigDecimal level2 = tanX
                .subtract(cotX, mc)
                .add(cotX, mc);

        Sec sec = new Sec();
        BigDecimal secX = sec.compute(x, p);

        BigDecimal level3 = level2.divide(secX, mc);

        BigDecimal cosX = BigDecimal.valueOf(Math.cos(x.doubleValue()));
        when(cos.compute(eq(x), any())).thenReturn(cosX);

        BigDecimal expected = level3
                .divide(cosX, mc)
                .subtract(cosX, mc)
                .setScale(6, RoundingMode.HALF_UP);

        Equation equation = new Equation();

        setField(equation, "tan", tan);
        setField(equation, "cot", cot);
        setField(equation, "sec", sec);
        setField(equation, "cos", cos);

        BigDecimal actual = equation.compute(x, ACCURACY_6);

        assertEquals(expectedY.setScale(6, RoundingMode.HALF_UP), expected);
        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "f({0}) = {1}")
    @CsvFileSource(resources = "/int/equationINT.csv", numLinesToSkip = 1)
    void shouldUseOnlyLogDependenciesForPositiveX(BigDecimal x, BigDecimal expectedY) {
        assumeTrue(x.compareTo(BigDecimal.ZERO) > 0);

        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);

        NaturalL ln = mock(NaturalL.class);
        BaseL log2 = mock(BaseL.class);
        BaseL log3 = mock(BaseL.class);
        BaseL log5 = mock(BaseL.class);

        BigDecimal log5X = BigDecimal.valueOf(Math.log(x.doubleValue()) / Math.log(5));
        BigDecimal log3X = BigDecimal.valueOf(Math.log(x.doubleValue()) / Math.log(3));
        BigDecimal lnX = BigDecimal.valueOf(Math.log(x.doubleValue()));
        BigDecimal log2X = BigDecimal.valueOf(Math.log(x.doubleValue()) / Math.log(2));

        when(log5.compute(eq(x), any())).thenReturn(log5X);
        BigDecimal level1 = log5X
                .subtract(log5X, mc)
                .multiply(log5X, mc)
                .pow(3, mc);

        when(log3.compute(eq(x), any())).thenReturn(log3X);
        BigDecimal numerator = level1.subtract(log3X, mc);

        BigDecimal denominatorLevel1 = log3X;

        when(ln.compute(eq(x), any())).thenReturn(lnX);
        BigDecimal denominatorLevel2 = denominatorLevel1.add(lnX, mc);

        when(log2.compute(eq(x), any())).thenReturn(log2X);
        BigDecimal denominator = denominatorLevel2.multiply(log2X, mc);

        BigDecimal expected = numerator
                .divide(denominator, mc)
                .setScale(6, RoundingMode.HALF_UP);

        Equation equation = new Equation();

        setField(equation, "log5", log5);
        setField(equation, "log3", log3);
        setField(equation, "ln", ln);
        setField(equation, "log2", log2);

        BigDecimal actual = equation.compute(x, ACCURACY_6);

        assertEquals(expectedY.setScale(6, RoundingMode.HALF_UP), expected);
        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "f({0}) = {1}")
    @CsvFileSource(resources = "/int/equationINT.csv", numLinesToSkip = 1)
    void Log5ForPositiveX(BigDecimal x, BigDecimal expectedY) {
        assumeTrue(x.compareTo(BigDecimal.ZERO) > 0);

        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);
        BigDecimal p = BigDecimal.ONE.scaleByPowerOfTen(-20);

        NaturalL ln = mock(NaturalL.class);
        BaseL log2 = mock(BaseL.class);
        BaseL log3 = mock(BaseL.class);

        BaseL log5 = new BaseL(5);
        BigDecimal log5X = log5.compute(x, p);

        BigDecimal log3X = BigDecimal.valueOf(Math.log(x.doubleValue()) / Math.log(3));
        BigDecimal lnX = BigDecimal.valueOf(Math.log(x.doubleValue()));
        BigDecimal log2X = BigDecimal.valueOf(Math.log(x.doubleValue()) / Math.log(2));

        BigDecimal level1 = log5X
                .subtract(log5X, mc)
                .multiply(log5X, mc)
                .pow(3, mc);

        when(log3.compute(eq(x), any())).thenReturn(log3X);
        BigDecimal numerator = level1.subtract(log3X, mc);

        BigDecimal denominatorLevel1 = log3X;

        when(ln.compute(eq(x), any())).thenReturn(lnX);
        BigDecimal denominatorLevel2 = denominatorLevel1.add(lnX, mc);

        when(log2.compute(eq(x), any())).thenReturn(log2X);
        BigDecimal denominator = denominatorLevel2.multiply(log2X, mc);

        BigDecimal expected = numerator
                .divide(denominator, mc)
                .setScale(6, RoundingMode.HALF_UP);

        Equation equation = new Equation();

        setField(equation, "log5", log5);
        setField(equation, "log3", log3);
        setField(equation, "ln", ln);
        setField(equation, "log2", log2);

        BigDecimal actual = equation.compute(x, ACCURACY_6);

        assertEquals(expectedY.setScale(6, RoundingMode.HALF_UP), expected);
        assertEquals(expected, actual);
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