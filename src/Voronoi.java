import java.awt.*;
import java.util.*;
import java.util.List;

public class Voronoi implements Constants, Drawable {


    private List<Point> convexHull = new ArrayList<>();
    private Map<Point, VoronoiCell> cells = new HashMap<>();
    private Color color;

    private Voronoi() {

    }

    public Voronoi(Point a) {
        convexHull.add(a);
        cells.put(a, new VoronoiCell(a));
    }

    public Voronoi(Point a, Point b) {
        convexHull.add(a);
        convexHull.add(b);

        VoronoiCell cellA = new VoronoiCell(a);
        VoronoiCell cellB = new VoronoiCell(b);
        Neighbour nA = new Neighbour(cellB, BOUNDS, new Bisector(a, b));
        Neighbour nB = new Neighbour(cellA, BOUNDS, new Bisector(b, a));
        cellA.neighbours.put(b, nA);
        cellB.neighbours.put(a, nB);
        cells.put(a, cellA);
        cells.put(b, cellB);
    }

    /**
     * return index of lowermost point
     *
     * @param points
     * @return index
     */

    private static int findLowermost(List<Point> points) {
        int result = 0, index = 0;
        Point lowermost = points.get(0);
        for (Point p : points) {
            if (p.getY() > lowermost.getY()) {
                lowermost = p;
                result = index;
            }
            ++index;
        }
        return result;
    }

    private static boolean predicate(Pair<Bisector, Point> first, Pair<Bisector, Point> second) {
        return (first != null) &&
                (
                        second == null ||
                                first.getSecond().getY() <= second.getSecond().getY()
                );
    }

    private static Point getNewCenter(VoronoiCell left, VoronoiCell right, Pair<Bisector, Point> intersection) {
        Point candidate1 = intersection.getFirst().getA();
        Point candidate2 = intersection.getFirst().getB();

        if (candidate1.equals(left.center) || candidate1.equals(right.center)) {
            return candidate2;
        }
        return candidate1;
    }

    private static Pair<Bisector, Bisector> handleBisectors(Bisector bisector1, Bisector bisector2, Point clip,
                                                            VoronoiCell leftCell, VoronoiCell rightCell) {
        Rectangle bounds;
        bounds = new Rectangle((int) BOUNDS.getX() - 1, (int) clip.getY() - 1,
                (int) (BOUNDS.getX() + BOUNDS.getWidth() + 2), (int) (BOUNDS.getY() + BOUNDS.getHeight() + 2));


        bisector1.setColor(Color.GRAY);
        bisector2.setColor(Color.GRAY);

        if(bisector1.lower() == Bisector.Side.LEFT) {
            bounds = new Rectangle(
                    (int) BOUNDS.getX() - 1, (int) BOUNDS.getY() - 1,
                    (int) (BOUNDS.getX() + clip.getX() + 2), (int) BOUNDS.getHeight() + 2);
            bisector1.clip(bounds);
            bisector2.clip(bounds);
        } else if(bisector1.lower() == Bisector.Side.RIGHT) {
            bounds = new Rectangle(
                    (int) (BOUNDS.getX() + clip.getX() - 1), (int) BOUNDS.getY() - 1,
                    (int) (BOUNDS.getX() + BOUNDS.getWidth() + 2), (int) BOUNDS.getHeight() + 2);
            bisector1.clip(bounds);
            bisector2.clip(bounds);
        }

        bisector1 = new Bisector(leftCell.center, rightCell.center);
        bisector2 = new Bisector(rightCell.center, leftCell.center);

        bounds = new Rectangle((int) BOUNDS.getX() - 1, (int) BOUNDS.getY(),
                (int) (BOUNDS.getX() + BOUNDS.getWidth() + 2), (int) clip.getY() + 1);

        bisector1.setColor(Color.RED);
        bisector2.setColor(Color.RED);
        bisector1.clip(bounds);
        bisector2.clip(bounds);
        leftCell.neighbours.put(rightCell.center, new Neighbour(rightCell, BOUNDS, bisector1));
        rightCell.neighbours.put(leftCell.center, new Neighbour(leftCell, BOUNDS, bisector2));

        return Pair.of(bisector1, bisector2);
    }

