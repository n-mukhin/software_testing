public class Atmosphere {
    private double density;
    private double transparency;
    private double oxygenLevel;

    public Atmosphere(double density, double transparency, double oxygenLevel) {
        this.density = density;
        this.transparency = transparency;
        this.oxygenLevel = oxygenLevel;
    }

    public double getDensity() {
        return density;
    }

    public double getTransparency() {
        return transparency;
    }

    public double getOxygenLevel() {
        return oxygenLevel;
    }

    public double calculateInsulation() {
        return density * (1.0 - transparency);
    }

    public double calculateBreathability() {
        return oxygenLevel;
    }
}