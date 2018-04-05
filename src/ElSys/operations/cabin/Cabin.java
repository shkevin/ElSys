package ElSys.operations.cabin;

import java.util.ArrayList;

public class Cabin {

	//	private FloorAlignment floorAlignment; //we'll need this eventually
	private Motion motion;
	private int cabNum;
	private int currentFloor;
	private Boolean isLocked;
	private ArrayList<ElButton> buttons;

	public Cabin(int cabNum) {
		//this.floorAlignment = new FloorAlignment();
		this.cabNum = cabNum;
		this.currentFloor = 1;
		this.isLocked = false;
		this.buttons = new ArrayList<ElButton>();
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
			if (this.motion == null || this.motion.getMotionType() == MotionTypes.NOTMOVING) {
				System.out.println("(Cabin) creating motion: " + (cabNum + 1));
				this.motion = new Motion(this.currentFloor, cabNum, this);
				motion.setTargetFloor(targetFloor);
				motion.start();
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
