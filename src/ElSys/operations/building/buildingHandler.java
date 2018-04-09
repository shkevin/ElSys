package ElSys.operations.building;

import ElSys.Controller;
import ElSys.operations.cabin.Cabin;
import ElSys.operations.cabin.MotionTypes;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class buildingHandler implements Runnable{

	private Controller controller;
	private buildingCanvas canvas;
	private HashMap<Cabin,ArrayList<Integer>> CabinSchedules = new HashMap<>();
	private Thread buildingThread = new Thread(this,"buildingThread");
	public buildingHandler(buildingCanvas canvas, Controller controller) {
		this.controller = controller;
		this.canvas = canvas;

		for(Cabin cab: controller.getCabins()) {
		    ArrayList<Integer> emptyList = new ArrayList<>();
		    CabinSchedules.put(cab,emptyList);
        }
        buildingThread.start();
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
				//System.out.println("Move elevator " + elevator + " to " + floor);
				controller.moveElevator(elevator, floor);
			}
		}
	};

	private EventHandler<ActionEvent> onButtonEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			String buttonText = ((Button) event.getSource()).getText();
			Boolean lock = buttonText.equalsIgnoreCase("Lock");
			Boolean unlock = buttonText.equalsIgnoreCase("Unlock");
			int elevator = controller.elevatorCombo.getSelectionModel().getSelectedIndex();

			if (!lock && !unlock) {
				int floor = Integer.parseInt(buttonText);
				controller.moveElevator(elevator, floor);
			} else {
				if (lock) {
					controller.getCabins().get(elevator).setIsLocked(true);
				}
			}
		}
	};


	/**
	 * simple getter for mouse handler within canvas.
	 *
	 * @return mouse handler
	 */
	public EventHandler<MouseEvent> getOnMouseEventHandler() {
		return onMouseEventHandler;
	}

	public EventHandler<ActionEvent> getOnButtonEventHandler() {
		return onButtonEventHandler;
	}

	public void setCabinRequest(Cabin cab, int floor) {
		ArrayList<Integer> Schedule = CabinSchedules.get(cab);
		MotionTypes direction = cab.getMotion().getMotionType();
		Schedule.add(floor);
		Comparator<Integer> floorComparotor;

		if (direction == MotionTypes.MOVINGUP) {
			floorComparotor = new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}
			};

		} else {
			floorComparotor = new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o2.compareTo(o1);
				}
			};
		}
		//will need additional case of not moving for later implementation
		Schedule.sort(floorComparotor);
	}

	@Override
	public void run() {
		List<Cabin> cabins = controller.getCabins();
		while (true) {
			for (Cabin cab : cabins) {

				if (!cab.getIsLocked() && cab.getMotion().getMotionType() == MotionTypes.NOTMOVING) {
					ArrayList<Integer> schedule = CabinSchedules.get(cab);

					if (!schedule.isEmpty()) {
						cab.startMotion(schedule.remove(0));
					}
				}

			}
		}
	}

}
