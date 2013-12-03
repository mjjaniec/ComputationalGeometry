
public class Utils {

    private static double y_from_x_scalar(Point segment_p1, Point segment_p2, double x) {
            return segment_p1.getY() + (segment_p2.getY() - segment_p1.getY())
                    * ( x - segment_p1.getX()) / (segment_p2.getX() - segment_p1.getX());
    }
    public static Point intersection(Point s1p1, Point s1p2, Point s2p1, Point s2p2) {
        if(s1p1.compareTo(s1p2) > 0) {
            return intersection(s1p2, s1p1, s2p1, s2p2);
        }
        if(s2p1.compareTo(s2p2) > 0) {
            return intersection(s1p1, s1p2, s2p2, s2p1);
        }
        double x, y, x1, x2, x3, x4, y1, y2, y3, y4;
        x1 = s1p1.getX();
        y1 = s1p1.getY();
        x2 = s1p2.getX();
        y2 = s1p2.getY();
        x3 = s2p1.getX();
        y3 = s2p1.getY();
        x4 = s2p2.getX();
        y4 = s2p2.getY();

        double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (denominator == 0) {
            if (x2 - x1 == 0 && x1 == x3) {
                if (y1<=y3 && y3<=y2) {
                    return s2p1;
                }
                if (y1<=y4 && y4<=y2) {
                    return s2p2;
                }
            }
            if (x1<=x3 && x3<=x2 && y_from_x_scalar(s1p1, s1p2, x3) == y3) {
                return s2p1;
            }
            if (x1<=x4 && x4<=x2 && y_from_x_scalar(s1p1, s1p2, x4) == y4) {
                return s2p2;
            }

            return null;
        }

        x = (x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4);
        x /= denominator;

        if (! (x1 <= x && x <= x2 && x3 <= x && x <= x4)) {
            return null;
        }

        y = (x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4);

        return  new Point(x, y / denominator);
    }
}
