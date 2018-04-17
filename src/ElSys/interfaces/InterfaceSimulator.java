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
    private MotionTypes direction;
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

            if (roundDouble(currentFloor % 1.0) == 0){
                if((int)currentFloor != (int)cabinFloorAlighnment.getFloor()){
                    cabinFloorAlighnment.signal((int)currentFloor);
                }
            }
        }
    }

    public int roundDouble(double value){
        return (int)(Math.round(value * 10.0) / 10.0);
    }
}


