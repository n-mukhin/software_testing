package Lower;

import Storing.CoreFunctions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class NaturalL extends CoreFunctions.BaseFunction {

    @Override
    public BigDecimal compute(BigDecimal value, BigDecimal accuracy) {
        validateInput(value, accuracy);

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArithmeticException("Натуральный логарифм определён только для x > 0");
        }

        if (value.compareTo(BigDecimal.ONE) == 0) {
            return BigDecimal.ZERO.setScale(accuracy.scale(), RoundingMode.HALF_UP);
        }

        int scale = Math.max(accuracy.scale() + 20, 30);
        MathContext mc = new MathContext(scale, RoundingMode.HALF_UP);
        BigDecimal internalAccuracy = BigDecimal.ONE.scaleByPowerOfTen(-scale);

        BigDecimal two = BigDecimal.valueOf(2);
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal currentValue = value;

        while (currentValue.compareTo(two) > 0) {
            currentValue = currentValue.divide(two, mc);
            result = result.add(compute(two, internalAccuracy), mc);
        }

        while (currentValue.compareTo(BigDecimal.ONE) < 0) {
            currentValue = currentValue.multiply(two, mc);
            result = result.subtract(compute(two, internalAccuracy), mc);
        }

        BigDecimal z = currentValue.subtract(BigDecimal.ONE, mc)
                .divide(currentValue.add(BigDecimal.ONE, mc), mc);

        BigDecimal zSquared = z.multiply(z, mc);
        BigDecimal term = z;
        BigDecimal series = BigDecimal.ZERO;

        for (int i = 1; i <= iterations; i += 2) {
            BigDecimal current = term.divide(BigDecimal.valueOf(i), mc);
            series = series.add(current, mc);

            if (current.abs().compareTo(internalAccuracy) < 0) {
                break;
            }

            term = term.multiply(zSquared, mc);
        }

        return result.add(series.multiply(two, mc), mc)
                .setScale(accuracy.scale(), RoundingMode.HALF_DOWN);
    }
}