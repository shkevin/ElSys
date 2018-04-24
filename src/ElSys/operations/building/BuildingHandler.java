package ElSys.operations.building;

import ElSys.Controller;
import ElSys.interfaces.InterfaceSimulator;
import ElSys.operations.cabin.Cabin;
import ElSys.operations.cabin.ElButton;
import ElSys.operations.cabin.Motion;
import ElSys.operations.cabin.MotionTypes;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class BuildingHandler implements Runnable{

	private Controller controller;
	private BuildingCanvas canvas;
	private HashMap<Cabin,CopyOnWriteArrayList<Integer>> CabinSchedules = new HashMap<>();
	private Thread buildingThread = new Thread(this,"buildingThread");
	private ArrayList<ElButton> upButtonList;
	private ArrayList<ElButton> downButtonList;

	public BuildingHandler(BuildingCanvas canvas, Controller controller) {
		this.controller = controller;
		this.canvas = canvas;
		this.upButtonList = new ArrayList<>();
		this.downButtonList = new ArrayList<>();
		for (int i = 0; i < 10; i++)
		{
			upButtonList.add(new ElButton());
			downButtonList.add(new ElButton());
		}

		for(Cabin cab: controller.getCabins()) {
		    CopyOnWriteArrayList<Integer> emptyList = new CopyOnWriteArrayList<>();
		    CabinSchedules.put(cab,emptyList);
			new InterfaceSimulator(cab.getMotion()); //maybe store a reference to each of the four initialized here
        }

        buildingThread.start();
	}

	public ArrayList<ElButton> getUpButtonList() {
		return this.upButtonList;
	}

	public ArrayList<ElButton> getDownButtonList() {
		return this.downButtonList;
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
			if (event.getX() >= BuildSpecs.FLOOR_WIDTH && event.getX() < canvas.getWidth() - BuildSpecs.FLOOR_WIDTH) {
				elevator = (((int) event.getX() - BuildSpecs.FLOOR_WIDTH) / (BuildSpecs.FLOOR_HEIGHT));
			}

			//Finds the row that we are in within the 2D array
			int floor = -1;
			if (event.getY() >= 0 && event.getY() <= canvas.getHeight() - BuildSpecs.FLOOR_HEIGHT) {
				floor = (BuildSpecs.MAX_FLOORS - ((int) event.getY()) / (BuildSpecs.FLOOR_W_SEP));
			}

			if (floor >= 0 && elevator >= 0) {
				//System.out.println("Move elevator " + elevator + " to " + floor);
                Cabin cab = controller.getCabins().get(elevator);
				if(!cab.getIsLocked()) {
                    newCabinRequest(cab, floor);
                }
			}
		}
	};

	private EventHandler<ActionEvent> onCabinButtonEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			String buttonText = ((Button) event.getSource()).getText();
			Boolean lock = buttonText.equalsIgnoreCase("Lock");
			Boolean unlock = buttonText.equalsIgnoreCase("Unlock");
			int elevator = controller.elevatorCombo.getSelectionModel().getSelectedIndex();
			if (!lock && !unlock) {
				int floor = Integer.parseInt(buttonText);
				newCabinRequest(controller.getCabins().get(elevator), floor);
			} else {
				if (lock) {
					controller.getCabins().get(elevator).setIsLocked(true);
				}
			}
		}
	};


	private EventHandler<ActionEvent> onFloorDownButtonEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			String buttonText = ((Button) event.getSource()).getText();
			int floor = Integer.parseInt(buttonText);
			newFloorRequest(floor, "down");
		}
	};

	private EventHandler<ActionEvent> onFloorUpButtonEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			String buttonText = ((Button) event.getSource()).getText();
			int floor = Integer.parseInt(buttonText);
			newFloorRequest(floor, "up");
		}
	};

	private EventHandler<ActionEvent> onFireAlarmHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			fireAlarm();
		}
	};

	private EventHandler<ActionEvent> maintenanceKeyHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			int elevator = controller.elevatorCombo.getSelectionModel().getSelectedIndex();
			Cabin cab = controller.getCabins().get(elevator);
			cab.setMaintenance(!cab.getMaintenance());
			System.out.println("Maintenance key pressed in cabin: " + elevator + " value: " + cab.getMaintenance());

			if(cab.getMaintenance() == true)
			{
				if (cab.getFireAlarm()) {
					cab.getMotion().closeDoors();
					cab.setFireAlarm(false);
				}
				CopyOnWriteArrayList<Integer> Schedule = CabinSchedules.get(cab);
				Schedule.clear();

				Motion cabMotion = cab.getMotion();
				if(cabMotion.getMotionType() == MotionTypes.MOVINGUP) {

					//System.out.println("cabin spot: "+(int)Math.round(cabMotion.getPosition()) + 1);
					cabMotion.setTargetFloor((int)Math.round(cabMotion.getPosition()) + 1);
				} else if(cabMotion.getMotionType() == MotionTypes.MOVINGDOWN) {
					cabMotion.setTargetFloor((int)Math.round(cabMotion.getPosition()) - 1);
				}

				for(int i =0; i <cab.getButtons().size(); i++)
				{
					cab.getButtons().get(i).setPressed(false);
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

	public EventHandler<ActionEvent> getOnCabinButtonEventHandler() {
		return onCabinButtonEventHandler;
	}

	public EventHandler<ActionEvent> getOnFloorDownButtonEventHandler() {
		return onFloorDownButtonEventHandler;
	}

	public EventHandler<ActionEvent> getOnFloorUpButtonEventHandler() {
		return onFloorUpButtonEventHandler;
	}


	public EventHandler<ActionEvent> getMaintenanceKeyHandler() {
		return maintenanceKeyHandler;
	}
	/*
	newCabinRequest sets the button to pressed, adds the floor to the list of requests, then sorts
	the list according to direction
	 */

	public void newCabinRequest(Cabin cabin, int floor) {
		CopyOnWriteArrayList<Integer> Schedule = CabinSchedules.get(cabin);
		if(cabin.getMaintenance() == true )
		{
			if(Schedule.isEmpty() && !cabin.getMotion().getHasRequest()) {
				cabin.getButtons().get(floor - 1).setPressed(true);
				Motion cabMotion = cabin.getMotion();
				cabMotion.setTargetFloor(floor);
				Schedule.addIfAbsent(floor);
			}
		} else {
			cabin.getButtons().get(floor - 1).setPressed(true);
			MotionTypes direction = cabin.getMotion().getMotionType();
			Schedule.addIfAbsent(floor);
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
			synchronized (floorlock) {
				Schedule.sort(floorComparotor);
			}
		}

	}

	public void newFloorRequest(int floor, String direction){
		System.out.println("New floor request: " + floor + " going " + direction);
		MotionTypes requestedDirection;
		if (direction.equalsIgnoreCase("up")) {
			upButtonList.get(floor-1).setPressed(true);
			requestedDirection = MotionTypes.MOVINGUP;
		} else {
			downButtonList.get(floor-1).setPressed(true);
			requestedDirection = MotionTypes.MOVINGDOWN;
		}

		Cabin cabin = getBestCabin(floor,requestedDirection);
		if(cabin == null){
			System.err.println("Null value");
			System.exit(0);
		}
		newCabinRequest(cabin,floor);




		/*		List<Cabin> cabins = controller.getCabins();

			for (Cabin cab : cabins) {
			CopyOnWriteArrayList<Integer> schedule = CabinSchedules.get(cab);
			if(cab.getMotion().getMotionType() == MotionTypes.NOTMOVING && schedule.isEmpty()) {
				newCabinRequest(cabins.indexOf(cab), floor);
				return;
			}
		}*/
	}

	private Cabin getBestCabin(int floorRequest, MotionTypes direction){
		PriorityQueue<Cabin> avail_cabins = new PriorityQueue<>(new Comparator<Cabin>() {
			@Override
			public int compare(Cabin o1, Cabin o2) {

				int elevator = controller.elevatorCombo.getSelectionModel().getSelectedIndex();
				Cabin cab = controller.getCabins().get(elevator);
				cab.setMaintenance(!cab.getMaintenance());
				System.out.println("CHECKING THE KEY STUFF " + elevator + " value: " + cab.getMaintenance());


				int distance1 = Math.abs(o1.getFloor() - floorRequest);
				int distance2 = Math.abs(o2.getFloor() - floorRequest);
				if(distance1 < distance2) {
					return -1;
				}else if(distance1 > distance2){
					return 1;
				}else{
					return 0;
				}
			}
		});
		for(Cabin cab: controller.getCabins()){
			MotionTypes cabinDirection = cab.getMotion().getMotionType();
			double cabinFloor = cab.getFloor();
			if(direction.equals(cabinDirection)){
				avail_cabins.add(cab);
			}else if(cabinDirection.valid(floorRequest,(int)cabinFloor,direction)) {
				avail_cabins.add(cab);
			}
		}
		if(!avail_cabins.isEmpty()){
			return avail_cabins.peek();
		}else{
			return null;
		}
	}

	public EventHandler<ActionEvent> getOnFireAlarmHandler() {
		return this.onFireAlarmHandler;
	}

	public void fireAlarm() {
		System.out.println("fire alarm");
		List<Cabin> cabins = controller.getCabins();
		for(Cabin cab : cabins) {
			cab.setFireAlarm(true);
			Motion cabMotion = cab.getMotion();
			CopyOnWriteArrayList<Integer> Schedule = CabinSchedules.get(cab);
			Schedule.clear();
			if(cabMotion.getMotionType() == MotionTypes.MOVINGUP) {
				cabMotion.setTargetFloor((int)Math.round(cabMotion.getPosition()) + 1);
				newCabinRequest(cab, 1);
			} else if(cabMotion.getMotionType() == MotionTypes.MOVINGDOWN) {
				cabMotion.setTargetFloor(1);
			} else {
				newCabinRequest(cab, 1);
			}
		}
	}

	@Override
	public void run() {
		List<Cabin> cabins = controller.getCabins();
		while (true) {
			for (Cabin cab : cabins) {
                CopyOnWriteArrayList<Integer> schedule = CabinSchedules.get(cab);
				if (!schedule.isEmpty() && !cab.getIsLocked()){
				    if (cab.getMotion().getMotionType() == MotionTypes.NOTMOVING && !cab.getMotion().getHasRequest()) {
						cab.startMotion(schedule.remove(0));
					} else {
				        //checks for request between current and target floor, if one is found, recreate target floor request
                        //and set current request to the first in between floor
				        int between = 0;
						if (cab.getMotion().getMotionType() == MotionTypes.MOVINGUP) {
							between = getBetweenUp(schedule,cab.getFloor(),cab.getMotion().getTargetFloor());
                        } else if (cab.getMotion().getMotionType() == MotionTypes.MOVINGDOWN) {
							between = getBetweenDown(schedule,cab.getFloor(),cab.getMotion().getTargetFloor());

                        }

						if (between != 0) {
							newCabinRequest(cab, cab.getMotion().getTargetFloor());
							cab.startMotion(schedule.remove(schedule.indexOf(between)));
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

    public Integer getBetweenDown(CopyOnWriteArrayList<Integer> schedule, int current, int target) {
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

	public Integer getBetweenUp(CopyOnWriteArrayList<Integer> schedule, int current, int target) {
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
