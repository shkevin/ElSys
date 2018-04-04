package ElSys.operations.cabin;

import ElSys.interfaces.FloorAlignment;

public class Cabin {

//	private FloorAlignment floorAlignment; //we'll need this eventually
	private Motion motion;
	private int cabNum;


	public Cabin(int cabNum) {
		//this.floorAlignment = new FloorAlignment();
		this.cabNum = cabNum;
		this.motion = new Motion(cabNum);
		motion.start();
	}

	//while the elevator is moving, the Motion Thread keeps track of the current floor
	//but when it's not, the cabin will keep this info.

	public int getFloor() {
		return this.motion.getCurrentFloor();
	}

	public Motion getMotion() {
		return this.motion;
	}

	public void tryRequest(int floor) {
		//Check to see if button is locked or unlocked. for now, always valid
		boolean buttonValid = true;
		if(buttonValid) {
			motion.newRequest(floor);
		}
	}

}
