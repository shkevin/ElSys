package ElSys.operations.cabin;
import ElSys.interfaces.motor;
import ElSys.interfaces.FloorAlignment;
/*
* Each Cabin will start a Motion thread when it gets a command to move that will track where in the elevator
* the cabin is and communicate with the MotorControl while running
 */

public class Motion implements Runnable{

    private double currentFloor;
    private int targetFloor;

    private String cabin;
    public Thread t;
    private MotionTypes motionType = MotionTypes.NOTMOVING;
    private boolean hasRequest = false;
    private Cabin cab;
    private motor motionMotor = new motor();
    private FloorAlignment floorAlignment = new FloorAlignment(this);

    public Motion(double startingFloor, int cabNum, Cabin cab) {
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
                double floorDiff;
                synchronized (this) {
                    floorDiff = this.targetFloor - this.currentFloor;
                }
                if (floorDiff > 0) {
                    this.motionType = MotionTypes.MOVINGUP;
                } else if (floorDiff < 0) {
                    this.motionType = MotionTypes.MOVINGDOWN;
                } else {
                    this.motionType = MotionTypes.NOTMOVING;
                }
                if (Math.abs(floorDiff) >= MotionTypes.MOVINGUP.toVal()/2) {
                    try {
                        Thread.sleep(70);
                        synchronized (this) {
                            this.currentFloor = Math.round((this.currentFloor +  motionType.toVal()) * 10.0) / 10.0;
                            motionMotor.move(Math.round(motionType.toVal() * 10.0)/10.0);
                        }
                    } catch (InterruptedException e) {
                        System.out.println("(Motion) Elevator " + this.cabin + " interrupted.");
                    }
                } else {
                    this.cab.getButtons().get((int)Math.floor(currentFloor) - 1).setPressed(false); //turn off button on arrival
                    System.out.println("(Motion) Elevator " + this.cabin + " done moving. ");
                    this.motionType = MotionTypes.NOTMOVING;
                    setHasRequest(false);
                    try {
                        this.motionType = MotionTypes.DOORS;
                        Thread.sleep(2500); //this is simulating the doors opening and closing
                        this.motionType = MotionTypes.NOTMOVING;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }



/*

                if(getHasRequest()){//Not moving to moving
                    synchronized (this){
                    try{
                        do{
                            this.currentFloor = Math.round(this.currentFloor + motor.move(motionType.toVal()) * 10.0);
                            floorAlighnment.check();//check floor alignment
                        }while(true)
                    }catch(FloorAlignment fae){
                        }

                }
*/


                //right here we need to ensure the doors open and close before the motion checks for another request
            }
        }
    }

    public motor getMotor() {
        return motionMotor;
    }

    public FloorAlignment getFloorAlignment() {
        return floorAlignment;
    }
}