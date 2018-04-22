package ElSys.operations.building;

import ElSys.Controller;
import ElSys.Main;
import ElSys.operations.cabin.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class BuildingCanvas extends Canvas {

	private Controller controller;
	private GraphicsContext gc;
	int startingFloor = 10;

	public BuildingCanvas(ArrayList<Cabin> cabins, Controller controller) {
		this.controller = controller;
		widthProperty().addListener(event -> drawCanvas(cabins));
	 	widthProperty().addListener(event -> drawCanvas(cabins));
	}

	public void drawCanvas(ArrayList<Cabin> cabins) {

//		double w = widthProperty().get();
//		double h = heightProperty().get();
		double w = Main.WIDTH;
		double h = Main.HEIGHT;
		int row = 100;

		gc = getGraphicsContext2D();
		gc.clearRect(0, 0, w, h);

		//Draw the sky
		gc.setFill(Color.LIGHTBLUE);
		gc.fillRect(0, 0, w, h);

		//Draw Ground
		gc.setFill(Color.SADDLEBROWN);
		gc.fillRect(0, w, w, 100);

		//Draw Building
		gc.setFill(Color.GREY);
		gc.fillRect(BuildSpecs.FLOOR_W_SEP, 0, w - BuildSpecs.FLOOR_WIDTH, h - BuildSpecs.FLOOR_HEIGHT);

		//Draws the floors
		for (int x = BuildSpecs.FLOOR_WIDTH; x < w - BuildSpecs.FLOOR_WIDTH; x += BuildSpecs.FLOOR_HEIGHT) {
			for (int y = BuildSpecs.FLOOR_H_SEP, p = 0; y < h - BuildSpecs.FLOOR_HEIGHT; y += BuildSpecs.FLOOR_W_SEP, p++) {
				gc.setFill(Color.YELLOW);
				gc.fillRect(x, y, BuildSpecs.CABIN_WIDTH, BuildSpecs.CABIN_HEIGHT);
			}
		}

		//Draw Elevator
		for (Cabin cabin : cabins) {
			double floor = (w - 60);
			if (cabin.getPosition() != 1.0) floor = ((w - 60) - ((cabin.getPosition() - 1) * 80));
			MotionTypes cabinMoving = MotionTypes.NOTMOVING;
			Motion cabinMotion = cabin.getMotion();
			if (cabinMotion != null) {
				cabinMoving = cabinMotion.getMotionType();
			}
			if(cabinMoving == MotionTypes.NOTMOVING) {
				gc.setFill(Color.BLACK);
			}else if (cabinMoving == MotionTypes.MOVINGUP) {
				gc.setFill(Color.GREEN);
			}else if (cabinMoving == MotionTypes.MOVINGDOWN) {
				gc.setFill(Color.RED);
			}else if (cabinMoving == MotionTypes.DOORSOPENING) {
				gc.setFill(Color.ORANGE);
			} else if (cabinMoving == MotionTypes.DOORSCLOSING) {
				gc.setFill(Color.ORANGE);
			}
			gc.fillRect(row += BuildSpecs.FLOOR_HEIGHT, floor, BuildSpecs.CABIN_WIDTH, BuildSpecs.CABIN_HEIGHT);
		}

	}

}
