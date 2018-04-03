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
			if (event.getX() >= buildSpecs.FLOOR_WIDTH && event.getX() < canvas.getWidth() - buildSpecs.FLOOR_WIDTH) {
				elevator = (((int) event.getX() - buildSpecs.FLOOR_WIDTH) / (buildSpecs.FLOOR_HEIGHT));
			}

			//Finds the row that we are in within the 2D array
			int floor = -1;
			if (event.getY() >= 0 && event.getY() <= canvas.getHeight() - buildSpecs.FLOOR_HEIGHT) {
				floor = (buildSpecs.MAX_FLOORS - ((int) event.getY()) / (buildSpecs.FLOOR_W_SEP));
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
