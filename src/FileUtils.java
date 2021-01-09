import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUtils {

    private String getSvgCode() {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xml:space=\"preserve\">\n" +
                "</svg>\n";
    }

    public static void saveToFile(File outFile, Pane canvas) {
        try (PrintWriter fileWriter = new PrintWriter(outFile)) {
            String fileContent = produceCsv(canvas);
            fileWriter.print(fileContent);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String produceCsv(Pane canvas) {
        StringBuilder csvBuilder = new StringBuilder();
        for (int i = 0; i < canvas.getChildren().size(); i++) {
            csvBuilder.append(ShapeStringGenerator.getStr(
                    (Shape) canvas.getChildren().get(i),
                    (int) canvas.getPrefWidth(),
                    (int) canvas.getPrefHeight()
            )).append("\n");
        }
        return csvBuilder.toString();
    }

    public static void exportToSvgFile(File svgFile, Pane canvas) {
        try (PrintWriter svgWriter = new PrintWriter(svgFile)) {
            String svgCode = produceSvg(canvas);
            svgWriter.print(svgCode);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String produceSvg(Pane canvas) {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xml:space=\"preserve\">\n" +
                "</svg>\n";
    }

    public static void readFile(File outFile, Pane canvas) {

    }
}