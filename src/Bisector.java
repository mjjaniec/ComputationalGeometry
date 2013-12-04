import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Bisector implements Drawable, Constants {
    private Point a, b;
    private List<Point> chain;
    private Color color;

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

    private Point fst() {
        return chain.get(0);
    }

    private Point lst() {
        return chain.get(chain.size() - 1);
    }

    public Side lower() {
        if(fst().getY() > lst().getY() && fst().getX() < lst().getX()) {
            return Side.LEFT;
        }
        if(fst().getY() > lst().getY() && fst().getX() > lst().getX()) {
            return Side.RIGHT;
        }
        if(fst().getY() < lst().getY() && fst().getX() < lst().getX()) {
            return Side.RIGHT;
        }
        if(fst().getY() < lst().getY() && fst().getX() > lst().getX()) {
            return Side.LEFT;
        }
        return Side.NONE;
    }

    public Side lefter() {
        if(fst().getY() > lst().getY() && fst().getX() < lst().getX()) {
            return Side.BOTTOM;
        }
        if(fst().getY() > lst().getY() && fst().getX() > lst().getX()) {
            return Side.TOP;
        }
        if(fst().getY() < lst().getY() && fst().getX() < lst().getX()) {
            return Side.TOP;
        }
        if(fst().getY() < lst().getY() && fst().getX() > lst().getX()) {
            return Side.BOTTOM;
        }
        return Side.NONE;
    }


    public Point getA() {
        return a;
    }

    public Point getB() {
        return b;
    }

    @Override
    public void draw(Graphics g) {
        Color oldColor = g.getColor();
        if (getColor() != null) {
            g.setColor(getColor());
        }
        for (int i = 1; i < chain.size(); ++i) {
            drawLine(g, chain.get(i - 1), chain.get(i));
        }
        g.setColor(oldColor);
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
                        chain.get(i - 1), chain.get(i), bisector.chain.get(j - 1), bisector.chain.get(j));
                if (oneIntersection != null) {
                    result.add(oneIntersection);
                }
            }
        }
        return result;
    }

    public void clip(Rectangle r) {
        Point prev = null;
        boolean started = false;
        List<Point> toRemove = new ArrayList<>();
        for (Point p : chain) {
            if (prev == null) {
                prev = p;
                continue;
            }

            List<Point> inter = intersects(r, prev, p);
            if (inter.size() == 2) {
                chain = inter;
                return;
            }

            if (inter.size() == 1) {
                if (!started) {
                    started = true;
                    Point i = inter.get(0);
                    prev.setX(i.getX());
                    prev.setY(i.getY());
                } else {
                    Point i = inter.get(0);
                    p.setX(i.getX());
                    p.setY(i.getY());
                }
            } else if (!r.contains((int) p.getX(), (int) p.getY())) {
                if (started) {
                    toRemove.add(p);
                } else {
                    toRemove.add(prev);
                }
            } else {
                started = true;
            }
            prev = p;
        }

        chain.removeAll(toRemove);
    }

    private List<Point> intersects(Rectangle r, Point p, Point prev) {
        Point p1, p2, p3, p4;
        p1 = Utils.intersection(new Point(r.getX(), r.getY()), new Point(r.getX() + r.getWidth(), r.getY()), p, prev);
        p2 = Utils.intersection(new Point(r.getX() + r.getWidth(), r.getY()), new Point(r.getX() + r.getWidth(), r.getY() + r.getHeight()), p, prev);
        p3 = Utils.intersection(new Point(r.getX() + r.getWidth(), r.getY() + r.getHeight()), new Point(r.getX(), r.getY() + r.getHeight()), p, prev);
        p4 = Utils.intersection(new Point(r.getX(), r.getY()), new Point(r.getX(), r.getY() + r.getHeight()), p, prev);

        return Utils.notNullList(p1, p2, p3, p4);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public enum Side {
        NONE, TOP, BOTTOM, LEFT, RIGHT;
    }
}