    /**
     * Merge two Voronoi diagrams. IMPORTANT: diagrams l, r would be destroyed
     *
     * @param l         left
     * @param r         right
     * @param mainPanel
     * @return merged Voronoi diagram
     */
    public static Voronoi merge(Voronoi l, Voronoi r, MainPanel mainPanel) {
        Voronoi result = new Voronoi();

        Pair<Integer, Integer> startEdge = getStartEdge(
                findLowermost(l.convexHull), findLowermost(r.convexHull), l.convexHull, r.convexHull, mainPanel);

        VoronoiCell leftCell = l.getCell(l.convexHull.get(startEdge.getFirst()));
        VoronoiCell rightCell = r.getCell(r.convexHull.get(startEdge.getSecond()));
        Bisector bisector1 = new Bisector(leftCell.center, rightCell.center);
        Bisector bisector2 = new Bisector(rightCell.center, leftCell.center);
        Bisector first1 = bisector1;
        bisector1.setColor(Color.RED);
        bisector2.setColor(Color.RED);
        leftCell.neighbours.put(rightCell.center, new Neighbour(rightCell, BOUNDS, bisector1));
        rightCell.neighbours.put(leftCell.center, new Neighbour(leftCell, BOUNDS, bisector2));

        mainPanel.refresh();
        mainPanel.waitForNextStep();

        double last = Double.POSITIVE_INFINITY;

        while (true) {
            Pair<Bisector, Point> leftIntersection = getLowermostIntersection(bisector1, bisector2, leftCell, last);
            Pair<Bisector, Point> rightIntersection = getLowermostIntersection(bisector1, bisector2, rightCell, last);
            boolean any = false;

            if (leftIntersection == null && rightIntersection == null) {
                break;
            }
            if (predicate(leftIntersection, rightIntersection)) {
                any = true;
                Point leftCenter = getNewCenter(leftCell, rightCell, leftIntersection);

                Rectangle bounds = new Rectangle(
                        (int) BOUNDS.getX() - 1, (int) BOUNDS.getY() - 1,
                        (int) (BOUNDS.getX() + leftIntersection.getSecond().getX() + 2), (int) BOUNDS.getHeight() + 2);

                VoronoiCell newLeftCell = l.getCell(leftCenter);
                leftCell.neighbours.get(leftCenter).bisector.clip(bounds);
                newLeftCell.neighbours.get(leftCell.center).bisector.clip(bounds);
                leftCell.center.setColor(Color.GREEN);
                leftCell = newLeftCell;
                leftCell.center.setColor(Color.RED);

                Pair<Bisector, Bisector> pair =
                        handleBisectors(bisector1, bisector2, leftIntersection.getSecond(), leftCell, rightCell);
                bisector1 = pair.getFirst();
                bisector2 = pair.getSecond();

                mainPanel.refresh();
                mainPanel.waitForNextStep();
            }
            if (predicate(rightIntersection, leftIntersection)) {
                any = true;
                Point rightCenter = getNewCenter(leftCell, rightCell, rightIntersection);

                Rectangle bounds = new Rectangle(
                        (int) (BOUNDS.getX() + rightIntersection.getSecond().getX() - 1), (int) BOUNDS.getY() - 1,
                        (int) (BOUNDS.getX() + BOUNDS.getWidth() + 2), (int) BOUNDS.getHeight() + 2);

                VoronoiCell newRightCell = r.getCell(rightCenter);
                rightCell.neighbours.get(rightCenter).bisector.clip(bounds);
                newRightCell.neighbours.get(rightCell.center).bisector.clip(bounds);
                rightCell.center.setColor(SKYBLUE);
                rightCell = newRightCell;
                rightCell.center.setColor(Color.RED);

                Pair<Bisector, Bisector> pair =
                        handleBisectors(bisector1, bisector2, rightIntersection.getSecond(), leftCell, rightCell);
                bisector1 = pair.getFirst();
                bisector2 = pair.getSecond();

                leftCell.neighbours.put(rightCell.center, new Neighbour(rightCell, BOUNDS, bisector1));
                rightCell.neighbours.put(leftCell.center, new Neighbour(leftCell, BOUNDS, bisector2));

                mainPanel.refresh();
                mainPanel.waitForNextStep();
            }

            if (!any) {
                break;
            }

            if (leftIntersection != null) {
                last = Math.min(last, leftIntersection.getSecond().getY());
            }
            if (rightIntersection != null) {
                last = Math.min(last, rightIntersection.getSecond().getY());
            }

        }

        bisector1.setColor(Color.GRAY);
        bisector2.setColor(Color.GRAY);

        for (VoronoiCell cell : l.cells.values()) {
            result.cells.put(cell.center, cell);
        }
        for (VoronoiCell cell : r.cells.values()) {
            result.cells.put(cell.center, cell);
        }

        mergeHulls(l, r, result, bisector1, first1);
        return result;
    }

    private static Point extractPoint(Voronoi v, Bisector bisector) {
        if (v.getCell(bisector.getA()) != null) {
            return bisector.getA();
        } else {
            return bisector.getB();
        }
    }

