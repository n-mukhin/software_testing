public class Horizon {

    private String color;
    private double visibility;

    public Horizon(String color, double visibility) {
        this.color = color;
        this.visibility = visibility;
    }

    public void illuminate() {
        System.out.println("Horizon is illuminated.");
    }

    public void darken() {
        System.out.println("Horizon darkens.");
    }

    public String getColor() {
        return color;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }
}