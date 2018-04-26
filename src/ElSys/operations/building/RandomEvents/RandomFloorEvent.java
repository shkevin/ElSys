package ElSys.operations.building.RandomEvents;

import ElSys.Controller;
import ElSys.operations.building.BuildSpecs;
import ElSys.operations.cabin.Cabin;
import javafx.scene.control.Button;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomFloorEvent implements Runnable {

    private Controller controller;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final List<Button> upButtons;
    private final List<Button> downButtons;
    private final int sleepVal = 2000;

    public RandomFloorEvent(Controller controller) {
        this.controller = controller;
        upButtons = Controller.getFloorUpButtonList();
        downButtons = Controller.getFloorDownButtonList();
    }

    public void start() {
        Thread worker = new Thread(this);
        this.running.set(true);
        worker.start();
    }

    public void stop() {
        running.set(false);
    }

    @Override
    public void run() {

        while (running.get()) {

            try {
                Thread.sleep(sleepVal);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                stop();
            }
            setRandomFloor();
        }
    }

    private void setRandomFloor() {
        Random random = new Random();
        List<Cabin> cabins = controller.getCabins();
        int floor;
        int elevator = random.nextInt(cabins.size());
        int upOrDownVal = random.nextInt(2);
        String upOrDown;

        //Get the direction
        if (upOrDownVal == 0) upOrDown = "UP";
        else upOrDown = "DOWN";

        //Complete a random floor request (up or down button)
        switch (upOrDown) {
            case "UP":
                floor = ThreadLocalRandom.current().nextInt(1, BuildSpecs.MAX_FLOORS - 1);
//                System.out.println("Test: " + upButtons.get(floor - 1).getText() + " going up");
//                System.out.println("Floor " + floor + " going " + "up");
                upButtons.get(floor - 1).fire();
                break;
            case "DOWN":
                floor = ThreadLocalRandom.current().nextInt(2, BuildSpecs.MAX_FLOORS);
//                System.out.println("Test: " + downButtons.get(floor - 1).getText() + " going down");
//                System.out.println("Floor " + floor + " going " + "down");
                downButtons.get(floor - 1).fire();
                break;
            default:
                System.out.println("Error");
                break;
        }


    }
}
