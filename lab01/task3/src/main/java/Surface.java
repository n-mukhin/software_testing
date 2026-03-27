public class Surface {
    private double albedo;
    private double waterLevel;

    public Surface(double albedo, double waterLevel) {
        this.albedo = albedo;
        this.waterLevel = waterLevel;
    }

    public double getAlbedo() {
        return albedo;
    }

    public double getWaterLevel() {
        return waterLevel;
    }

    public double absorbEnergy(double flux) {
        return flux * (1.0 - albedo);
    }
}