import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class MainPanel extends JPanel {
    private List<Point> points;
    private Semaphore semaphore;
    private MouseHandler mouseHandler;
    private Set<Drawable> objects;

    public MainPanel() {
        objects = new HashSet<>();
        semaphore = new Semaphore(8);
        points = new ArrayList<>();
        setPreferredSize(new Dimension(600, 400));
        mouseHandler = new MouseHandler(this);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void clear() {
        semaphore.release(1000);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        points.clear();
        objects.clear();
        repaint();
    }

    public void refresh() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    repaint();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public void load() {
        points.clear();
        int[] x, y;
        x = new int[]{50, 100, 200, 200, 200, 300, 300, 400, 550};
        y = new int[]{200, 350, 50, 200, 300, 100, 200, 50, 200};
//        x = new int[]{100, 200, 100};
//        y = new int[]{100, 100, 200};

//        x = new int[]{300, 200, 500, 500, 300, 200};
//        y = new int[]{100, 350, 100, 300, 200, 160};
        for (int i = 0; i < x.length; ++i) {
            points.add(new Point(x[i], y[i]));
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (Point p : points) {
            p.draw(g);
        }

        for (Drawable drawable : objects) {
            drawable.draw(g);
        }
    }

    public List<Point> getPoints() {
        return points;
    }

    public void start() {
        removeMouseListener(mouseHandler);
        removeMouseMotionListener(mouseHandler);
        semaphore.release(15);

        new Thread(new Runnable() {

            @Override
            public void run() {
                GraphicsViewDelegate delegate = new GraphicsViewDelegate() {
                    @Override
                    public Set<Drawable> getObjects() {
                        return objects;
                    }

                    @Override
                    public void refresh() {
                        MainPanel.this.refresh();
                    }

                    @Override
                    public void waitForNextStep() {
                        MainPanel.this.waitForNextStep();
                    }
                };
                Voronoi.compute(points, delegate);
                refresh();
            }
        }).start();
    }

    public void step() {
        semaphore.release();
    }

    public void waitForNextStep() {
        semaphore.acquireUninterruptibly();
    }

    private static class MouseHandler implements MouseListener, MouseMotionListener {
        private static final int RADIUS = 5;
        private MainPanel mainPanel;
        private Point glued;

        public MouseHandler(MainPanel mainPanel) {
            this.mainPanel = mainPanel;
        }

        boolean isHit(Point p, MouseEvent e) {
            return Math.abs(p.getX() - e.getX()) + Math.abs(p.getY() - e.getY()) < RADIUS;
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            for (Point p : mainPanel.getPoints()) {
                if (isHit(p, e)) {
                    glued = p;
                    return;
                }
            }
            glued = new Point(e.getX(), e.getY());
            mainPanel.getPoints().add(glued);
            mainPanel.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (glued != null) {
                glued = null;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            glued = null;
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (glued != null) {
                mainPanel.getPoints().remove(glued);
                glued = null;
                mainPanel.repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (glued != null) {
                glued.setX(e.getX());
                glued.setY(e.getY());
                mainPanel.repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }
}
