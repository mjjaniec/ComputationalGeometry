import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class Solver implements Runnable {
    private static final int FRAME_MARGIN = 5;
    private static final Color SKYBLUE = new Color(20,120,255);
    private MainPanel mainPanel;

    public Solver(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    private void waitForNextStep() {
        mainPanel.waitForNextStep();
    }

    @Override
    public void run() {
        waitForNextStep();
        Collections.sort(mainPanel.getPoints());
        int i = 0;
        for (Point p : mainPanel.getPoints()) {
            p.setLabel(Integer.toString(i));
            ++i;
        }
        recursive(mainPanel.getPoints());
    }

    private Voronoi recursive(List<Point> points) {
        if(points.size() > 3) {
            int pivot = points.size() / 2;
            List<Point> left = points.subList(0,pivot);
            List<Point> right = points.subList(pivot, points.size());
            Rectangle leftFrame = getFrame(left);
            Rectangle rightFrame = getFrame(right);

            mainPanel.getFrames().add(leftFrame);
            Voronoi leftVoronoi = recursive(left);
            mainPanel.getFrames().remove(leftFrame);
            mainPanel.getFrames().add(rightFrame);
            Voronoi rightVoronoi = recursive(right);
            mainPanel.getFrames().remove(rightFrame);
            return Voronoi.merge(leftVoronoi, rightVoronoi, mainPanel);
        } else {
            if(points.size() == 1) {
                return new Voronoi(points.get(0));
            } else {
                return new Voronoi(points.get(0), points.get(1));
            }
        }
    }



    private Rectangle getFrame(List<Point> points) {
        double xMin = Double.POSITIVE_INFINITY, yMin = Double.POSITIVE_INFINITY;
        double xMax = Double.NEGATIVE_INFINITY, yMax = Double.NEGATIVE_INFINITY;

        for (Point p : points) {
            xMin = Math.min(xMin, p.getX());
            yMin = Math.min(yMin, p.getY());
            xMax = Math.max(xMax, p.getX());
            yMax = Math.max(yMax, p.getY());
        }

        return new Rectangle((int)xMin - FRAME_MARGIN, (int)yMin - FRAME_MARGIN,
                (int)(xMax - xMin + 2 * FRAME_MARGIN), (int)(yMax - yMin + 2 * FRAME_MARGIN));
    }

}
