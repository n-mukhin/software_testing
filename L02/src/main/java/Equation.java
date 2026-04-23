import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import Lower.*;
import Upper.*;
import Storing.*;

public class Equation extends CoreFunctions.BaseFunction {

    private final Tan tan = new Tan();
    private final Cot cot = new Cot();
    private final Sec sec = new Sec();
    private final Cos cos = new Cos();

    private final NaturalL ln = new NaturalL();
    private final BaseL log2 = new BaseL(2);
    private final BaseL log3 = new BaseL(3);
    private final BaseL log5 = new BaseL(5);

    @Override
    public BigDecimal compute(BigDecimal x, BigDecimal accuracy) {
        validateInput(x, accuracy);

        final int scale = Math.max(accuracy.scale() + 10, 20);
        final MathContext mc = new MathContext(scale, RoundingMode.HALF_UP);
        final BigDecimal p = BigDecimal.ONE.scaleByPowerOfTen(-scale);

        try {
            if (x.compareTo(BigDecimal.ZERO) <= 0) {
                return (
                        (
                            (
                                (
                                    tan.compute(x, p)
                                        .subtract(cot.compute(x, p), mc)
                                        .add(cot.compute(x, p), mc)
                                )
                                    .divide(sec.compute(x, p), mc)
                            )
                                .divide(cos.compute(x, p), mc)
                        )
                            .subtract(cos.compute(x, p), mc)
                ).setScale(accuracy.scale(), RoundingMode.HALF_UP);
            } else {
                return (
                        (
                            (
                                log5.compute(x, p)
                                    .subtract(log5.compute(x, p), mc)
                                    .multiply(log5.compute(x, p), mc)
                            )
                                .pow(3, mc)
                        )
                            .subtract(log3.compute(x, p), mc)
                ).divide(
                        (
                            log3.compute(x, p)
                                .add(ln.compute(x, p), mc)
                                .multiply(log2.compute(x, p), mc)
                        ),
                        mc
                ).setScale(accuracy.scale(), RoundingMode.HALF_UP);
            }
        } catch (ArithmeticException e) {
            throw new ArithmeticException("У функции нет значения при x = " + x);
        }
    }
}