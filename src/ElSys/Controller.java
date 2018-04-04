package ElSys;

import ElSys.operations.building.buildingCanvas;
import ElSys.operations.cabin.cabinCanvas;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import ElSys.operations.building.buildingHandler;
import java.util.ArrayList;
import ElSys.operations.cabin.Cabin;


public class Controller {

	@FXML private BorderPane buildingPane = new BorderPane();
	@FXML private AnchorPane cabinPane = new AnchorPane();
	@FXML private ComboBox<String> elevatorCombo = new ComboBox<>();
	private ArrayList<Cabin> cabins = setupCabins(4);
	private buildingCanvas buildingCanvas = new buildingCanvas(cabins, this);
	private cabinCanvas cabinCanvas = new cabinCanvas(12, cabins);
	private buildingHandler handler = new buildingHandler(buildingCanvas, this);
	private Pane buildingCanvasPane = new Pane();
	private Pane cabinCanvasPane = new Pane();

	@FXML
	public void initialize() {
		setupBuildingCanvas();
		setupCabinCanvas();
		createCombo();
	}

	private void createCombo() {
		ObservableList<String> elevatorList = FXCollections.observableArrayList("Elevator: 1",
						"Elevator: 2", "Elevator: 3", "Elevator: 4");
		elevatorCombo.setItems(elevatorList);
		elevatorCombo.getSelectionModel().select(0);
	}

	private void setupBuildingCanvas() {
		buildingCanvas.widthProperty().bind(buildingCanvasPane.widthProperty());
		buildingCanvas.heightProperty().bind(buildingCanvasPane.heightProperty());
		buildingCanvasPane.addEventFilter(MouseEvent.MOUSE_CLICKED, handler.getOnMouseEventHandler());

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				buildingCanvas.drawCanvas(cabins);
			}
		}.start();

		buildingCanvasPane.getChildren().add(buildingCanvas);
		buildingPane.setCenter(buildingCanvasPane);
	}

	private void setupCabinCanvas() {
		cabinCanvas.widthProperty().bind(cabinPane.widthProperty());
		cabinCanvas.heightProperty().bind(cabinPane.heightProperty());

		new AnimationTimer() {
			public void handle(long now) {
				cabinCanvas.drawCanvas(elevatorCombo.getSelectionModel().getSelectedIndex());
			}
		}.start();

		cabinCanvasPane.getChildren().add(cabinCanvas);
		cabinPane.getChildren().add(cabinCanvasPane);
	}

	private ArrayList<Cabin> setupCabins(int numberOfCabins) {
		cabins = new ArrayList<Cabin>();
		for (int i = 0; i < numberOfCabins; i++)
		{
			cabins.add(new Cabin(i));
		}
		return cabins;
	}

	public void cabinRequest(int elevator, int floor){
		Cabin cab = cabins.get(elevator);
		cab.tryRequest(floor);
	}

}
