package Upper;

import Storing.CoreFunctions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Tan extends CoreFunctions.BaseFunction {

    private final Sin sin = new Sin();
    private final Cos cos = new Cos();

    private static final BigDecimal PI = BigDecimal.valueOf(Math.PI);
    private static final BigDecimal TWO = new BigDecimal("2");

    @Override
    public BigDecimal compute(BigDecimal value, BigDecimal accuracy) {
        validateInput(value, accuracy);

        int scale = Math.max(accuracy.scale() + 10, 20);
        MathContext mc = new MathContext(scale, RoundingMode.HALF_UP);

        BigDecimal internalAccuracy = accuracy.setScale(scale, RoundingMode.HALF_UP);
        BigDecimal singularAccuracy = accuracy.multiply(new BigDecimal("10"), mc);

        BigDecimal twoPi = PI.multiply(TWO, mc);
        BigDecimal x = value.remainder(twoPi, mc);

        if (x.compareTo(PI) > 0) {
            x = x.subtract(twoPi, mc);
        } else if (x.compareTo(PI.negate()) < 0) {
            x = x.add(twoPi, mc);
        }

        BigDecimal halfPi = PI.divide(TWO, mc);

        if (x.subtract(halfPi, mc).abs().compareTo(singularAccuracy) <= 0
                || x.add(halfPi, mc).abs().compareTo(singularAccuracy) <= 0) {
            throw new ArithmeticException("inf");
        }

        BigDecimal sinValue = sin.compute(value, internalAccuracy);
        BigDecimal cosValue = cos.compute(value, internalAccuracy);

        if (cosValue.abs().compareTo(singularAccuracy) <= 0) {
            throw new ArithmeticException("inf");
        }

        return sinValue
                .divide(cosValue, mc)
                .setScale(accuracy.scale(), RoundingMode.HALF_UP);
    }
}