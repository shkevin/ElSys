package ElSys.interfaces;


import ElSys.operations.cabin.Motion;
import ElSys.operations.building.buildSpecs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InterfaceSimulator implements Runnable{

    private ArrayList<motor> cabinMotors = new ArrayList<>(4);
    private HashMap<motor,FloorAlignment> cabinFloorAlignments = new HashMap<>(4);
    private volatile static int THREAD_COUNT = 0;

    public InterfaceSimulator(){

    }

    public void setup(ArrayList<Motion> motions){
        for(Motion currentMotion: motions){
            motor currentMotor = currentMotion.getMotor();
            cabinMotors.add(currentMotor);
            cabinFloorAlignments.put(currentMotor,currentMotion.getFloorAlignment());

            Thread t = new Thread(this,"interfaceSimulatorThread");
           synchronized (this){
               THREAD_COUNT++;
           }

            t.start();
        }
    }

    @Override
    public void run() {
        while (true) {
            double totalDistance = 0;
            for (motor cabinMotor : cabinMotors) {
                FloorAlignment cabinFloorAlighnment = cabinFloorAlignments.get(cabinMotor);
                totalDistance = 0;

                while (Math.abs(totalDistance) <= buildSpecs.FLOOR_HEIGHT) {
                    totalDistance += cabinMotor.getDistance();
                }

                cabinFloorAlighnment.signal();


            }
        }
    }
}

