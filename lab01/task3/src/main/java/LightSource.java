public class LightSource {

    public enum LightForm {
        POINT,
        CRESCENT,
        SUN
    }

    public enum LightColor {
        WHITE,
        RED,
        ORANGE,
        MULTICOLOR
    }

    private double brightness;
    private LightForm form;
    private LightColor color;
    private double spreadSpeed;

    public LightSource(double brightness, LightForm form, LightColor color) {
        this(brightness, form, color, 1.0);
    }

    public LightSource(double brightness, LightForm form, LightColor color, double spreadSpeed) {
        this.brightness = brightness;
        this.form = form;
        this.color = color;
        this.spreadSpeed = spreadSpeed;
    }

    public void flash() {
        System.out.println("Light source flashes.");
    }

    public void expand() {
        System.out.println("Light source expands.");
    }

    public void transform(LightForm newForm) {
        this.form = newForm;
        System.out.println("Light source transformed into " + newForm);
    }

    public CelestialBody transformToCelestialBody(String name, int count) {
        return new CelestialBody(name, brightness, color, count);
    }

    public double getBrightness() {
        return brightness;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }

    public LightForm getForm() {
        return form;
    }

    public void setForm(LightForm form) {
        this.form = form;
    }

    public LightColor getColor() {
        return color;
    }

    public void setColor(LightColor color) {
        this.color = color;
    }

    public double getSpreadSpeed() {
        return spreadSpeed;
    }

    public void setSpreadSpeed(double spreadSpeed) {
        this.spreadSpeed = spreadSpeed;
    }
}