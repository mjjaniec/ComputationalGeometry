import java.awt.*;

public class Point implements Comparable<Point> , Drawable{
    private static final int RADIUS = 3;
    private double x, y;
    private Color color;
    private String label;


    public Point(double x, double y) {
        this(x, y, Color.BLACK);
    }

    public Point(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public int compareTo(Point o) {
        if (x != o.x) {
            return x < o.x ? -1 : 1;
        }
        return y < o.y ? -1 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (Double.compare(point.x, x) != 0) return false;
        if (Double.compare(point.y, y) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        fillCircle(g, (int) x, (int) y, RADIUS);
        if (getLabel() != null) {
            g.drawString(getLabel(), (int) getX() + 2 * RADIUS, (int) getY() + RADIUS);
        }
    }

    private void fillCircle(Graphics g, int x, int y, int radius) {
        g.fillArc(x - radius, y - radius, 2 * radius, 2 * radius, 0, 360);
    }

    @Override
    public String toString() {
        return label;
    }
}
