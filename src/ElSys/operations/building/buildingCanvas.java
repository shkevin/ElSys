package ElSys.operations.building;

import ElSys.Controller;
import ElSys.Main;
import ElSys.operations.cabin.Cabin;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * @author snord
 */
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
		gc.fillRect(0, 800, w, 100);

		//Draw Building
		gc.setFill(Color.GREY);
		gc.fillRect(100, 0, w - buildSpecs.floorWidth, h - buildSpecs.floorHeight);

		//Draws the floors
		for (int x = buildSpecs.floorWidth; x < w - buildSpecs.floorWidth; x += buildSpecs.floorHeight) {
			for (int y = buildSpecs.floorHSeparator, p = 0; y < h - buildSpecs.floorHeight; y += buildSpecs.floorWSeparator, p++) {
				gc.setFill(Color.YELLOW);
				gc.fillRect(x, y, buildSpecs.cabinWidth, buildSpecs.cabinHeight);
			}
		}

		//Draw Elevator
		for (Cabin cabin : cabins) {
			gc.setFill(Color.BLACK);
			gc.fillRect(row += buildSpecs.floorHeight, ((9 - cabin.getFloor() + w) - 55), buildSpecs.cabinWidth, buildSpecs.cabinHeight);
		}

	}

}
