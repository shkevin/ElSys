package ElSys.operations.building;

import ElSys.operations.cabin.ServiceDirection;

public class request {
    Integer floor;
    ServiceDirection requestDirection = ServiceDirection.NONE;

    public request(Integer floor){
        this.floor = floor;
    }

    public request(Integer floor, ServiceDirection direction){
        this.floor = floor;
        this.requestDirection = direction;
    }

    public void setServiceDirection(ServiceDirection direction){
        this.requestDirection = direction;
    }

    public ServiceDirection getRequestDirection(){
        return requestDirection;
    }

    public int getFloor(){return floor;}
}
