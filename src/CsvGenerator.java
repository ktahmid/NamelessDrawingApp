import javafx.scene.shape.*;

public class CsvGenerator {

    public static String getStr(Shape shape, int w, int h) {
        if (shape instanceof Line)
            return getStr((Line) shape, w, h);
        else if (shape instanceof Polyline)
            return getStr((Polyline) shape, w, h);
        else if (shape instanceof Arc)
            return getStr((Arc) shape, w, h);
        else if (shape instanceof Ellipse)
            return getStr((Ellipse) shape, w, h);
        else
            return "\n";
    }

    public static String getStr(Line line, int w, int h) {
        return "line," +
                translateX(line.getStartX(), w) + "," +
                translateY(line.getStartY(), h) + "," +
                translateX(line.getEndX(), w) + "," +
                translateY(line.getEndY(), h);
    }

    public static String getStr(Polyline polyline, int w, int h) {
        String str = "polyline";
        for (int i = 0; i < polyline.getPoints().size(); i += 2) {
            str += "," + translateX(polyline.getPoints().get(i), w);
            str += "," + translateX(polyline.getPoints().get(i + 1), h);
        }
        return str;
    }

    public static String getStr(Arc arc, int w, int h) {
        return "arc," +
                translateX(arc.getCenterX(), w) + "," +
                translateY(arc.getCenterY(), h) + "," +
                arc.getRadiusX() + "," +
                arc.getRadiusY() + "," +
                arc.getStartAngle();
    }

    public static String getStr(Ellipse ellipse, int w, int h) {
        return "ellipse," +
                translateX(ellipse.getCenterX(), w) + "," +
                translateY(ellipse.getCenterY(), h) + "," +
                ellipse.getRadiusX() + "," +
                ellipse.getRadiusY();
    }

    private static double translateX(double x, int width) {
        return x - width/2;
    }

    private static double translateY(double y, int height) {
        return height/2 - y;
    }
}
