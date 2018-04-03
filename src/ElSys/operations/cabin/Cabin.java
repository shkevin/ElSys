package ElSys.operations.cabin;

import ElSys.interfaces.FloorAlignment;

public class Cabin {

//	private FloorAlignment floorAlignment; //we'll need this eventually
	private Motion motion;
	private int cabNum;
	private int currentFloor;

	public Cabin(int cabNum) {
		//this.floorAlignment = new FloorAlignment();
		this.cabNum = cabNum;
		this.currentFloor = 1;
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
		if(this.motion == null || this.motion.getMotionType() == MotionTypes.NOTMOVING) {
			System.out.println("(Cabin) creating motion: " + (cabNum + 1));
			this.motion = new Motion(this.currentFloor, cabNum);
			motion.setTargetFloor(targetFloor);
			motion.start();
			this.currentFloor = targetFloor;
		}else {
			System.out.println("(Cabin) Elevator " + (cabNum + 1) + " moving, click disregarded");
		}
	}
}
