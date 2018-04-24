package ElSys.operations.building;

import ElSys.Controller;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

/* *******************************************************************************
 * Creates a new random fire event for the "System control panel" (controller)
 * ******************************************************************************/
public class RandomFireEvent{
    //The time interval is in milliseconds. 20000 = 20 seconds.
    private static final int startInterval = 20000;
    private static final int endInterval = 30000;

    /**
     * Constructor for random fire events. Called in controller class.
     * @param controller for the main GUI. Passed along to inner class.
     */
    public RandomFireEvent(Controller controller) {
        Random random = new Random();
        Timer timer = new Timer();
        timer.schedule(new fireEvent(controller, timer, random), random.nextInt(startInterval));
    }

    /* *******************************************************************************
     * Inner static class for the RandomFireEvent class, this creates a new task for
     * the fire events. The time interval is specified in the final ints startInterval
     * and endInterval.
     * ******************************************************************************/
    static class fireEvent extends TimerTask {
        private Controller controller;
        private Random random;
        private Timer timer;

        /**
         * Creates a fire event in the given final start interval and end interval.
         * @param controller main controller for the GUI.
         * @param timer timer created by the super class.
         * @param random random created by the super class.
         */
        fireEvent(Controller controller, Timer timer, Random random) {
            this.controller = controller;
            this.timer = timer;
            this.random = random;
        }

        @Override
        public void run() {
            System.out.println(new Date());
//            controller.getUpbutton10().fire();
            timer.schedule(new fireEvent(controller,timer, random), ThreadLocalRandom.current().nextInt(startInterval, endInterval));
        }
    }
}
