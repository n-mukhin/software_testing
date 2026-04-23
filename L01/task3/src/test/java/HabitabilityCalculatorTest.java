import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HabitabilityCalculatorTest {

    @Test
    void computeTemperatureShouldUseAllFormulaSteps() {
        Star star = new Star(1000.0, 50.0);
        Orbit orbit = new Orbit(2.0, 0.0);
        Atmosphere atmosphere = new Atmosphere(0.5, 0.2, 0.21);
        Surface surface = new Surface(0.25, 0.7);
        HabitabilityCalculator calculator = new HabitabilityCalculator();

        double distance = orbit.getAverageDistance();
        double flux = star.calculateFlux(distance);
        double absorbed = surface.absorbEnergy(flux);
        double equilibrium = Math.pow(absorbed / 5.67e-8, 0.25);
        double expected = equilibrium * (1.0 + atmosphere.calculateInsulation());

        double result = calculator.computeTemperature(star, orbit, atmosphere, surface);

        assertEquals(expected, result, 1e-9);
    }

    @Test
    void computeHabitabilityShouldWork() {
        HabitabilityCalculator calculator = new HabitabilityCalculator();

        double result = calculator.computeHabitability(22.0, 0.8, 0.2, 0.0);

        assertEquals(0.16, result, 1e-9);
    }

    @Test
    void computeClimateShouldReturnCold() {
        HabitabilityCalculator calculator = new HabitabilityCalculator();

        ClimateType result = calculator.computeClimate(-1.0);

        assertEquals(ClimateType.COLD, result);
    }

    @Test
    void computeClimateShouldReturnTemperateForZero() {
        HabitabilityCalculator calculator = new HabitabilityCalculator();

        ClimateType result = calculator.computeClimate(0.0);

        assertEquals(ClimateType.TEMPERATE, result);
    }

    @Test
    void computeClimateShouldReturnTemperateForThirty() {
        HabitabilityCalculator calculator = new HabitabilityCalculator();

        ClimateType result = calculator.computeClimate(30.0);

        assertEquals(ClimateType.TEMPERATE, result);
    }

    @Test
    void computeClimateShouldReturnHot() {
        HabitabilityCalculator calculator = new HabitabilityCalculator();

        ClimateType result = calculator.computeClimate(30.1);

        assertEquals(ClimateType.HOT, result);
    }
}