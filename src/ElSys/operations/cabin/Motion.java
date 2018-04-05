package ElSys.operations.cabin;

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
    private Cabin cab;

    public Motion(int startingFloor, int cabNum, Cabin cab) {
        this.currentFloor = startingFloor;
        this.cab = cab;
        switch(cabNum) {
            case 0:
                this.cabin = "One";
                return;
            case 1:
                this.cabin = "Two";
                return;
            case 2:
                this.cabin = "Three";
                return;
            case 3:
                this.cabin = "Four";
                return;
        }
        System.out.println("(Motion) Creating thread for elevator: " + this.cabin);
    }

    //must be called before move or starting the thread so the elevator knows where to go
    public void setTargetFloor(int n) {
        this.targetFloor = n;
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

    public void start () {
        if (t == null) {
            t = new Thread (this, this.cabin);
            t.start();
        }
    }

    /*
    * This thread moves the elevator and willeventually communication with the motor and Floor alignment interfaces.
    * It checks to see if it needs to move up or down, then moves the necessary floors.
     */

    @Override
    public void run() {
        if (this.currentFloor < this.targetFloor) {
            this.motionType = MotionTypes.MOVINGUP;
        } else if (this.currentFloor > this.targetFloor) {
            this.motionType = MotionTypes.MOVINGDOWN;
        } else {
            this.motionType = MotionTypes.NOTMOVING;
        }
        try {
            synchronized(this) {
                int floorDiff = Math.abs(this.targetFloor - this.currentFloor);
                for (int i = 0; i < floorDiff; i++) {
                    System.out.println("(Motion) Elevator " + this.cabin + " moving from " + this.currentFloor + " to " + (int)(this.currentFloor + motionType.toVal()));
                    Thread.sleep(1000);
                    this.currentFloor += motionType.toVal();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("(Motion) Elevator " +  this.cabin + " interrupted.");
        }
        System.out.println("(Motion) Elevator " +  this.cabin + " done moving.");
        this.motionType = MotionTypes.NOTMOVING;
        this.cab.getButtons().get(targetFloor-1).setPressed(false); //turn off button on arrival
    }

}