    private static void mergeHulls(Voronoi l, Voronoi r, Voronoi result, Bisector bisector, Bisector first) {
        int lIndex = 0, rIndex = 0;
        int lSize = l.convexHull.size();
        int rSize = r.convexHull.size();


        rIndex = 0;
        Point p = extractPoint(r, first);
        while (r.convexHull.get(rIndex) != p) {
            rIndex = next(rIndex, rSize);
        }
        p = extractPoint(r, bisector);
        while (r.convexHull.get(rIndex) != p) {
            result.convexHull.add(r.convexHull.get(rIndex));
            rIndex = next(rIndex, rSize);
        }
        result.convexHull.add(p);

        p = extractPoint(l, bisector);
        while (l.convexHull.get(lIndex) != p) {
            lIndex = prev(lIndex, lSize);
        }
        p = extractPoint(l, first);
        while (l.convexHull.get(lIndex) != p) {
            result.convexHull.add(l.convexHull.get(lIndex));
            lIndex = prev(lIndex, lSize);
        }
        result.convexHull.add(p);
    }

    private static Pair<Bisector, Point> getLowermostIntersection(
            Bisector bisector1, Bisector bisector2, VoronoiCell cell, double last) {
        Pair<Bisector, Point> result = new Pair<>(null, null);
        for (Neighbour n : cell.neighbours.values()) {

            List<Point> intersections = new ArrayList<>();
            if (n.bisector == bisector1 || n.bisector == bisector2) {
                continue;
            }
            intersections.addAll(bisector1.intersections(n.bisector));
            intersections.addAll(bisector2.intersections(n.bisector));
            for (Point p : intersections) {
                if (p.getY() < last && (result.getSecond() == null || result.getSecond().getY() > p.getY())) {
                    result = Pair.of(n.bisector, p);
                }
            }
        }
        if (result.getSecond() == null) {
            return null;
        }
        return result;
    }

    private static int prev(int index, int size) {
        return (size + index - 1) % size;
    }

    private static int next(int index, int size) {
        return (index + 1) % size;
    }

    private static Pair<Integer, Integer> getStartEdge(int lIndex, int rIndex, List<Point> lHull, List<Point> rHull,
                                                       MainPanel mainPanel) {
        int lSize = lHull.size(), rSize = rHull.size();
        boolean isLeftOK = false, isRightOK = false;

        boolean change = true;
        LimitingCircle lc = null;
        while (!isLeftOK || !isRightOK) {

            if (lc != null) {
                mainPanel.getObjects().remove(lc);
            }

            lc = new LimitingCircle(lHull.get(lIndex), rHull.get(rIndex));
            lHull.get(lIndex).setColor(Color.RED);
            rHull.get(rIndex).setColor(Color.RED);
            mainPanel.getObjects().add(lc);

            if (change) {
                mainPanel.refresh();
                mainPanel.waitForNextStep();
                change = false;
            }

            if (!isLeftOK) {
                if (lSize == 1 || !lc.contains(lHull.get(prev(lIndex, lSize)))) {
                    isLeftOK = true;
                } else {
                    change = true;
                    lIndex = prev(lIndex, lSize);
                    isRightOK = false;
                }
            } else if (!isRightOK) {
                if (rSize == 1 || !lc.contains(rHull.get(next(rIndex, rSize)))) {
                    isRightOK = true;
                } else {
                    change = true;
                    rIndex = next(rIndex, rSize);
                    isLeftOK = false;
                }
            }
        }

        mainPanel.getObjects().remove(lc);
        return Pair.of(lIndex, rIndex);
    }

    @Override
    public void draw(Graphics g) {
        if (color != null) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        }
        Point prev = convexHull.get(convexHull.size() - 1);
        for (Point p : convexHull) {
            g.drawLine((int) prev.getX(), (int) prev.getY(), (int) p.getX(), (int) p.getY());
            prev = p;
        }
        if (color != null) {
            g.setColor(color);
        }
        for (VoronoiCell cell : cells.values()) {
            cell.draw(g);
        }

    }

    public Collection<Point> getPoints() {
        return cells.keySet();
    }

    private VoronoiCell getCell(Point point) {
        return cells.get(point);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        for(VoronoiCell cell : cells.values()) {
            for(Neighbour neighbour : cell.neighbours.values()) {
                neighbour.bisector.setColor(null);
            }
        }
    }

    private static class Neighbour {
        public VoronoiCell cell;
        public Bisector bisector;

        public Neighbour(VoronoiCell cell, Rectangle borders, Bisector bisector) {
            this.cell = cell;
            this.bisector = bisector;
        }
    }

    private static class VoronoiCell implements Drawable {
        public Point center;
        public Map<Point, Neighbour> neighbours;

        public VoronoiCell(Point center) {
            this.center = center;
            neighbours = new HashMap<>();
        }

        @Override
        public void draw(Graphics g) {
            for (Neighbour neighbour : neighbours.values()) {
                neighbour.bisector.draw(g);
            }
        }
    }
}
