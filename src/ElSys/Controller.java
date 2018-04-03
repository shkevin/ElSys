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
import java.util.ArrayList;
import ElSys.operations.cabin.Cabin;


public class Controller {

	@FXML private BorderPane pane = new BorderPane();
	@FXML private ComboBox<String> elevatorCombo = new ComboBox<>();
	private ArrayList<Cabin> cabins = setupCabins(4);
	private buildingCanvas canvas = new buildingCanvas(cabins, this);
	private buildingHandler handler = new buildingHandler(canvas, this);
	private Pane canvasPane = new Pane();

	@FXML
	public void initialize() {
		setupCanvas();
		createCombo();
	}

	private void createCombo() {
		ObservableList<String> elevatorList = FXCollections.observableArrayList("Elevator: 1",
						"Elevator: 2", "Elevator: 3", "Elevator: 4");
		elevatorCombo.setItems(elevatorList);
	}

	private void setupCanvas() {
		canvas.widthProperty().bind(canvasPane.widthProperty());
		canvas.heightProperty().bind(canvasPane.heightProperty());

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				canvas.drawCanvas(cabins);
			}
		}.start();
		canvasPane.addEventFilter(MouseEvent.MOUSE_CLICKED, handler.getOnMouseEventHandler());

		canvasPane.getChildren().add(canvas);
		pane.setCenter(canvasPane);
	}

	private ArrayList<Cabin> setupCabins(int numberOfCabins) {
		cabins = new ArrayList<Cabin>();
		for (int i = 0; i < numberOfCabins; i++)
		{
			cabins.add(new Cabin());
		}
		return cabins;
	}

	//This will eventually be called by the scheduler that handles cabin and floor requests,
	//but for now it call the startMotion function, which moves the elevator based on a GUI click
	public void moveElevator(int elevator, int floor) {
		Cabin cab = cabins.get(elevator);
		cab.startMotion(floor);
		canvas.drawCanvas(cabins);
		System.out.println("Elevator " + elevator + " now at: " + cab.getFloor());
		System.out.println();
	}

}
