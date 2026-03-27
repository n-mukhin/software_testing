public class Planet {

    public enum PlanetType {
        MERCURY,
        VENUS,
        EARTH,
        MARS,
        JUPITER,
        SATURN
    }

    private double illumination;
    private double temperature;
    private double atmosphereDensity;
    private int visibleSuns;
    private LightSource.LightColor colorEffect;
    private PlanetType type;

    public Planet(double illumination, double temperature,
                  double atmosphereDensity, int visibleSuns,
                  LightSource.LightColor colorEffect) {
        this.illumination = illumination;
        this.temperature = temperature;
        this.atmosphereDensity = atmosphereDensity;
        this.visibleSuns = visibleSuns;
        this.colorEffect = colorEffect;
        this.type = classify();
    }

    public PlanetType classify() {
        if (temperature > 800 && atmosphereDensity < 0.3) {
            return PlanetType.MERCURY;
        }
        if (temperature > 700 && atmosphereDensity >= 0.7) {
            return PlanetType.VENUS;
        }
        if (temperature >= 200 && temperature <= 350 && atmosphereDensity >= 0.5 && visibleSuns == 1) {
            return PlanetType.EARTH;
        }
        if (temperature < 200 && atmosphereDensity < 0.5) {
            return PlanetType.MARS;
        }
        if (atmosphereDensity > 2.0 && visibleSuns >= 2) {
            return PlanetType.JUPITER;
        }
        return PlanetType.SATURN;
    }

    public void describe() {
        System.out.println("Planet type: " + type);
    }

    public double getIllumination() {
        return illumination;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getAtmosphereDensity() {
        return atmosphereDensity;
    }

    public int getVisibleSuns() {
        return visibleSuns;
    }

    public LightSource.LightColor getColorEffect() {
        return colorEffect;
    }

    public PlanetType getType() {
        return type;
    }
}