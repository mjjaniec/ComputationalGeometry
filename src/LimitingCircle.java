import java.awt.*;

public class LimitingCircle implements Drawable, Constants {
    private static final Color COLOR = new Color(200, 200, 200);
    private Point a, b;

    // a:  y = +x + a.y - a.x
    // b:  y = -x + b.y + b.x
    public LimitingCircle(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    public boolean contains(Point x) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        if (Math.abs(dx) > Math.abs(dy)) {
            if (a.compareTo(b) < 0) {
                return x.getY() > x.getX() + b.getY() - b.getX() && x.getY() > -x.getX() + a.getY() + a.getX();
            } else {
                return x.getY() < x.getX() + b.getY() - b.getX() && x.getY() < -x.getX() + a.getY() + a.getX();
            }
        } else if (Math.abs(dy) > Math.abs(dx)) {
            if (a.getY() < b.getY()) {
                return x.getY() > x.getX() + a.getY() - a.getX() && x.getY() < -x.getX() + b.getY() + b.getX();
            } else {
                return x.getY() < x.getX() + a.getY() - a.getX() && x.getY() > -x.getX() + b.getY() + b.getX();
            }
        } else {
            if (dx > 0) {
                if (dy > 0) {
                    return x.getY() > x.getX() + a.getY() - a.getX();
                } else {
                    return x.getY() > -x.getX() + a.getY() + a.getX();
                }
            } else {
                if (dy > 0) {
                    return x.getY() < -x.getX() + a.getY() + a.getX();
                } else {
                    return x.getY() < x.getX() + a.getY() - a.getX();
                }
            }
        }
    }

    public void draw(Graphics g) {

        g.setColor(COLOR);

        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();

        if (Math.abs(dx) > Math.abs(dy)) {
            double x = 0.5 * (b.getX() + a.getX() - b.getY() + a.getY());
            double y = 0.5 * (b.getY() + a.getY() - b.getX() + a.getX());
            if (a.compareTo(b) < 0) {
                double x0 = BOUNDS.getX();
                double y0 = -x0 + a.getY() + a.getX();
                g.drawLine((int) x0, (int) y0, (int) x, (int) y);
                x0 = BOUNDS.getX() + BOUNDS.getWidth();
                y0 = x0 + b.getY() - b.getX();
                g.drawLine((int) x, (int) y, (int) x0, (int) y0);
            } else {
                double x0 = BOUNDS.getX();
                double y0 = +x0 + b.getY() - b.getX();
                g.drawLine((int) x0, (int) y0, (int) x, (int) y);
                x0 = BOUNDS.getX() + BOUNDS.getWidth();
                y0 = -x0 + a.getY() + a.getX();
                g.drawLine((int) x, (int) y, (int) x0, (int) y0);
            }
        } else if (Math.abs(dy) > Math.abs(dx)) {
            double x = 0.5 * (a.getX() + b.getX() - a.getY() + b.getY());
            double y = 0.5 * (a.getY() + b.getY() - a.getX() + b.getX());
            if (a.getY() < b.getY()) {
                double y0 = BOUNDS.getY();
                double x0 = y0 - a.getY() + a.getX();
                g.drawLine((int) x0, (int) y0, (int) x, (int) y);
                y0 = BOUNDS.getY() + BOUNDS.getHeight();
                x0 = -y0 + b.getY() + b.getX();
                g.drawLine((int) x, (int) y, (int) x0, (int) y0);
            } else {
                double y0 = BOUNDS.getY();
                double x0 = -y0 + b.getY() + b.getX();
                g.drawLine((int) x0, (int) y0, (int) x, (int) y);
                y0 = BOUNDS.getY() + BOUNDS.getHeight();
                x0 = y0 - a.getY() + a.getX();
                g.drawLine((int) x, (int) y, (int) x0, (int) y0);
            }
        } else { // abs(dx) == abs(dy)
            if (dx * dy > 0) {
                double x0 = BOUNDS.getX();
                double y0 = x0 + a.getY() - a.getX();
                double x1 = BOUNDS.getX() + BOUNDS.getWidth();
                double y1 = x1 + a.getY() - a.getX();
                g.drawLine((int) x0, (int) y0, (int) x1, (int) y1);
            } else {
                double x0 = BOUNDS.getX();
                double y0 = -x0 + a.getY() + a.getX();
                double x1 = BOUNDS.getX() + BOUNDS.getWidth();
                double y1 = -x1 + a.getY() + a.getX();
                g.drawLine((int) x0, (int) y0, (int) x1, (int) y1);
            }
        }
    }


}
