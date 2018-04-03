package ElSys.operations.cabin;

/*
* Each Cabin will have a Motion thread that will track where in the elevator
* the cabin is and communicate with the MotorControl while running
 */

public class Motion extends Thread{

    private int currentFloor;
    private int targetFloor;

    public Motion(int startingFloor) {
        this.currentFloor = startingFloor;
    }

    //must be called before move or starting the thread so the elevator knows
    //where to go
    public void setTargetFloor(int n) {
        this.targetFloor = n;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    //this is just for testing while the elevators are "materializing"
    //this logic should be in run thread once the elevator gets told to move
    public void move() {
        System.out.println("moving from " + this.currentFloor + " to " + this.targetFloor);
        this.currentFloor = this.targetFloor;
    }

    @Override
    public void run() {

    }
}
