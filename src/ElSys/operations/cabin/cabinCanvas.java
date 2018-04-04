package ElSys.operations.cabin;

import ElSys.Controller;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin
 */
public class cabinCanvas extends Canvas {

	private GraphicsContext gc;
	private List<Image> imageList;
	private ImageView image;
//	private int scaleHeight = 20;
//	private int scaleWidth = 20;

	public cabinCanvas(int numCells) {
		widthProperty().addListener(event -> drawCanvas(null));
		heightProperty().addListener(event -> drawCanvas(null));

		image = new ImageView();
		imageList = new ArrayList<>(numCells);
		setImages();

	}

	private void setImages() {
		imageList.add(new Image("/numbers/1.png"));
		imageList.add(new Image("/numbers/2.png"));
		imageList.add(new Image("/numbers/3.png"));
		imageList.add(new Image("/numbers/4.png"));
		imageList.add(new Image("/numbers/5.png"));
		imageList.add(new Image("/numbers/6.png"));
		imageList.add(new Image("/numbers/7.png"));
		imageList.add(new Image("/numbers/8.png"));
		imageList.add(new Image("/numbers/9.png"));
		imageList.add(new Image("/numbers/10.png"));
		imageList.add(new Image("/numbers/up.png"));
		imageList.add(new Image("/numbers/down.png"));
	}

	public void drawCanvas(Cabin cabin) {
		double w = widthProperty().get();
		double h = heightProperty().get();
		double centerX = (this.getWidth()/2) - 25;
		double botY = 70;

		gc = getGraphicsContext2D();
		gc.clearRect(0, 0, w, h);
		image.setImage(imageList.get(0));
		image.setSmooth(true);

		if (cabin != null) {
			image.setImage(imageList.get(cabin.getFloor() - 1));
			gc.drawImage(image.snapshot(null, null),centerX , botY);
		}
		else gc.drawImage(image.snapshot(null, null), centerX, botY);
	}

}
