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
			//This interprets mouse clicks as a command for that elevator to go to that floor
			//Mostly for testing
			int elevator = -1;
			if (event.getX() >= buildSpecs.floorWidth && event.getX() < canvas.getWidth() - buildSpecs.floorWidth) {
				elevator = ((int) event.getX() - buildSpecs.floorWidth) / (buildSpecs.floorHeight);
			}

			//Finds the row that we are in within the 2D array
			int floor = -1;
			if (event.getY() >= 0 && event.getY() <= canvas.getHeight() - buildSpecs.floorHeight) {
				floor = 9 - ((int) event.getY()) / (buildSpecs.floorWSeparator);
			}

			if (floor >= 0 && elevator >= 0) {
				System.out.println("Move elevator " + elevator + " to " + floor);
				controller.moveElevator(elevator, floor);
			}
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
