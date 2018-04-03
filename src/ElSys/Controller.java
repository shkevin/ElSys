package ElSys;

import ElSys.operations.building.buildingCanvas;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import ElSys.operations.building.buildingHandler;

public class Controller {

	@FXML private BorderPane pane = new BorderPane();
	private buildingCanvas canvas = new buildingCanvas(this);
	private buildingHandler handler = new buildingHandler(canvas, this);
	private Pane canvasPane = new Pane();

	@FXML
	public void initialize() {
		setupCanvas();
	}

	private void setupCanvas() {
		canvas.widthProperty().bind(canvasPane.widthProperty());
		canvas.heightProperty().bind(canvasPane.heightProperty());

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				canvas.drawCanvas();
			}
		}.start();
		canvasPane.addEventFilter(MouseEvent.ANY, handler.getOnMouseEventHandler());

		canvasPane.getChildren().add(canvas);
		pane.setCenter(canvasPane);
	}


}
