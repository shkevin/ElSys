package ElSys.operations.cabin;

public class ElButton {

    private boolean pressed;

    public ElButton(){
        this.pressed = false;
    }

    public boolean getPressed(){
        return this.pressed;
    }

    public void setPressed(boolean press) {
        this.pressed = press;
    }

}
