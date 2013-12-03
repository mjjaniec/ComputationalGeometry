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
        Neighbour nA = new Neighbour(cellB, BOUNDS);
        Neighbour nB = new Neighbour(cellA, BOUNDS);
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
        Bisector bisector = new Bisector(leftCell.center, rightCell.center);
        Bisector first = bisector;

        while (true) {
            bisector.setColor(Color.RED);
            mainPanel.getObjects().add(bisector);
            mainPanel.refresh();
            mainPanel.waitForNextStep();
            bisector.setColor(Color.GRAY);


            Pair<Bisector, Point> leftIntersection = getLowermostIntersection(bisector, leftCell);
            Pair<Bisector, Point> rightIntersection = getLowermostIntersection(bisector, rightCell);

            if (leftIntersection == null && rightIntersection == null) {
                break;
            } else if (rightIntersection == null ||
                    leftIntersection.getSecond().getY() <= rightIntersection.getSecond().getY()) {
                Point leftCenter = leftIntersection.getFirst().getB();
                bisector = new Bisector(leftIntersection.getFirst().getB(), rightCell.center);
                VoronoiCell newLeftCell = l.getCell(leftCenter);
               /* Rectangle newBorders = new Rectangle(
                        (int) BOUNDS.getX(), (int) leftIntersection.getSecond().getX(),
                        (int) leftIntersection.getSecond().getX(), (int) (BOUNDS.getY() + BOUNDS.getHeight()));
                Neighbour toUpdate = leftCell.neighbours.get(bisector.getA());
                toUpdate.borders = toUpdate.borders.intersection(newBorders);*/
                leftCell.neighbours.put(newLeftCell.center, new Neighbour(newLeftCell, /*newBorders*/ BOUNDS));
                leftCell.center.setColor(Color.GREEN);
                leftCell = newLeftCell;
                leftCell.center.setColor(Color.RED);
            } else if (leftIntersection == null ||
                    rightIntersection.getSecond().getY() <= leftIntersection.getSecond().getY()) {
                Point rightCenter = rightIntersection.getFirst().getB();
                bisector = new Bisector(rightIntersection.getFirst().getB(), leftCell.center);
                VoronoiCell newRightCell = r.getCell(rightCenter);
                /*Rectangle newBorders = new Rectangle(
                        (int) rightIntersection.getSecond().getX(), (int) BOUNDS.getY(),
                        (int) (BOUNDS.getX() + BOUNDS.getWidth()), (int) (BOUNDS.getY() + BOUNDS.getHeight()));
                Neighbour toUpdate = rightCell.neighbours.get(bisector.getA());
                toUpdate.borders = toUpdate.borders.intersection(newBorders);*/
                rightCell.neighbours.put(newRightCell.center, new Neighbour(newRightCell, /*newBorders*/ BOUNDS));
                rightCell.center.setColor(SKYBLUE);
                rightCell = newRightCell;
                rightCell.center.setColor(Color.RED);
            }
        }

        for (VoronoiCell cell : l.cells.values()) {
            result.cells.put(cell.center, cell);
        }
        for (VoronoiCell cell : r.cells.values()) {
            result.cells.put(cell.center, cell);
        }

        mergeHulls(l, r, result, bisector, first);
        return result;
    }

    private static void mergeHulls(Voronoi l, Voronoi r, Voronoi result, Bisector bisector, Bisector first) {
        int lIndex = 0, rIndex = 0;
        int lSize = l.convexHull.size();
        int rSize = r.convexHull.size();

        result.convexHull.add(first.getA());
        result.convexHull.add(first.getB());
        rIndex = 0;
        while (r.convexHull.get(rIndex) != first.getB()) {
            rIndex = next(rIndex, rSize);
        }
        while (r.convexHull.get(rIndex) != bisector.getB()) {
            result.convexHull.add(r.convexHull.get(rIndex));
            rIndex = next(rIndex, rSize);
        }
        if (first != bisector) {
            result.convexHull.add(bisector.getB());
            result.convexHull.add(bisector.getA());
        }
        while (l.convexHull.get(lIndex) != bisector.getA()) {
            lIndex = prev(lIndex, lSize);
        }
        while (l.convexHull.get(lIndex) != first.getA()) {
            result.convexHull.add(l.convexHull.get(lIndex));
            lIndex = prev(lIndex, lSize);
        }
    }

    private static Pair<Bisector, Point> getLowermostIntersection(Bisector bisector, VoronoiCell cell) {
        Pair<Bisector, Point> result = new Pair<>(null, null);
        for (Neighbour n : cell.neighbours.values()) {
            Bisector b = new Bisector(cell.center, n.cell.center);
            List<Point> intersections = bisector.intersections(b);
            for (Point p : intersections) {
                if (result.getSecond() == null || result.getSecond().getY() > p.getY()) {
                    result = Pair.of(b, p);
                }
            }
        }
        if(result.getSecond() == null) {
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
            rHull.get(lIndex).setColor(Color.RED);
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
        g.setColor(color);
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
    }

    private static class Neighbour {
        public VoronoiCell cell;
        public Rectangle borders;

        public Neighbour(VoronoiCell cell, Rectangle borders) {
            this.cell = cell;
            this.borders = borders;
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
                drawBisector(g, center, neighbour.cell.center, neighbour.borders);
            }
        }

        private void drawBisector(Graphics g, Point a, Point b, Rectangle borders) {
            g.clipRect((int) borders.getX(), (int) borders.getY(), (int) borders.getWidth(), (int) borders.getHeight());
            new Bisector(a, b).draw(g);
        }
    }
}
