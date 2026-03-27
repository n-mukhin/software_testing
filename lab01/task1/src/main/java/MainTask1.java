import utils.Cosine;

public class MainTask1 {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: <x> [<n>]");
            return;
        }

        try {
            double x = Double.parseDouble(args[0]);
            int n = args.length >= 2
                    ? Integer.parseInt(args[1])
                    : Integer.MAX_VALUE;

            double result = Cosine.cos(x, n);

            if (Double.isNaN(result)) {
                System.out.println("Error: invalid input value.");
                return;
            }

            System.out.printf("app.cos(%.6f) = %.12f%n(with %d terms)%n",
                    x, result, n);

        } catch (NumberFormatException e) {
            System.out.println(
                    "Invalid input. Enter a valid number for x and an integer for n."
            );
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}