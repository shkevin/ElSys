package ElSys.interfaces;

public class motor {

    private double distance = 0;

    public motor(){

    }

    synchronized public void move(double velocity){
        distance += velocity;
    }

    synchronized public double getDistance(){
        double temp = distance;
        distance = 0;
        return temp;
    }
}
