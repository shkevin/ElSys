package ElSys.operations.building;

import ElSys.Controller;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * @author snord
 */
public class buildingHandler {

	private Controller controller;
	private buildingCanvas canvas;

	public buildingHandler(buildingCanvas canvas, Controller controller) {
		this.controller = controller;
		this.canvas = canvas;
	}

	/**
	 * Handles mouse over on Canvas. Used for getting current location
	 * and would've been used with implemented zoom on cursor.
	 */
	private EventHandler<MouseEvent> onMouseEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {

			//Finds the column that we are in within the 2D array
			if (event.getX() >= 200 && event.getX() < canvas.getWidth() - 200) {
				int column = ((int) event.getX() - 200) / (100);
//				System.out.print("Col: " + column + " ");
			}

			//Finds the row that we are in within the 2D array
			if (event.getY() >= 0 && event.getY() <= canvas.getHeight() - 100) {
				int row = ((int) event.getY()) / (80);
//				System.out.print("Row: " + row);
			}

//			System.out.println();
		}
	};

	/**
	 * simple getter for mouse handler within canvas.
	 * @return mouse handler
	 */
	public EventHandler<MouseEvent> getOnMouseEventHandler() {
		return onMouseEventHandler;
	}

}
