package ElSys.interfaces;


import ElSys.operations.cabin.Motion;
import ElSys.operations.building.buildSpecs;
import ElSys.operations.cabin.MotionTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InterfaceSimulator implements Runnable {

    private motor cabinMotor;
    private FloorAlignment cabinFloorAlighnment;
    private double currentFloor;
    public InterfaceSimulator(Motion motion) {
        cabinMotor = motion.getMotor();
        cabinFloorAlighnment = motion.getFloorAlignment();
        currentFloor = motion.getCurrentFloor();
        Thread t = new Thread(this, "interfaceSimulatorThread");
        t.start();
    }


    @Override
    public void run() {
        while (true) {
            double distance = cabinMotor.getDistance();
            currentFloor += distance;

            currentFloor = roundDouble(currentFloor);
            if (checkThreshold((currentFloor % 1.0),0.0,.09)){
                if(roundDouble(currentFloor) != cabinFloorAlighnment.getFloor()){
                    cabinFloorAlighnment.signal((int)currentFloor);
                }
            }
        }
    }

    private boolean checkThreshold(double value, double min, double max){
        return value <= max && value >= min;
    }
    public double roundDouble(double value){
        return (Math.round(value * 10.0) / 10.0);
    }
}


