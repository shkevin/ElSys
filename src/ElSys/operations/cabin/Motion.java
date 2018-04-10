package ElSys.operations.cabin;

import java.util.ArrayList;
import java.util.Comparator;
/*
* Each Cabin will start a Motion thread when it gets a command to move that will track where in the elevator
* the cabin is and communicate with the MotorControl while running
 */

public class Motion implements Runnable{

    private int currentFloor;
    private int targetFloor;

    private String cabin;
    public Thread t;
    private MotionTypes motionType = MotionTypes.NOTMOVING;
    private boolean hasRequest = false;
    private Cabin cab;


    public Motion(int startingFloor, int cabNum, Cabin cab) {
        this.currentFloor = startingFloor;
        this.cab = cab;
        switch(cabNum) {
            case 0:
                this.cabin = "One";
                break;
            case 1:
                this.cabin = "Two";
                break;
            case 2:
                this.cabin = "Three";
                break;
            case 3:
                this.cabin = "Four";
                break;
        }
        System.out.println("(Motion) Creating thread for elevator: " + this.cabin);
        t = new Thread(this, ("motion " + cabin));
        t.start();
    }

    //must be called before move or starting the thread so the elevator knows where to go
    public void setTargetFloor(int n) {
        this.targetFloor = n;
    }

    public int getTargetFloor() {
        return this.targetFloor;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public MotionTypes getMotionType() {
        return this.motionType;
    }

    public void setMotionType(MotionTypes motion) {
        this.motionType = motion;
    }

    synchronized public void setHasRequest (boolean value) {
        hasRequest = value;
    }

    synchronized public boolean getHasRequest(){return hasRequest;}
    /*
    * This thread moves the elevator and will eventually communication with the motor and Floor alignment interfaces.
     * It checks to see if it needs to move up or down, then moves the necessary floors.
     */

    @Override
    public void run() {
        while (true) {
            if (getHasRequest()) {
                int floorDiff = this.targetFloor - this.currentFloor;
                if (floorDiff > 0) {
                    this.motionType = MotionTypes.MOVINGUP;
                } else if (floorDiff < 0) {
                    this.motionType = MotionTypes.MOVINGDOWN;
                } else {
                    this.motionType = MotionTypes.NOTMOVING;
                }
                if (floorDiff != 0) {
                    try {
                        System.out.println("(Motion) Elevator " + this.cabin + " moving from " + this.currentFloor
                                + " to " + (int) (this.currentFloor + motionType.toVal()));
                        Thread.sleep(1000);
                        this.currentFloor += motionType.toVal();
                    } catch (InterruptedException e) {
                        System.out.println("(Motion) Elevator " + this.cabin + " interrupted.");
                    }
                } else {
                    this.cab.getButtons().get(currentFloor - 1).setPressed(false); //turn off button on arrival
                    System.out.println("(Motion) Elevator " + this.cabin + " done moving.");
                    this.motionType = MotionTypes.NOTMOVING;
                    setHasRequest(false);
                    try {
                        this.motionType = MotionTypes.DOORS;
                        Thread.sleep(5000); //this is simulating the doors opening and closing
                        this.motionType = MotionTypes.NOTMOVING;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //right here we need to ensure the doors open and close before the motion checks for another request
            }
        }
    }
}