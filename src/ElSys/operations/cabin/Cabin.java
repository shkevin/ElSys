package ElSys.operations.cabin;

import ElSys.interfaces.FloorAlignment;

public class Cabin {

//	private FloorAlignment floorAlignment; //we'll need this eventually
	private Motion motion;

	public Cabin() {
		//this.floorAlignment = new FloorAlignment();
		this.motion = new Motion(0);
	}

	public int getFloor() {
		return this.motion.getCurrentFloor();
	}

	//this should be private eventually
	public void startMotion(int targetFloor) {
		//needs to start or re-awaken the Motion thread
		motion.setTargetFloor(targetFloor);
		motion.move();
	}
}
