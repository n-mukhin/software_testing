enum ClimateType {
    COLD,
    TEMPERATE,
    HOT
}

enum PlanetClass {
    NON_HABITABLE,
    POTENTIALLY_HABITABLE,
    HABITABLE
}

public class Planet {
    private String name;
    private Orbit orbit;
    private Atmosphere atmosphere;
    private Surface surface;
    private HabitabilityCalculator calculator;

    private double surfaceTemperature;
    private double radiationLevel;
    private double habitabilityIndex;
    private PlanetClass planetClass;
    private ClimateType climateType;

    public Planet(String name, Orbit orbit, Atmosphere atmosphere, Surface surface, HabitabilityCalculator calculator) {
        this.name = name;
        this.orbit = orbit;
        this.atmosphere = atmosphere;
        this.surface = surface;
        this.calculator = calculator;
    }

    public String getName() {
        return name;
    }


    public double getSurfaceTemperature() {
        return surfaceTemperature;
    }

    public double getRadiationLevel() {
        return radiationLevel;
    }

    public double getHabitabilityIndex() {
        return habitabilityIndex;
    }

    public PlanetClass getPlanetClass() {
        return planetClass;
    }

    public ClimateType getClimateType() {
        return climateType;
    }

    public double calculateTemperature(Star star) {
        surfaceTemperature = calculator.computeTemperature(star, orbit, atmosphere, surface);
        return surfaceTemperature;
    }

    public double calculateHabitability(Star star) {
        double oxygen = atmosphere.calculateBreathability();
        double water = surface.getWaterLevel();
        habitabilityIndex = calculator.computeHabitability(surfaceTemperature, water, oxygen, radiationLevel);
        return habitabilityIndex;
    }

    public ClimateType determineClimate() {
        climateType = calculator.computeClimate(surfaceTemperature);
        return climateType;
    }

    public PlanetClass classify() {
        if (habitabilityIndex < 0.3) {
            planetClass = PlanetClass.NON_HABITABLE;
        } else if (habitabilityIndex < 0.7) {
            planetClass = PlanetClass.POTENTIALLY_HABITABLE;
        } else {
            planetClass = PlanetClass.HABITABLE;
        }
        return planetClass;
    }

    public void updateState(Star star) {
        calculateTemperature(star);
        radiationLevel = star.calculateRadiation(orbit.getAverageDistance()) / (1.0 + atmosphere.getDensity());
        calculateHabitability(star);
        determineClimate();
        classify();
    }
}