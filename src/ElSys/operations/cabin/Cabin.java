package ElSys.operations.cabin;

import java.util.ArrayList;

public class Cabin {

	//	private FloorAlignment floorAlignment; //we'll need this eventually
	private int cabNum;
	private int currentFloor;
	private Boolean isLocked = false;
	private ArrayList<ElButton> buttons;
	private Motion motion;

	public Cabin(int cabNum) {
		//this.floorAlignment = new FloorAlignment();
		this.cabNum = cabNum;
		this.currentFloor = 1;
		this.buttons = new ArrayList<ElButton>();
		this.motion = new Motion(1,cabNum,this);
		for (int i = 0; i < 10; i++)
		{
			buttons.add(new ElButton());
		}
	}

	public ArrayList<ElButton> getButtons() {
		return buttons;
	}

	//while the elevator is moving, the Motion Thread keeps track of the current floor
	//but when it's not, the cabin will keep this info.

	public int getFloor() {
		if (this.motion != null) {
			return this.motion.getCurrentFloor();
		} else {
			return this.currentFloor;
		}
	}

	public Motion getMotion() {
		return this.motion;
	}

	/*
	* startMotion is whats called when a request for a floor is made. If the elevator isn't moving already, it
	* creates a new motion thread, sets the target floor, and then starts the thread.
	 */
	public void startMotion(int targetFloor) {
		if (!isLocked) {
			if (this.motion.getMotionType() == MotionTypes.NOTMOVING) {

				motion.setTargetFloor(targetFloor);

				motion.setHasRequest(true);

				this.currentFloor = targetFloor;

			} else {
				System.out.println("(Cabin) Elevator " + (cabNum + 1) + " moving, click disregarded");
			}
		}
	}

	public Boolean getIsLocked() {
		return this.isLocked;
	}

	public void setIsLocked(Boolean val) {
		this.isLocked = val;
	}


}
