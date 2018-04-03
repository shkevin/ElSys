package ElSys.operations.building;

import ElSys.Controller;
import ElSys.Main;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * @author snord
 */
public class buildingCanvas extends Canvas {

	private Controller controller;
	private GraphicsContext gc;
 	int startingFloor = 10;

	public buildingCanvas(Controller controller) {
		this.controller = controller;
		widthProperty().addListener(event -> drawCanvas());
	 	widthProperty().addListener(event -> drawCanvas());
	}

	public void drawCanvas() {

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
	}

}
