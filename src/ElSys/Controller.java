package ElSys;

import ElSys.operations.building.buildingCanvas;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import ElSys.operations.building.buildingHandler;

public class Controller {

	@FXML private BorderPane pane = new BorderPane();
	@FXML private ComboBox<String> elevatorCombo = new ComboBox<>();
	private buildingCanvas canvas = new buildingCanvas(this);
	private buildingHandler handler = new buildingHandler(canvas, this);
	private Pane canvasPane = new Pane();
	private ObservableList<String> elevatorList;

	@FXML
	public void initialize() {
		setupCanvas();
		createCombo();
	}

	private void createCombo() {
		elevatorList = FXCollections.observableArrayList("Elevator: 1",
						"Elevator: 2", "Elevator: 3", "Elevator: 4");
		elevatorCombo.setItems(elevatorList);
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
