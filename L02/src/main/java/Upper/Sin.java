package Upper;

import Storing.CoreFunctions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Sin extends CoreFunctions.BaseFunction {

    private static final BigDecimal PI = BigDecimal.valueOf(Math.PI);
    private static final BigDecimal TWO = new BigDecimal("2");

    @Override
    public BigDecimal compute(BigDecimal value, BigDecimal accuracy) {
        validateInput(value, accuracy);

        int scale = Math.max(accuracy.scale() + 10, 20);
        MathContext mc = new MathContext(scale, RoundingMode.HALF_UP);
        BigDecimal internalAccuracy = BigDecimal.ONE.scaleByPowerOfTen(-scale);

        BigDecimal x = value;

        BigDecimal twoPi = PI.multiply(TWO, mc);
        x = x.remainder(twoPi, mc);

        BigDecimal result = BigDecimal.ZERO;
        BigDecimal term = x;
        BigDecimal xSquared = x.multiply(x, mc);

        for (int n = 0; n < iterations; n++) {
            result = result.add(term, mc);

            if (term.abs().compareTo(internalAccuracy) < 0) {
                return result.setScale(accuracy.scale(), RoundingMode.HALF_UP);
            }

            BigDecimal denominator = BigDecimal.valueOf((2L * n + 2) * (2L * n + 3));
            term = term.multiply(xSquared, mc)
                    .divide(denominator, mc)
                    .negate();
        }

        return result.setScale(accuracy.scale(), RoundingMode.HALF_UP);
    }
}