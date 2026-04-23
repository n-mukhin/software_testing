package Storing;

import java.math.BigDecimal;
import java.util.Objects;

public final class CoreFunctions {

    public CoreFunctions() {
    }

    public interface Computable {
        BigDecimal compute(final BigDecimal value, final BigDecimal accuracy);
    }

    public abstract static class BaseFunction implements Computable {
        private static final int DEFAULT_ITERATIONS = 100;

        protected final int iterations;

        protected BaseFunction() {
            this.iterations = DEFAULT_ITERATIONS;
        }

        protected void validateInput(final BigDecimal value, final BigDecimal accuracy) {
            Objects.requireNonNull(value, "Аргумент не может быть null");
            Objects.requireNonNull(accuracy, "Точность не может быть null");

            if (accuracy.compareTo(BigDecimal.ZERO) <= 0 || accuracy.compareTo(BigDecimal.ONE) >= 0) {
                throw new ArithmeticException("Точность должна быть в диапазоне (0, 1)");
            }
        }
    }
}