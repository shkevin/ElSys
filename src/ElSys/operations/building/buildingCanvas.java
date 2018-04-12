package ElSys.operations.building;

import ElSys.Controller;
import ElSys.Main;
import ElSys.operations.cabin.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class buildingCanvas extends Canvas {

	private Controller controller;
	private GraphicsContext gc;
	int startingFloor = 10;

	public buildingCanvas(ArrayList<Cabin> cabins, Controller controller) {
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
		gc.setFill(Color.BROWN);
		gc.fillRect(0, w, w, 100);

		//Draw Building
		gc.setFill(Color.GREY);
		gc.fillRect(buildSpecs.FLOOR_W_SEP, 0, w - buildSpecs.FLOOR_WIDTH, h - buildSpecs.FLOOR_HEIGHT);

		//Draws the floors
		for (int x = buildSpecs.FLOOR_WIDTH; x < w - buildSpecs.FLOOR_WIDTH; x += buildSpecs.FLOOR_HEIGHT) {
			for (int y = buildSpecs.FLOOR_H_SEP, p = 0; y < h - buildSpecs.FLOOR_HEIGHT; y += buildSpecs.FLOOR_W_SEP, p++) {
				gc.setFill(Color.YELLOW);
				gc.fillRect(x, y, buildSpecs.CABIN_WIDTH, buildSpecs.CABIN_HEIGHT);
			}
		}

		//Draw Elevator
		for (Cabin cabin : cabins) {
			double floor = (w - 60);
			if (cabin.getFloor() != 1) floor = ((w - 60) - ((cabin.getPosition() - 1) * 80));
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
			}else if (cabinMoving == MotionTypes.DOORS) {
				gc.setFill(Color.ORANGE);
			}
			gc.fillRect(row += buildSpecs.FLOOR_HEIGHT, floor, buildSpecs.CABIN_WIDTH, buildSpecs.CABIN_HEIGHT);
		}

	}

}
