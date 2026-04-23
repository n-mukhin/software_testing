public class HabitabilityCalculator {
    private static final double SIGMA = 5.67e-8;

    public double computeTemperature(Star star, Orbit orbit, Atmosphere atmosphere, Surface surface) {
        double distance = orbit.getAverageDistance();
        double flux = star.calculateFlux(distance);
        double absorbedFlux = surface.absorbEnergy(flux);
        double equilibriumTemperature = Math.pow(absorbedFlux / SIGMA, 0.25);
        double insulation = atmosphere.calculateInsulation();
        return equilibriumTemperature * (1.0 + insulation);
    }

    public double computeHabitability(double temp, double water, double oxygen, double radiation) {
        return (water * oxygen) / (1.0 + Math.abs(temp - 22.0) + radiation);
    }

    public ClimateType computeClimate(double temp) {
        if (temp < 0.0) {
            return ClimateType.COLD;
        }
        if (temp <= 30.0) {
            return ClimateType.TEMPERATE;
        }
        return ClimateType.HOT;
    }
}