package ElSys.operations.cabin;

/*
 * This Enum represents all the possible states of motion for the Elevator to be in.
 * I added these because we might add something like speeding up and slowing down.
 */

public enum MotionTypes {
    MOVINGUP,
    MOVINGDOWN,
    NOTMOVING,
    DOORSCLOSING,
    DOORSOPENING;

    public double toVal() {
        switch(this) {
            case MOVINGUP:
                return 0.1;
            case MOVINGDOWN:
                return -0.1;
            case NOTMOVING:
                return 0.0;
            default:
                return 0.0;
        }
    }


}
