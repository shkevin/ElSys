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
 	private ArrayList<Cabin> cabins;

	public buildingCanvas(ArrayList<Cabin> cabins, Controller controller) {
		this.controller = controller;
		this.cabins = cabins;
		widthProperty().addListener(event -> drawCanvas(cabins));
	 	widthProperty().addListener(event -> drawCanvas(cabins));
	}

	public void drawCanvas(ArrayList<Cabin> cabins) {

//		double w = widthProperty().get();
//		double h = heightProperty().get();
		double w = Main.WIDTH;
		double h = Main.HEIGHT;
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
		gc.fillRect(100, 0, w - 200, h - 100);

		//Draws the floors
		for (int x = 200; x < w - 200; x += 100) {
			for (int y = 20, p = 0; y < h - 100; y += 80, p++) {
				gc.setFill(Color.YELLOW);
				gc.fillRect(x, y, 55, 55);
			}
		}

		//Draw Elevator
		for (Cabin cabin : cabins) {
			gc.setFill(Color.BLACK);
			//This does nothing right now
			gc.fillRect(0, 0, 55, 55);
		}

	}

}
