public class Star {
    private double luminosity;
    private double activity;

    public Star(double luminosity, double activity) {
        this.luminosity = luminosity;
        this.activity = activity;
    }

    public double getLuminosity() {
        return luminosity;
    }

    public double getActivity() {
        return activity;
    }

    public double calculateFlux(double distance) {
        return luminosity / (4.0 * Math.PI * distance * distance);
    }

    public double calculateRadiation(double distance) {
        return activity / (distance * distance);
    }
}