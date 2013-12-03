import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Bisector implements Drawable, Constants {
    private static final Color COLOR = new Color(47, 255, 91);
    private Point a, b;
    private List<Point> chain;
    private Color color;

    public Point getA() {
        return a;
    }

    public Point getB() {
        return b;
    }

    public Bisector(Point a, Point b) {
        this.a = a;
        this.b = b;
        chain = new ArrayList<>();

        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();


        double midX = (a.getX() + b.getX()) / 2;
        double midY = (a.getY() + b.getY()) / 2;

        if (dx == 0) {
            chain.add(new Point(BOUNDS.getX(), midY));
            chain.add(new Point(BOUNDS.getX() + BOUNDS.getWidth(), midY));
        } else if (dy == 0) {
            chain.add(new Point(midX, BOUNDS.getY()));
            chain.add(new Point(midX, BOUNDS.getY() + BOUNDS.getHeight()));
        } else if (Math.abs(dx) > Math.abs(dy)) {
            double x = a.getX() + (dx - dy) / 2;
            if (dx > 0) {
                if (dy > 0) {
                    chain.add(new Point(x + dy, BOUNDS.getY()));
                    chain.add(new Point(x + dy, a.getY()));
                    chain.add(new Point(x, b.getY()));
                    chain.add(new Point(x, BOUNDS.getY() + BOUNDS.getHeight()));
                } else {
                    chain.add(new Point(x, BOUNDS.getY() + BOUNDS.getHeight()));
                    chain.add(new Point(x, a.getY()));
                    chain.add(new Point(x + dy, b.getY()));
                    chain.add(new Point(x + dy, BOUNDS.getY()));
                }
            } else {
                if (dy > 0) {
                    chain.add(new Point(x, BOUNDS.getY()));
                    chain.add(new Point(x, a.getY()));
                    chain.add(new Point(x + dy, b.getY()));
                    chain.add(new Point(x + dy, BOUNDS.getY() + BOUNDS.getHeight()));
                } else {
                    chain.add(new Point(x + dy, BOUNDS.getY() + BOUNDS.getHeight()));
                    chain.add(new Point(x + dy, a.getY()));
                    chain.add(new Point(x, b.getY()));
                    chain.add(new Point(x, BOUNDS.getY()));
                }
            }
        } else if (Math.abs(dy) > Math.abs(dx)) {
            double y = a.getY() + (dy - dx) / 2;
            if (dy < 0) {
                if (dx < 0) {
                    chain.add(new Point(BOUNDS.getX() + BOUNDS.getWidth(), y + dx));
                    chain.add(new Point(a.getX(), y + dx));
                    chain.add(new Point(b.getX(), y));
                    chain.add(new Point(BOUNDS.getX(), y));
                } else {
                    chain.add(new Point(BOUNDS.getX(), y));
                    chain.add(new Point(a.getX(), y));
                    chain.add(new Point(b.getX(), y + dx));
                    chain.add(new Point(BOUNDS.getX() + BOUNDS.getWidth(), y + dx));
                }
            } else {
                if (dx > 0) {
                    chain.add(new Point(BOUNDS.getX(), y + dx));
                    chain.add(new Point(a.getX(), y + dx));
                    chain.add(new Point(b.getX(), y));
                    chain.add(new Point(BOUNDS.getX() + BOUNDS.getWidth(), y));
                } else {
                    chain.add(new Point(BOUNDS.getX() + BOUNDS.getWidth(), y));
                    chain.add(new Point(a.getX(), y));
                    chain.add(new Point(b.getX(), y + dx));
                    chain.add(new Point(BOUNDS.getX(), y + dx));
                }
            }
        } else {
            if (dx > 0) {
                if (dy > 0) {
                    chain.add(new Point(a.getX(), BOUNDS.getY() + BOUNDS.getHeight()));
                    chain.add(new Point(a.getX(), b.getY()));
                    chain.add(new Point(b.getX(), a.getY()));
                    chain.add(new Point(BOUNDS.getX() + BOUNDS.getWidth(), a.getY()));
                } else {
                    chain.add(new Point(a.getX(), BOUNDS.getY()));
                    chain.add(new Point(a.getX(), b.getY()));
                    chain.add(new Point(b.getX(), a.getY()));
                    chain.add(new Point(BOUNDS.getX() + BOUNDS.getWidth(), a.getY()));
                }
            } else {
                if (dy > 0) {
                    chain.add(new Point(a.getX(), BOUNDS.getY() + BOUNDS.getHeight()));
                    chain.add(new Point(a.getX(), b.getY()));
                    chain.add(new Point(b.getX(), a.getY()));
                    chain.add(new Point(BOUNDS.getX(), a.getY()));
                } else {
                    chain.add(new Point(a.getX(), BOUNDS.getY()));
                    chain.add(new Point(a.getX(), b.getY()));
                    chain.add(new Point(b.getX(), a.getY()));
                    chain.add(new Point(BOUNDS.getX(), a.getY()));
                }
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        if(getColor() != null) {
            g.setColor(getColor());
        }
        for (int i = 1; i < chain.size(); ++i) {
            drawLine(g, chain.get(i - 1), chain.get(i));
        }

    }

    private void drawLine(Graphics g, Point a, Point b) {
        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY());
    }

    public List<Point> intersections(Bisector bisector) {
        List<Point> result = new ArrayList<>();
        int iLim = chain.size(), jLim = bisector.chain.size();
        for (int i = 1; i < iLim; ++i) {
            for (int j = 1; j < jLim; ++j) {
                Point oneIntersection = Utils.intersection(
                        chain.get(i-1), chain.get(i), bisector.chain.get(j), bisector.chain.get(j));
                if(oneIntersection != null) {
                    result.add(oneIntersection);
                }
            }
        }
        return result;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
