package ElSys.operations.cabin;

import ElSys.Controller;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class DoorCanvas extends Canvas {

    private GraphicsContext gc;
    private List<Cabin> cabins;
    private Controller controller;

    public DoorCanvas(List<Cabin> cabins, Controller controller) {
        this. cabins = cabins;
        this.controller = controller;
        widthProperty().addListener(event -> drawDoors(0));
        heightProperty().addListener(event -> drawDoors(0));
        for (Cabin cabin : cabins) {
            cabin.setDoorValue(200);
        }
    }

    public void drawDoors(int cabin) {
        double w = widthProperty().get();
        double h = heightProperty().get();
        double elevWPlacement = 50;
        double elevHPlacement = 50;
        double doorX = elevWPlacement / 2;
        double doorY = elevHPlacement / 2;

        gc = getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.BLACK);

        gc.fillRect(doorX, doorY, w-elevWPlacement, h-elevHPlacement);

        Motion cabinMotion = cabins.get(cabin).getMotion();
        MotionTypes	cabinMoving = cabinMotion.getMotionType();

        //Draw the doors
        double width = controller.getCabins().get(cabin).getDoorValue();
        if (cabinMoving == MotionTypes.DOORS) {
            gc.setFill(Color.ORANGE);
            gc.fillRect(doorX, doorY, width, h-elevHPlacement);
            controller.getCabins().get(cabin).setDoorValue(width-20);
        }
    }
}