package utils;

public class Cosine {

    public static double cos(double x) {
        return cos(x, Integer.MAX_VALUE);
    }

    public static double cos(double x, int n) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            return Double.NaN;
        }

        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0.");
        }

        x = x % (2 * Math.PI);

        double res = 1.0;
        double term = 1.0;
        double xSquared = x * x;

        for (int i = 1; i < n; i++) {
            term *= -xSquared / ((2.0 * i - 1) * (2.0 * i));
            res += term;

            if (Math.abs(term) < 1e-15) {
                break;
            }
        }

        return res;
    }
}