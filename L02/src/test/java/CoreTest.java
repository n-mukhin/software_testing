import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import Storing.*;

import static org.junit.jupiter.api.Assertions.*;

class CoreTest {

    private static class TestFunction extends CoreFunctions.BaseFunction {
        @Override
        public BigDecimal compute(BigDecimal value, BigDecimal accuracy) {
            validateInput(value, accuracy);
            return value;
        }

        void callValidateInput(BigDecimal value, BigDecimal accuracy) {
            validateInput(value, accuracy);
        }

        int getIterations() {
            return iterations;
        }
    }

    @Test
    void coreFunctions_shouldBeInstantiable() {
        CoreFunctions coreFunctions = new CoreFunctions();
        assertNotNull(coreFunctions);
    }

    @Test
    void computable_shouldWorkWithLambdaImplementation() {
        CoreFunctions.Computable computable = (value, accuracy) -> value.add(accuracy);

        BigDecimal result = computable.compute(new BigDecimal("2"), new BigDecimal("0.1"));

        assertEquals(new BigDecimal("2.1"), result);
    }

    @Test
    void baseFunction_shouldUseDefaultIterationsValue() {
        TestFunction function = new TestFunction();

        assertEquals(100, function.getIterations());
    }

    @Test
    void validateInput_shouldNotThrow_whenArgumentsAreValid() {
        TestFunction function = new TestFunction();

        assertDoesNotThrow(() ->
                function.callValidateInput(new BigDecimal("5"), new BigDecimal("0.0001"))
        );
    }

    @Test
    void compute_shouldReturnValue_whenArgumentsAreValid() {
        TestFunction function = new TestFunction();

        BigDecimal result = function.compute(new BigDecimal("7"), new BigDecimal("0.01"));

        assertEquals(new BigDecimal("7"), result);
    }

    @Test
    void validateInput_shouldThrowNullPointerException_whenValueIsNull() {
        TestFunction function = new TestFunction();

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> function.callValidateInput(null, new BigDecimal("0.1"))
        );

        assertEquals("Аргумент не может быть null", ex.getMessage());
    }

    @Test
    void validateInput_shouldThrowNullPointerException_whenAccuracyIsNull() {
        TestFunction function = new TestFunction();

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> function.callValidateInput(new BigDecimal("1"), null)
        );

        assertEquals("Точность не может быть null", ex.getMessage());
    }

    @Test
    void validateInput_shouldThrowArithmeticException_whenAccuracyIsZero() {
        TestFunction function = new TestFunction();

        ArithmeticException ex = assertThrows(
                ArithmeticException.class,
                () -> function.callValidateInput(new BigDecimal("1"), BigDecimal.ZERO)
        );

        assertEquals("Точность должна быть в диапазоне (0, 1)", ex.getMessage());
    }

    @Test
    void validateInput_shouldThrowArithmeticException_whenAccuracyIsNegative() {
        TestFunction function = new TestFunction();

        ArithmeticException ex = assertThrows(
                ArithmeticException.class,
                () -> function.callValidateInput(new BigDecimal("1"), new BigDecimal("-0.1"))
        );

        assertEquals("Точность должна быть в диапазоне (0, 1)", ex.getMessage());
    }

    @Test
    void validateInput_shouldThrowArithmeticException_whenAccuracyIsOne() {
        TestFunction function = new TestFunction();

        ArithmeticException ex = assertThrows(
                ArithmeticException.class,
                () -> function.callValidateInput(new BigDecimal("1"), BigDecimal.ONE)
        );

        assertEquals("Точность должна быть в диапазоне (0, 1)", ex.getMessage());
    }

    @Test
    void validateInput_shouldThrowArithmeticException_whenAccuracyIsGreaterThanOne() {
        TestFunction function = new TestFunction();

        ArithmeticException ex = assertThrows(
                ArithmeticException.class,
                () -> function.callValidateInput(new BigDecimal("1"), new BigDecimal("1.1"))
        );

        assertEquals("Точность должна быть в диапазоне (0, 1)", ex.getMessage());
    }
}