package Lower;

import Storing.CoreFunctions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BaseL extends CoreFunctions.BaseFunction {

    private final BigDecimal base;
    private final NaturalL ln;

    public BaseL(int base) {
        if (base <= 0 || base == 1) {
            throw new IllegalArgumentException("Основание логарифма должно быть > 0 и != 1");
        }
        this.base = BigDecimal.valueOf(base);
        this.ln = new NaturalL();
    }

    @Override
    public BigDecimal compute(BigDecimal value, BigDecimal accuracy) {
        validateInput(value, accuracy);

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArithmeticException("Логарифм определён только для x > 0");
        }

        int scale = Math.max(accuracy.scale() + 10, 20);
        MathContext mc = new MathContext(scale, RoundingMode.HALF_UP);
        BigDecimal internalAccuracy = BigDecimal.ONE.scaleByPowerOfTen(-scale);

        return ln.compute(value, internalAccuracy)
                .divide(ln.compute(base, internalAccuracy), mc)
                .round(new MathContext(accuracy.scale() + 1, RoundingMode.HALF_UP))
                .setScale(accuracy.scale(), RoundingMode.HALF_UP);
    }
}