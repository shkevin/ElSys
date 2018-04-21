package ElSys.operations.cabin;

import ElSys.interfaces.Motor;
import ElSys.interfaces.FloorAlignment;
import ElSys.operations.building.BuildSpecs;
/*
 * Each Cabin will start a Motion thread when it gets a command to move that will track where in the elevator
 * the cabin is and communicate with the MotorControl while running
 */

public class Motion implements Runnable {

    private double currentFloor;
    private int targetFloor;
    private double speed;

    private String cabin;
    public Thread t;
    private MotionTypes motionType = MotionTypes.NOTMOVING;
    private boolean hasRequest = false;
    private Cabin cab;
    private Motor motionMotor = new Motor();
    private FloorAlignment floorAlignment = new FloorAlignment(this);

    public Motion(double startingFloor, int cabNum, Cabin cab) {
        this.currentFloor = startingFloor;
        this.cab = cab;
        switch (cabNum) {
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
        synchronized (this) {
            this.targetFloor = n;
        }
    }

    public int getTargetFloor() {
        return this.targetFloor;
    }

    public int getCurrentFloor() {
        return (int) Math.floor(currentFloor);
    }

    public double getPosition() {
        return this.currentFloor;
    }

    public MotionTypes getMotionType() {
        return this.motionType;
    }

    public void setMotionType(MotionTypes motion) {
        this.motionType = motion;
    }

    synchronized public void setHasRequest(boolean value) {
        hasRequest = value;
    }

    synchronized public boolean getHasRequest() {
        return hasRequest;
    }
    /*
     * This thread moves the elevator and will eventually communication with the Motor and Floor alignment interfaces.
     * It checks to see if it needs to move up or down, then moves the necessary floors.
     */

    @Override
    public void run() {
        while (true) {
            if (getHasRequest()) {
                double floorDiff;
                synchronized (this) {
                    floorDiff = this.targetFloor - this.currentFloor;
                }
                if (floorDiff > 0.0) {
                    this.motionType = MotionTypes.MOVINGUP;
                } else if (floorDiff < 0.0) {
                    this.motionType = MotionTypes.MOVINGDOWN;
                }

                if (this.motionType == MotionTypes.MOVINGUP) {
                    if (Math.abs(floorDiff) <= 0.5 && this.speed >= 0.01) {
                        this.speed *= 0.65;
                    } else if (Math.abs(floorDiff) >= 0.5 && this.speed <= motionType.toVal()){
                        if (this.speed == 0) this.speed += 0.005;
                        else {
                            this.speed *= 1.45;
                        }
                    }
                } else if (this.motionType == MotionTypes.MOVINGDOWN) {
                    if (Math.abs(floorDiff) <= 0.5 && this.speed <= -0.01) {
                        this.speed *= 0.65;
                    } else if (Math.abs(floorDiff) >= 0.5 && this.speed >= motionType.toVal()){
                        if (this.speed == 0) this.speed -= 0.005;
                        else {
                            this.speed *= 1.45;
                        }
                    }
                }

                try {
                    Thread.sleep(70);
                } catch (InterruptedException e) {
                }

                this.currentFloor = Math.round((this.currentFloor + speed) * 100.0) / 100.0;
                motionMotor.move(Math.round(speed * 100.0) / 100.0);
                if (floorAlignment.check()) {
                    if (Math.abs(currentFloor - (double)targetFloor) < 0.05) {
                        this.currentFloor = targetFloor;
                        this.cab.arrived((int)currentFloor);
                        this.speed = 0;
                    }
                }
            }
            //right here we need to ensure the doors open and close before the motion checks for another request
        }
    }

    public Motor getMotor() {
        return motionMotor;
    }

    /*
    This could be implemented in Cabin if helpful with synchronization with the Motor
     */
    public void openCloseDoors() {
        openDoors();
        closeDoors();
    }

    public void openDoors(){
        try {
            this.motionType = MotionTypes.DOORS;
            this.speed = 0; //might eventually be doorsopening
            Thread.sleep(BuildSpecs.doorSpeed);
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted while opening doors");

        }
    }

    private void closeDoors(){
        try {
            this.motionType = MotionTypes.DOORS; //might eventually be doorsclosing
            Thread.sleep(BuildSpecs.doorSpeed);
            this.motionType = MotionTypes.NOTMOVING;
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted while closing doors");
        }
    }


    public FloorAlignment getFloorAlignment() {
        return floorAlignment;
    }
}