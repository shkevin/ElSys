package ElSys.operations.cabin;

/*
 * This Enum represents all the possible states of motion for the Elevator to be in.
 * I added these because we might add something like speeding up and slowing down.
 */

public enum MotionTypes {
    MOVINGUP,
    MOVINGDOWN,
    NOTMOVING;

    @Override
    public String toString() {
        switch(this) {
            case MOVINGUP:
                return "Up";
            case MOVINGDOWN:
                return "Down";
            case NOTMOVING:
                return "Still";
            default:
                return null;
        }
    }
}
