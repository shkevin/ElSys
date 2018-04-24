package ElSys.operations.cabin;

import ElSys.interfaces.Motor;
import ElSys.interfaces.FloorAlignment;
import ElSys.operations.building.BuildSpecs;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
/*
 * Each Cabin will start a Motion thread when it gets a command to move that will track where in the elevator
 * the cabin is and communicate with the MotorControl while running
 */

public class Motion implements Runnable {

    private double currentFloor;
    private int targetFloor = 1;
    private double speed;
    private String cabin;
    private Thread t;
    private MotionTypes motionType = MotionTypes.NOTMOVING;
    private boolean hasRequest = false;
    private Cabin cab;
    private Motor motionMotor = new Motor();
    private FloorAlignment floorAlignment = new FloorAlignment(this);
    private CyclicBarrier barrier;
    private ArrayList<DoorThread> threads;
    double outerDoorVal = 0.0;
    double innerDoorVal = 0.0;
    final double DOORCLOSED = 0.0;
    final double DOOROPEN = 250.0;

    public Motion(double startingFloor, int cabNum, Cabin cab) {
        this.currentFloor = startingFloor;
        this.cab = cab;
        this.threads = new ArrayList<>(BuildSpecs.NUM_DOOR_THREADS);
        initializeDoorThreads();
        this.barrier = new CyclicBarrier(BuildSpecs.NUM_DOOR_THREADS, this::onBarrierBroken);
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

    private void initializeDoorThreads() {
        String name;
        for (int i = 0; i < BuildSpecs.NUM_DOOR_THREADS; i++) {
            if (i % 2 == 0) name = "InnerDoor";
            else name = "OuterDoor";
            DoorThread thread = new DoorThread(this.barrier, motionType, name);
            threads.add(thread);
        }
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
                }else if(floorDiff == 0.0){
                    this.cab.arrived((int)currentFloor);
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
        while (this.motionType != MotionTypes.DOORSOPEN) {
            try {
                Thread.sleep(10);
            }catch (InterruptedException e) {}
        }
        closeDoors();
    }

    synchronized public void openDoors(){
            this.speed = 0;
            this.motionType = MotionTypes.DOORSOPENING;
            iterateDoorThreads(this.motionType);
    }

    synchronized public void closeDoors(){
        this.motionType = MotionTypes.DOORSCLOSING;
        iterateDoorThreads(this.motionType);
    }

    private void iterateDoorThreads(MotionTypes motion) {
        for (DoorThread thread : threads) {
            if (thread != null) {
                thread = new DoorThread(this.barrier, motion, thread.name);
                thread.start();
            }
        }
    }

    private void onBarrierBroken() {
        //Once animation is done, unlock/lock the doors
        if (this.motionType == MotionTypes.DOORSOPENING) {
            try {
                Thread.sleep(BuildSpecs.DOOR_SPEED); //wait open for a while
            } catch (InterruptedException e) {System.out.println("sleep error");}
            this.speed = 0;
            this.motionType = MotionTypes.DOORSOPEN;
            System.out.println("Done with doors opening");
        }
        else if (this.motionType == MotionTypes.DOORSCLOSING) {
            this.motionType = MotionTypes.NOTMOVING;
            System.out.println("Done with doors closing");
        }
    }

    public FloorAlignment getFloorAlignment() {
        return floorAlignment;
    }


    /**
     * Door Thread class.
     */
    private class DoorThread extends Thread {
        private CyclicBarrier barrier;
        private MotionTypes motion;
        private String name;

        DoorThread(CyclicBarrier barrier, MotionTypes motion, String name) {
            this.barrier = barrier;
            this.motion = motion;
            this.name = name;
        }

        @Override
        public void run() {
            try {

                if (this.name.equalsIgnoreCase("InnerDoor")) {
                    if (this.motion == MotionTypes.DOORSOPENING) {
                        System.out.println("Opening inner");
                        while(innerDoorVal < DOOROPEN){
                            innerDoorVal += this.motion.toVal();
                            try { Thread.sleep(70); } catch (InterruptedException ex) {System.out.println("error...");}
                        }
                    }
                    else if (this.motion == MotionTypes.DOORSCLOSING) {
                        System.out.println("Closing inner");
                        while(innerDoorVal > DOORCLOSED){
                            innerDoorVal += this.motion.toVal();
                            try { Thread.sleep(70); } catch (InterruptedException ex) {System.out.println("error...");}
                        }
                    }
                }
                else if (this.name.equalsIgnoreCase("OuterDoor")) {
                    if (this.motion == MotionTypes.DOORSOPENING) {
                        System.out.println("Opening outer");
                        while(outerDoorVal < DOOROPEN){
                            outerDoorVal += this.motion.toVal();
                            try { Thread.sleep(70); } catch (InterruptedException ex) {System.out.println("error...");}
                        }
                    }
                    else if (this.motion == MotionTypes.DOORSCLOSING) {
                        System.out.println("Closing outer");
                        while(outerDoorVal > DOORCLOSED){
                            outerDoorVal += this.motion.toVal();
                            try { Thread.sleep(70); } catch (InterruptedException ex) {System.out.println("error...");}
                        }
                    }
                }

                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("Error occurred");
            }
        }
    }
}