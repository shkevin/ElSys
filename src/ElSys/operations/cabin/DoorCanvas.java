package ElSys.operations.cabin;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DoorCanvas extends Canvas {
    private GraphicsContext gc;


    public DoorCanvas() {
        widthProperty().addListener(event -> drawDoors(0));
        heightProperty().addListener(event -> drawDoors(0));
    }

    public void drawDoors(int Cabin) {
        double w = widthProperty().get();
        double h = heightProperty().get();

        gc = getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        gc.setFill(Color.GREY);
        gc.fillRect(0, 0, w, h);
    }
}
