package ElSys;

import ElSys.operations.building.buildSpecs;
import ElSys.operations.building.buildingCanvas;
import ElSys.operations.building.buildingHandler;
import ElSys.operations.cabin.Cabin;
import ElSys.operations.cabin.cabinCanvas;
import ElSys.operations.cabin.ElButton;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import ElSys.interfaces.*;

public class Controller {

	@FXML private BorderPane buildingPane = new BorderPane();
	@FXML private AnchorPane cabinPane = new AnchorPane();
	@FXML private AnchorPane buttonPane = new AnchorPane();
	@FXML private Button button1;
	@FXML private Button button2;
	@FXML private Button button3;
	@FXML private Button button4;
	@FXML private Button button5;
	@FXML private Button button6;
	@FXML private Button button7;
	@FXML private Button button8;
	@FXML private Button button9;
	@FXML private Button button10;



	public ComboBox<String> elevatorCombo = new ComboBox<>();
	static ArrayList<Button> buttonList = new ArrayList<>(buildSpecs.MAX_FLOORS);
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
		setupButtons();
	}

	@FXML
	private void lock() {
		cabins.get(elevatorCombo.getSelectionModel().getSelectedIndex()).setIsLocked(true);
		buttonList.forEach(button -> button.setDisable(true));
	}

	@FXML
	private void unlock() {
		cabins.get(elevatorCombo.getSelectionModel().getSelectedIndex()).setIsLocked(false);
		buttonList.forEach(button -> button.setDisable(false));
	}

	@FXML
	private void selectElevator() {
		if (cabins.get(elevatorCombo.getSelectionModel().getSelectedIndex()).getIsLocked()) {
			buttonList.forEach(button -> button.setDisable(true));
		}
		else buttonList.forEach(button -> button.setDisable(false));
	}

	private void setupButtons() {

		buttonList.add(button1);
		buttonList.add(button2);
		buttonList.add(button3);
		buttonList.add(button4);
		buttonList.add(button5);
		buttonList.add(button6);
		buttonList.add(button7);
		buttonList.add(button8);
		buttonList.add(button9);
		buttonList.add(button10);

		for (Button button : buttonList) {
			button.setOnAction(handler.getOnButtonEventHandler());
		}
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
			@Override
			public void handle(long now) {
				cabinCanvas.drawCanvas(elevatorCombo.getSelectionModel().getSelectedIndex());
				ArrayList<ElButton> buttons = cabins.get(elevatorCombo.getSelectionModel().getSelectedIndex()).getButtons();

				for (int i = 0; i < 10; i++) {
					if (buttons.get(i).getPressed()) {
						buttonList.get(i).setStyle("-fx-body-color: #ffff00;"); //set to yellow
					} else {
						buttonList.get(i).setStyle("-fx-body-color: linear-gradient(to bottom,derive(#d0d0d0,34%) 0%, derive(#d0d0d0,-18%) 100%);"); //set back to default
					}
				}
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

	public List<Button> getElevatorButtons() {
		return buttonList;
	}

	public List<Cabin> getCabins() {
		return cabins;
	}

}
