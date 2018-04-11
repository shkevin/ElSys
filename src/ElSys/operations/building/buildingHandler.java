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
                newCabinRequest(elevator,floor);
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
				newCabinRequest(elevator, floor);
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

	/*
	newCabinRequest sets the button to pressed, adds the floor to the list of requests, then sorts
	the list according to direction
	 */

	public void newCabinRequest(int cab, int floor) {
	    Cabin cabin = controller.getCabins().get(cab);
		cabin.getButtons().get(floor - 1).setPressed(true);
		ArrayList<Integer> Schedule = CabinSchedules.get(cabin);
		MotionTypes direction = cabin.getMotion().getMotionType();
		Schedule.add(floor);
		Comparator<Integer> floorComparotor;
		Object floorlock = cabin.getMotion();

		Integer currentfloor;
		synchronized (floorlock) {
			currentfloor = cabin.getFloor();
		}
		if (direction == MotionTypes.MOVINGUP) {
			floorComparotor = new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {

					synchronized (floorlock) {
						if (o1 <= currentfloor) {
							return 1;
						} else {

							return o1.compareTo(o2);
						}
					}
				}
			};

		} else {
			floorComparotor = new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {

					synchronized (floorlock) {
						if (o1 >= currentfloor) {
							return 1;
						} else {
							return o2.compareTo(o1);
						}
					}
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
                ArrayList<Integer> schedule = CabinSchedules.get(cab);
				if (!schedule.isEmpty() && !cab.getIsLocked()){
				    if (cab.getMotion().getMotionType() == MotionTypes.NOTMOVING && !cab.getMotion().getHasRequest()) {
						cab.startMotion(schedule.remove(0));
					} else {
				        //checks for request between current and target floor, if one is found, recreate target floor request
                        //and set current request to the first in between floor
				        if (cab.getMotion().getMotionType() == MotionTypes.MOVINGUP) {
				            int between = getBetweenUp(schedule,cab.getMotion().getCurrentFloor(),cab.getMotion().getTargetFloor());
                            if (between != 0) {
                                newCabinRequest(0, cab.getMotion().getTargetFloor());
                                cab.startMotion(schedule.remove(schedule.indexOf(between)));
                            }
                        } else if (cab.getMotion().getMotionType() == MotionTypes.MOVINGDOWN) {
				            int between = getBetweenDown(schedule,cab.getMotion().getCurrentFloor(),cab.getMotion().getTargetFloor());
                            if (between != 0) {
                                newCabinRequest(0, cab.getMotion().getTargetFloor());
                                cab.startMotion(schedule.remove(schedule.indexOf(between)));
                            }
                        }
                    }
                }
			}
		}
	}

    /*
     * returns the first request that's between the current floor and the target floor,
     * null if there isn't one. Used when the elevator is moving down
     */

    public Integer getBetweenDown(ArrayList<Integer> schedule, int current, int target) {
        for (Integer i : schedule) {
            if (i < current && i > target){
                return i;
            }
        }
        return 0;
    }

    /*
    * returns the first request that's between the current floor and the target floor,
    * null if there isn't one. Used when the elevator is moving up
     */

	public Integer getBetweenUp(ArrayList<Integer> schedule, int current, int target) {
	    for (Integer i : schedule) {
	        if (i > current && i < target){
	            return i;
            }
        }
        return 0;
    }

    /*
    * Utility function to print a schedule
     */

	public void printSchedule(ArrayList<Integer> schedule){
	    if (!schedule.isEmpty()) {
            System.out.print("Schedule: ");
            for (Integer i : schedule) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

}
