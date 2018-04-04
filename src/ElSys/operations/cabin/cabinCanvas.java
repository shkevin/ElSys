package ElSys.operations.cabin;

import javafx.animation.FadeTransition;
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

	private List<Cabin> cabins;
	private GraphicsContext gc;
	private List<Image> imageList;
	private ImageView image;
	private int scaleHeight = 60;
	private int scaleWidth = 60;
	private FadeTransition fade;

	public cabinCanvas(int numCells, List<Cabin> cabins) {
		this.cabins = cabins;
		widthProperty().addListener(event -> drawCanvas(0));
		heightProperty().addListener(event -> drawCanvas(0));

		image = new ImageView();
		imageList = new ArrayList<>(numCells);
		setImages();
	}

	/*
	* Added with for loop didn't ensure correct indexing.
	*/
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

	public void drawCanvas(int cabin) {
		double w = widthProperty().get();
		double h = heightProperty().get();
		double centerX = (this.getWidth()/2) - 30;
		double botY = 60;

		gc = getGraphicsContext2D();
		gc.clearRect(0, 0, w, h);
		image.setImage(imageList.get(0));
		image.setSmooth(true);

		if (cabins != null) {
			image.setImage(imageList.get(cabins.get(cabin).getFloor() - 1));
			gc.drawImage(image.snapshot(null, null),centerX , botY);

			MotionTypes cabinMoving = MotionTypes.NOTMOVING;
			Motion cabinMotion = cabins.get(cabin).getMotion();

			if (cabinMotion != null) {
				cabinMoving = cabinMotion.getMotionType();
			}
			if (cabinMoving != MotionTypes.NOTMOVING) {
				if (cabinMoving == MotionTypes.MOVINGUP) {
					image.setImage(imageList.get(10));
					gc.drawImage(image.snapshot(null, null),centerX - 80, botY - 10);
				}
				else if (cabinMoving == MotionTypes.MOVINGDOWN) {
					image.setImage(imageList.get(11));
					gc.drawImage(image.snapshot(null, null),centerX - 80, botY - 10);
				}
			}
		}
		else gc.drawImage(image.snapshot(null, null), centerX, botY);
	}

}
