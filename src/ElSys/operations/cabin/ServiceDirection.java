package ElSys.operations.cabin;

import ElSys.operations.building.request;

import java.util.Comparator;
import java.lang.Integer;

public enum ServiceDirection {
    UP,
    DOWN,
    NONE;

    public Comparator<request> getComparator(Object floorLock, Integer currentFloor){
        Comparator<request> floorComparator;

        switch(this){
            case UP:
                floorComparator = new Comparator<request>() {
                    @Override
                    public int compare(request o1, request o2) {
                        Integer first = o1.getFloor();
                        Integer second = o2.getFloor();
                        synchronized (floorLock) {
                            if (first <= currentFloor) {
                                return 1;
                            } else {
                                return first.compareTo(second);
                            }
                        }
                    }
                };

                break;
            case DOWN:
                floorComparator = new Comparator<request>() {
                    @Override
                    public int compare(request o1, request o2) {
                        Integer first = o1.getFloor();
                        Integer second = o2.getFloor();
                        synchronized (floorLock) {
                            if (first >= currentFloor) {
                                return 1;
                            } else {
                                return second.compareTo(first);
                            }
                        }
                    }
                };
                break;
            default:
                floorComparator = new Comparator<request>() {
                    @Override
                    public int compare(request o1, request o2) {
                        Integer first = o1.getFloor();
                        Integer second = o2.getFloor();
                        synchronized (floorLock) {
                            if (first >= currentFloor) {
                                return 1;
                            } else {
                                return second.compareTo(first);
                            }
                        }
                    }
                };
        }

        return floorComparator;
    }

    public String toString(){
        switch(this){
            case UP: return "UP";
            case DOWN: return "DOWN";
            default: return "NONE";
        }
    }
    public boolean valid(int floorRequest, int cabFloor){
        switch(this){
            case UP:
                return cabFloor < floorRequest;
            case DOWN:
                return cabFloor > floorRequest;
            default:
                return true;
        }
    }
}
