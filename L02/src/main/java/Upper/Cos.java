package Upper;

import Storing.CoreFunctions;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Cos extends CoreFunctions.BaseFunction {

    private final Sin sin = new Sin();

    private static final BigDecimal PI = BigDecimal.valueOf(Math.PI);
    private static final BigDecimal TWO = new BigDecimal("2");

    @Override
    public BigDecimal compute(BigDecimal value, BigDecimal accuracy) {
        validateInput(value, accuracy);

        int scale = Math.max(accuracy.scale() + 10, 20);
        BigDecimal internalAccuracy = BigDecimal.ONE.scaleByPowerOfTen(-scale);

        BigDecimal halfPi = PI.divide(TWO, scale, RoundingMode.HALF_UP);

        return sin
                .compute(halfPi.subtract(value), internalAccuracy)
                .setScale(accuracy.scale(), RoundingMode.HALF_UP);
    }
}