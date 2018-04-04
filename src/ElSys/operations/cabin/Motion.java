package ElSys.operations.cabin;

import java.util.TreeSet;

/*
* Each Cabin will start a Motion thread when it gets a command to move that will track where in the elevator
* the cabin is and communicate with the MotorControl while running
 */

public class Motion implements Runnable{

    private int currentFloor = 1;
    private int targetFloor = 0;
    private double position = 1.0;
    private String cabin;
    public Thread t;
    private MotionTypes motionType = MotionTypes.NOTMOVING;
    private TreeSet<Integer> requests = new TreeSet<>();
    private TreeSet<Integer> pendingRequests = new TreeSet<>();

    public Motion(int cabNum) {
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

    public void newRequest(int floor) {
        if (false) { //filter out requests above or below currently "path"
            pendingRequests.add(floor);
        } else {
            requests.add(floor);
            if (targetFloor == 0) {
                targetFloor = floor; //if it doesn't have a request to work on right now...
            }

            if (this.motionType == MotionTypes.WAITING) {
                synchronized(cabin) {
                    System.out.println("notify");
                    cabin.notify();
                }
            }
        }
    }

    /*
    * This thread moves the elevator and willeventually communication with the motor and Floor alignment interfaces.
    * It checks to see if it needs to move up or down, then moves the necessary floors.
     */

    @Override
    public void run() {
        while(true) {
            if (this.motionType == MotionTypes.NOTMOVING && requests.isEmpty()) {       //on start up
                synchronized (cabin) {
                    try {
                        System.out.println(cabin + " waiting...");
                        this.motionType = MotionTypes.WAITING;
                        cabin.wait();
                    } catch (InterruptedException ex) {
                        System.out.println("Thread interrupted");
                    }
                }
                System.out.println(cabin + " notified of input");
                this.motionType = MotionTypes.NOTMOVING;
            } else {
                //process next request based on up or down. if it's moving up
                //it can pollFirst from requests, if it's moving down it will pollLast
            }
        }
    }

}