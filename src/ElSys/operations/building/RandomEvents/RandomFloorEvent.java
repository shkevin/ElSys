package ElSys.operations.building.RandomEvents;

import ElSys.Controller;
import ElSys.operations.building.BuildSpecs;
import ElSys.operations.cabin.Cabin;
import javafx.scene.control.Button;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomFloorEvent implements Runnable {

    private Controller controller;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final List<Button> upButtons;
    private final List<Button> downButtons;

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
                Thread.sleep(5000);
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
        int floor = random.nextInt(BuildSpecs.MAX_FLOORS);
        int elevator = random.nextInt(cabins.size());
        int upOrDownVal = random.nextInt(2);
        String upOrDown;

        if (upOrDownVal == 0) {
            upOrDown = "UP";
            System.out.println("Floor " + floor + " going " + "up");
        } else {
            upOrDown = "DOWN";
            System.out.println("Floor " + floor + " going " + "down");
        }

        switch (upOrDown) {
            case "UP":
                System.out.println("UP");
                break;
            case "DOWN":
                System.out.println("DOWN");
                break;
            default:
                System.out.println("Error");
                break;
        }


    }
}
