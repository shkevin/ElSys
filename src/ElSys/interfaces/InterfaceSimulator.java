package ElSys.interfaces;


import ElSys.operations.cabin.Motion;

public class InterfaceSimulator implements Runnable {

    private Motor cabinMotor;
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
            if (checkThreshold((currentFloor % 1.0),0.0,.009)){
                if(currentFloor != cabinFloorAlighnment.getFloor()){
                    cabinFloorAlighnment.signal((int)currentFloor);
                }
            }
        }
    }

    private boolean checkThreshold(double value, double min, double max){
        return value <= max && value >= min;
    }
    public double roundDouble(double value){
        return (Math.round(value * 100.0) / 100.0);
    }
}


