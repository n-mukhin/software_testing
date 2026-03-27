public class Atmosphere {

    private double density;
    private String composition;
    private double transparency;

    public Atmosphere(double density, String composition) {
        this(density, composition, 1.0);
    }

    public Atmosphere(double density, String composition, double transparency) {
        this.density = density;
        this.composition = composition;
        this.transparency = transparency;
    }

    public void transmitLight() {
        System.out.println("Atmosphere transmits light.");
    }

    public void scatterLight() {
        System.out.println("Atmosphere scatters light.");
    }

    public double getDensity() {
        return density;
    }

    public String getComposition() {
        return composition;
    }

    public double getTransparency() {
        return transparency;
    }

    public void setTransparency(double transparency) {
        this.transparency = transparency;
    }
}