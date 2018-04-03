package ElSys.interfaces;

/*
 * FloorAlignment must give the floor number and whether the elevator reached top or bottom floor
 * This should probably have 'increment floor' 'decrement floor' methods or something... i'm not really sure
 * how we are supposed to get a value from this. from the controller maybe
 */

public class FloorAlignment {

    private int floor;

    public void setFloor(int n) {
        this.floor = n;
    }

    public int getFloor() {
        return floor;
    }
}