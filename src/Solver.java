import java.awt.*;
import java.util.Collections;
import java.util.List;

public class Solver implements Runnable, Constants {
    private static final int FRAME_MARGIN = 5;
    private MainPanel mainPanel;

    public Solver(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    private void waitForNextStep() {
        mainPanel.waitForNextStep();
    }

    @Override
    public void run() {
        if(mainPanel.getPoints().size() == 0) {
            return;
        }

        Collections.sort(mainPanel.getPoints());
        int i = 0;
        for (Point p : mainPanel.getPoints()) {
            p.setLabel(Integer.toString(i));
            ++i;
        }
        mainPanel.refresh();
        waitForNextStep();
        recursive(mainPanel.getPoints());
    }

    private Voronoi recursive(List<Point> points) {
        for (Point p : mainPanel.getPoints()) {
            p.setColor(Color.GRAY);
        }
        for (Point p: points) {
            p.setColor(Color.BLUE);
        }
        mainPanel.refresh();
        waitForNextStep();
        if(points.size() > 2) {
            int pivot = points.size() / 2;
            List<Point> left = points.subList(0,pivot);
            List<Point> right = points.subList(pivot, points.size());

            Voronoi leftVoronoi = recursive(left);
            Voronoi rightVoronoi = recursive(right);
            for (Point point : rightVoronoi.getPoints()) {
                point.setColor(Color.GRAY);
            }
            for (Point point : leftVoronoi.getPoints()) {
                point.setColor(Color.GRAY);
            }
            leftVoronoi.setColor(Color.GRAY);
            rightVoronoi.setColor(Color.GRAY);
            mainPanel.refresh();
            waitForNextStep();
            leftVoronoi.setColor(Color.GREEN);
            rightVoronoi.setColor(SKYBLUE);
            for (Point point : leftVoronoi.getPoints()) {
                point.setColor(Color.GREEN);
            }
            for (Point point : rightVoronoi.getPoints()) {
                point.setColor(SKYBLUE);
            }
            mainPanel.refresh();
            waitForNextStep();
            Voronoi result = Voronoi.merge(leftVoronoi, rightVoronoi, mainPanel);
            result.setColor(Color.GRAY);
            mainPanel.getObjects().remove(leftVoronoi);
            mainPanel.getObjects().remove(rightVoronoi);
            mainPanel.getObjects().add(result);
            for(Point p : result.getPoints()) {
                p.setColor(Color.GRAY);
            }
            mainPanel.refresh();
            mainPanel.waitForNextStep();
            return result;
        } else {
            if(points.size() == 1) {
                Voronoi result = new Voronoi(points.get(0));
                result.setColor(Color.GRAY);
                mainPanel.getObjects().add(result);
                return result;
            } else {
                Voronoi result = new Voronoi(points.get(0), points.get(1));
                result.setColor(Color.GRAY);
                mainPanel.getObjects().add(result);
                return result;
            }
        }
    }
}
