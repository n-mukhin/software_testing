public class CelestialBody {

    private String name;
    private double intensity;
    private LightSource.LightColor color;
    private int count;

    public CelestialBody(String name, double intensity, LightSource.LightColor color) {
        this(name, intensity, color, 1);
    }

    public CelestialBody(String name, double intensity, LightSource.LightColor color, int count) {
        this.name = name;
        this.intensity = intensity;
        this.color = color;
        this.count = count;
    }

    public void appear() {
        System.out.println(name + " appears.");
    }

    public void emitRadiation() {
        System.out.println(name + " emits radiation.");
    }

    public void burnHorizon(Horizon h) {
        h.illuminate();
        System.out.println(name + " burns the horizon.");
    }

    public Planet createPlanet(Atmosphere atmosphere, Horizon horizon) {
        double illumination = intensity * count * atmosphere.getTransparency();
        double temperature = intensity * 10;
        double atmosphereDensity = atmosphere.getDensity();
        LightSource.LightColor effectColor = color;

        return new Planet(illumination, temperature, atmosphereDensity, count, effectColor);
    }

    public String getName() {
        return name;
    }

    public double getIntensity() {
        return intensity;
    }

    public LightSource.LightColor getColor() {
        return color;
    }

    public void setColor(LightSource.LightColor color) {
        this.color = color;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}