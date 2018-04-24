package ElSys.operations.building;

import ElSys.Controller;
import ElSys.interfaces.InterfaceSimulator;
import ElSys.operations.cabin.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class BuildingHandler implements Runnable {

    private Controller controller;
    private BuildingCanvas canvas;
    private HashMap<Cabin, CopyOnWriteArrayList<request>> CabinSchedules = new HashMap<>();
    private Thread buildingThread = new Thread(this, "buildingThread");
    private ArrayList<ElButton> upButtonList;
    private ArrayList<ElButton> downButtonList;

    public BuildingHandler(BuildingCanvas canvas, Controller controller) {
        this.controller = controller;
        this.canvas = canvas;
        this.upButtonList = new ArrayList<>();
        this.downButtonList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            upButtonList.add(new ElButton());
            downButtonList.add(new ElButton());
        }

        for (Cabin cab : controller.getCabins()) {
            CopyOnWriteArrayList<request> emptyList = new CopyOnWriteArrayList<>();
            CabinSchedules.put(cab, emptyList);
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
                if (!cab.getIsLocked()) {
                    newCabinRequest(cab, new request(floor));
                }
            }
        }
    };

	private EventHandler<MouseEvent> onMouseEventHandlerDoors = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			int elevator = controller.elevatorCombo.getSelectionModel().getSelectedIndex();
			Cabin cab = controller.getCabins().get(elevator);
			if (cab.getMotion().getMotionType() == MotionTypes.DOORSCLOSING) {
				controller.getCabins().get(elevator).getMotion().setDoorInterfere(true);
				System.out.println("Door click");
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
                newCabinRequest(controller.getCabins().get(elevator), new request(floor));
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
            newFloorRequest(floor, MotionTypes.MOVINGDOWN);
        }
    };

    private EventHandler<ActionEvent> onFloorUpButtonEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            String buttonText = ((Button) event.getSource()).getText();
            int floor = Integer.parseInt(buttonText);
            newFloorRequest(floor, MotionTypes.MOVINGUP);
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
                    while (cab.getMotion().getMotionType() != MotionTypes.DOORSOPEN) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {}
                    }
                    cab.setFireMaint(true);
				}
				CopyOnWriteArrayList<request> Schedule = CabinSchedules.get(cab);
				Schedule.clear();

				Motion cabMotion = cab.getMotion();


				if(cabMotion.getMotionType() == MotionTypes.MOVINGUP) {
				    if(((int)Math.round(cabMotion.getPosition()) + 1) <= 10){
                        cabMotion.setTargetFloor((int)Math.round(cabMotion.getPosition()) + 1);
                    }
				} else if(cabMotion.getMotionType() == MotionTypes.MOVINGDOWN) {
				    if(((int)Math.round(cabMotion.getPosition()) - 1) >= 1){
                        cabMotion.setTargetFloor((int)Math.round(cabMotion.getPosition()) - 1);
                    }
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

	public EventHandler<MouseEvent> getOnMouseEventHandlerDoors() {
		return onMouseEventHandlerDoors;
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

    public void newCabinRequest(Cabin cabin, request floor) {
        CopyOnWriteArrayList<request> Schedule = CabinSchedules.get(cabin);
        if (cabin.getMaintenance()) {
            if(Schedule.isEmpty() && !cabin.getMotion().getHasRequest()) {
                cabin.getButtons().get(floor.getFloor() - 1).setPressed(true);
                Motion cabMotion = cabin.getMotion();
                cabMotion.setTargetFloor(floor.getFloor());
                Schedule.addIfAbsent(floor);
            }
        } else {
            cabin.getButtons().get(floor.getFloor() - 1).setPressed(true);
            MotionTypes direction = cabin.getMotion().getMotionType();
            Schedule.addIfAbsent(floor);
            Comparator<request> floorComparotor;
            Object floorlock = cabin.getMotion();

            Integer currentfloor;
            synchronized (floorlock) {
                currentfloor = cabin.getFloor();
            }
            floorComparotor = cabin.getCabinDirection().getComparator(floorlock,currentfloor);


            //will need additional case of not moving for later implementation
            synchronized (floorlock) {
                Schedule.sort(floorComparotor);
            }
        }
    }

    public void newFloorRequest(int floor, MotionTypes direction) {
        ServiceDirection requestedDirection;

        if (direction == MotionTypes.MOVINGUP) {
            requestedDirection = ServiceDirection.UP;
        } else {
            requestedDirection = ServiceDirection.DOWN;
        }


        Cabin cabin = getBestCabin(floor, requestedDirection);

        if (cabin == null) {
            System.err.println("Null value");
            System.exit(0);
        }

        boolean foundRequest = true;
        for (request req : CabinSchedules.get(cabin)) {
            if (req.getFloor() == floor && req.getRequestDirection().equals(requestedDirection)) foundRequest = false;
        }
        if (foundRequest) {
            newCabinRequest(cabin, new request(floor, requestedDirection));
        }
    }
    private Cabin getBestCabin(int floorRequest, ServiceDirection direction) {

        //this comparator get's used for the case no cabin can currently service the request.
        Comparator<Cabin> compareLastStopDistance = new Comparator<Cabin>() {
            @Override
            public int compare(Cabin cabin1, Cabin cabin2) {
                CopyOnWriteArrayList<request> cabin1Schedule = CabinSchedules.get(cabin1);
                CopyOnWriteArrayList<request> cabin2Schedule = CabinSchedules.get(cabin2);

				//List<Cabin> cabins = controller.getCabins();

                //These are the distances from the floorRequest to each cabins final request.
                Integer cabin1Distance;
                Integer cabin2Distance;
                if (!cabin1Schedule.isEmpty()) {
                    cabin1Distance = Math.abs(cabin1Schedule.get(cabin1Schedule.size() - 1).getFloor() - floorRequest);
                } else cabin1Distance = 0;
                if (!cabin2Schedule.isEmpty()) {
                    cabin2Distance = Math.abs(cabin2Schedule.get(cabin2Schedule.size() - 1).getFloor() - floorRequest);
                } else cabin2Distance = 0;
                if (cabin1Distance < cabin2Distance) {
                    return -1;
                } else if (cabin1Distance > cabin2Distance) {
                    return 1;
                } else {
                    return 0;
                }

            }
        };

        PriorityQueue<Cabin> avail_cabins = new PriorityQueue<>(new Comparator<Cabin>() {
            @Override
            public int compare(Cabin o1, Cabin o2) {
                int distance1 = Math.abs(o1.getFloor() - floorRequest);
                int distance2 = Math.abs(o2.getFloor() - floorRequest);
                if (distance1 < distance2) {
                    return -1;
                } else if (distance1 > distance2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        //Add all cabins that can currently service this request to the priorityQue
        for (Cabin cab : controller.getCabins()) {
            Integer cabinFloor = cab.getFloor();
            ServiceDirection cabinDirection = cab.getCabinDirection();
            boolean matchDirection = cabinDirection.equals(direction);

            //check if the cabin is at the floor of the request
            if(cabinDirection.equals(ServiceDirection.NONE)){ //check if cabin can service request
                avail_cabins.add(cab);
            }else if (matchDirection) {
                if (cabinDirection.valid(floorRequest, cabinFloor)) {
                    avail_cabins.add(cab);
                }
            }
        }


        if (!avail_cabins.isEmpty()) {
            return avail_cabins.peek();
        } else {
            //If no cabin was able to service the request then use other comparator method to find best cabin and
            //compare all cabins
            ArrayList<Cabin> cabins = new ArrayList<>(controller.getCabins());
            cabins.sort(compareLastStopDistance);
            return cabins.get(0);
        }
    }


    public EventHandler<ActionEvent> getOnFireAlarmHandler() {
        return this.onFireAlarmHandler;
    }

    public void fireAlarm() {
        System.out.println("fire alarm");
        List<Cabin> cabins = controller.getCabins();
        for (Cabin cab : cabins) {
            cab.setFireAlarm(true);
            Motion cabMotion = cab.getMotion();
            CopyOnWriteArrayList<request> Schedule = CabinSchedules.get(cab);
            Schedule.clear();
            for(int i =0; i <cab.getButtons().size(); i++)
            {
                cab.getButtons().get(i).setPressed(false);
            }
            if (cabMotion.getMotionType() == MotionTypes.MOVINGUP) {
                cabMotion.setTargetFloor((int) Math.round(cabMotion.getPosition()) + 1);
                newCabinRequest(cab, new request(1,ServiceDirection.DOWN));
            } else if (cabMotion.getMotionType() == MotionTypes.MOVINGDOWN) {
                cabMotion.setTargetFloor(1);
            } else {
                newCabinRequest(cab, new request(1,ServiceDirection.DOWN));
            }
        }
    }

    private void setCabServiceDirection(Cabin cab, request floorRequest){
        if(floorRequest.getRequestDirection().equals(ServiceDirection.NONE)){
            Integer nextFloor = floorRequest.getFloor();
            if(cab.getFloor() < nextFloor){
                cab.setCabinDirection(ServiceDirection.UP);
            }else {
                cab.setCabinDirection(ServiceDirection.DOWN);
            }
        }else {
            cab.setCabinDirection(floorRequest.getRequestDirection());
        }
    }
    @Override
    public void run() {
        List<Cabin> cabins = controller.getCabins();
        while (true) {
            for (Cabin cab : cabins) {
                CopyOnWriteArrayList<request> schedule = CabinSchedules.get(cab);
                if (!schedule.isEmpty() && !cab.getIsLocked()) {
                    if (cab.getMotion().getMotionType() == MotionTypes.NOTMOVING && !cab.getMotion().getHasRequest()) {
                        request nextRequest = schedule.remove(0);

                        Integer nextFloor = nextRequest.getFloor();
                        setCabServiceDirection(cab,nextRequest);
                        cab.startMotion(nextFloor);
                    } else {
                        //checks for request between current and target floor, if one is found, recreate target floor request
                        //and set current request to the first in between floor
                        request between = new request(0);
                        if (cab.getMotion().getMotionType() == MotionTypes.MOVINGUP) {
                            between = getBetweenUp(schedule, cab.getFloor(), cab.getMotion().getTargetFloor());
                        } else if (cab.getMotion().getMotionType() == MotionTypes.MOVINGDOWN) {
                            between = getBetweenDown(schedule, cab.getFloor(), cab.getMotion().getTargetFloor());

                        }

                        if (between.getFloor() != 0) {
                            newCabinRequest(cab, new request(cab.getMotion().getTargetFloor()));
                            cab.startMotion(schedule.remove(schedule.indexOf(between)).getFloor());
                        }
                    }
                }else if(schedule.isEmpty() && cab.getMotion().getMotionType() != MotionTypes.MOVINGDOWN && cab.getMotion().getMotionType() != MotionTypes.MOVINGUP){
                    cab.setCabinDirection(ServiceDirection.NONE);
                }
            }
        }
    }

    /*
     * returns the first request that's between the current floor and the target floor,
     * null if there isn't one. Used when the elevator is moving down
     */

    public request getBetweenDown(CopyOnWriteArrayList<request> schedule, int current, int target) {
        for (request req : schedule) {
            Integer i = req.getFloor();
            if (i < current && i > target) {
                return req;
            }
        }
        return new request(0);
    }

    /*
     * returns the first request that's between the current floor and the target floor,
     * null if there isn't one. Used when the elevator is moving up
     */

    public request getBetweenUp(CopyOnWriteArrayList<request> schedule, int current, int target) {
        for (request req : schedule) {
            Integer i = req.getFloor();
            if (i > current && i < target) {
                return req;
            }
        }
        return new request(0);
    }

    /*
     * Utility function to print a schedule
     */

    public void printSchedule(ArrayList<Integer> schedule) {
        if (!schedule.isEmpty()) {
            System.out.print("Schedule: ");
            for (Integer i : schedule) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

}
