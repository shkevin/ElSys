package ElSys.operations.cabin;

/*
 * This Enum represents all the possible states of motion for the Elevator to be in.
 * I added these because we might add something like speeding up and slowing down.
 */

public enum MotionTypes {
    MOVINGUP,
    MOVINGDOWN,
    WAITING,
    MOVING,
    NOTMOVING;

    public double toVal() {
        switch(this) {
            case MOVINGUP:
                return 1.0;
            case MOVINGDOWN:
                return -1.0;
            case NOTMOVING:
                return 0.0;
            default:
                return 0.0;
        }
    }
}
