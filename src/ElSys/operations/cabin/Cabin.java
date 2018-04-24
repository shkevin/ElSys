package ElSys.operations.cabin;

import java.util.ArrayList;
import ElSys.operations.building.BuildingHandler;
import static ElSys.operations.cabin.ServiceDirection.*;

public class Cabin {

	//	private FloorAlignment floorAlignment; //we'll need this eventually
	private int cabNum;
	private Integer currentFloor;
	private Boolean isLocked = false;
	private ArrayList<ElButton> buttons;
	private Motion motion;
	private BuildingHandler handler;
	private boolean firealarm = false;
	private boolean maintenance = false;

	private ServiceDirection cabinDirection = NONE;
	private double doorValue;

	public Cabin(int cabNum) {
		//this.floorAlignment = new FloorAlignment();
		this.cabNum = cabNum;
		this.currentFloor = 1;
		this.buttons = new ArrayList<>();
		this.motion = new Motion(1,cabNum,this);
		this.doorValue = motion.outerDoorVal;
		for (int i = 0; i < 10; i++)
		{
			buttons.add(new ElButton());
		}
	}

	public void setHandler(BuildingHandler handler) {
		this.handler = handler;
	}

	public boolean getMaintenance() {
	    return this.maintenance;
    }

    public void setMaintenance(boolean val) {
	    this.maintenance = val;
    }

	public int getCabNum() {
	    return this.cabNum;
    }

	public ArrayList<ElButton> getButtons() {
		return buttons;
	}

	public void arrived(int floor){
		handler.getDownButtonList().get(floor-1).setPressed(false);
		handler.getUpButtonList().get(floor-1).setPressed(false);
		this.buttons.get(floor-1).setPressed(false);
		this.motion.setMotionType(MotionTypes.NOTMOVING);
		if (!firealarm) {
            this.motion.openCloseDoors();
        } if (firealarm && floor == 1) {
		    this.motion.openDoors();
        }
        this.motion.setHasRequest(false);
		System.out.println("(Cabin) Elevator " + (this.cabNum + 1) + " done moving. " + getFloor());
	}

	//while the elevator is moving, the Motion Thread keeps track of the current floor
	//but when it's not, the cabin will keep this info.

	public Integer getFloor() {
		synchronized (getMotion())
		{if (this.motion != null) {
			return this.motion.getCurrentFloor();
		} else {
			return this.currentFloor;
		}}
	}

	public double getPosition() {
		synchronized (getMotion())
		{if (this.motion != null) {
			return this.motion.getPosition();
		} else {
			return this.currentFloor;
		}}
	}

	public Motion getMotion() {
		return this.motion;
	}

	public void setFireAlarm(boolean val) {
	    this.firealarm = val;
    }

    public boolean getFireAlarm(){
		return this.firealarm;
	}

	/*
	* startMotion is whats called when a request for a floor is made. If the elevator isn't moving already, it
	* creates a new motion thread, sets the target floor, and then starts the thread.
	 */
	public void startMotion(int targetFloor) {
        motion.setTargetFloor(targetFloor);
        if(this.getFloor() < targetFloor){
            motion.setMotionType(MotionTypes.MOVINGUP);
        }else{
            motion.setMotionType(MotionTypes.MOVINGDOWN);
        }
        motion.setHasRequest(true);
	}

	public Boolean getIsLocked() {
		return this.isLocked;
	}

	public void setIsLocked(Boolean val) {
		this.isLocked = val;
	}

	public double getDoorValue() {
		return doorValue;
	}

	public void setDoorValue(double doorValue) {
		this.doorValue = doorValue;
	}

	public void setCabinDirection(ServiceDirection direction){this.cabinDirection = direction;}

	public ServiceDirection getCabinDirection(){return this.cabinDirection;}
}
