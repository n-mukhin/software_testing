public class Orbit {
    private double distance;
    private double eccentricity;

    public Orbit(double distance, double eccentricity) {
        this.distance = distance;
        this.eccentricity = eccentricity;
    }

    public double getDistance() {
        return distance;
    }

    public double getEccentricity() {
        return eccentricity;
    }

    public double getAverageDistance() {
        return distance * (1.0 + eccentricity * eccentricity / 2.0);
    }
}