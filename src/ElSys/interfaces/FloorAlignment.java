package ElSys.interfaces;
import ElSys.operations.cabin.Motion;
/*
 * FloorAlignment must give the floor number and whether the elevator reached top or bottom floor
 * This should probably have 'increment floor' 'decrement floor' methods or something... i'm not really sure
 * how we are supposed to get a value from this. from the controller maybe
 */

public class FloorAlignment {


    private int floor;
    private Motion MotionController;
    private int height;
    private boolean signal = false;

    public FloorAlignment(){}

    public FloorAlignment(Motion motion){
        MotionController = motion;
        floor = MotionController.getCurrentFloor();
    }

    public int getFloor(){return floor;}

    public void signal(int signaledfloor) {
        this.signal = true;
        //System.out.println("FloorSignalCalled at floor " + signaledfloor);
        this.floor = signaledfloor;
    }

    public boolean check(){
        if(signal){
            signal = false;
            return true;
        }
        return false;
    }


}