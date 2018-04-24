package ElSys;

import ElSys.operations.building.BuildSpecs;
import ElSys.operations.building.BuildingCanvas;
import ElSys.operations.building.BuildingHandler;
import ElSys.operations.building.RandomFireEvent;
import ElSys.operations.cabin.Cabin;
import ElSys.operations.cabin.CabinCanvas;
import ElSys.operations.cabin.DoorCanvas;
import ElSys.operations.cabin.ElButton;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class Controller {

	@FXML private BorderPane buildingPane;
	@FXML private AnchorPane cabinPane;
	@FXML private AnchorPane doorPane;
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

	@FXML private Button upbutton1;
	@FXML private Button upbutton2;
	@FXML private Button upbutton3;
	@FXML private Button upbutton4;
	@FXML private Button upbutton5;
	@FXML private Button upbutton6;
	@FXML private Button upbutton7;
	@FXML private Button upbutton8;
	@FXML private Button upbutton9;
    @FXML private Button upbutton10;
	@FXML private Button downbutton1;
	@FXML private Button downbutton2;
	@FXML private Button downbutton3;
	@FXML private Button downbutton4;
	@FXML private Button downbutton5;
	@FXML private Button downbutton6;
	@FXML private Button downbutton7;
	@FXML private Button downbutton8;
	@FXML private Button downbutton9;
	@FXML private Button downbutton10;
	@FXML private Button maintenanceKey;
	@FXML private Button fireImage1;
	@FXML private Button fireImage2;

	public ComboBox<String> elevatorCombo = new ComboBox<>();
	static ArrayList<Button> buttonList = new ArrayList<>(BuildSpecs.MAX_FLOORS);
	private static ArrayList<Button> floorUpButtonList = new ArrayList<>(BuildSpecs.MAX_FLOORS);
	private static ArrayList<Button> floorDownButtonList = new ArrayList<>(BuildSpecs.MAX_FLOORS);
	private ArrayList<Cabin> cabins = setupCabins(4);
	private BuildingCanvas BuildingCanvas = new BuildingCanvas(cabins, this);
	private CabinCanvas cabinCanvas = new CabinCanvas(12, cabins);
	private DoorCanvas doorCanvas = new DoorCanvas(cabins, this);
	private BuildingHandler handler = new BuildingHandler(BuildingCanvas, this);
	private Pane buildingCanvasPane = new Pane();
	private Pane cabinCanvasPane = new Pane();
	private RandomFireEvent fireEvent;

	@FXML
	public void initialize() {
		setupBuildingCanvas();
		setupCabinCanvas();
		createCombo();
		setupButtons();
		for (Cabin cab : cabins) {
			cab.setHandler(this.handler);
		}
		//fireEvent = new RandomFireEvent(this);
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
			button.setOnAction(handler.getOnCabinButtonEventHandler());
		}
		
		floorUpButtonList.add(upbutton1);
		floorUpButtonList.add(upbutton2);
		floorUpButtonList.add(upbutton3);
		floorUpButtonList.add(upbutton4);
		floorUpButtonList.add(upbutton5);
		floorUpButtonList.add(upbutton6);
		floorUpButtonList.add(upbutton7);
		floorUpButtonList.add(upbutton8);
		floorUpButtonList.add(upbutton9);
		//floorUpButtonList.add(upbutton10);

		for (Button button : floorUpButtonList) {
			button.setOnAction(handler.getOnFloorUpButtonEventHandler());
		}
		upbutton10.setOnAction(handler.getOnFireAlarmHandler());

		floorDownButtonList.add(downbutton1);
		floorDownButtonList.add(downbutton2);
		floorDownButtonList.add(downbutton3);
		floorDownButtonList.add(downbutton4);
		floorDownButtonList.add(downbutton5);
		floorDownButtonList.add(downbutton6);
		floorDownButtonList.add(downbutton7);
		floorDownButtonList.add(downbutton8);
		floorDownButtonList.add(downbutton9);
		floorDownButtonList.add(downbutton10);

		for (Button button : floorDownButtonList) {
			button.setOnAction(handler.getOnFloorDownButtonEventHandler());
		}

		fireImage1.getStyleClass().clear();
		fireImage2.getStyleClass().clear();
		maintenanceKey.getStyleClass().clear();
		maintenanceKey.setStyle("-fx-background-image: url('/maintenance/0.png'); -fx-background-repeat: no-repeat;");
		maintenanceKey.setOnAction(handler.getMaintenanceKeyHandler());
	}


	private void createCombo() {
		ObservableList<String> elevatorList = FXCollections.observableArrayList("Elevator: 1",
						"Elevator: 2", "Elevator: 3", "Elevator: 4");
		elevatorCombo.setItems(elevatorList);
		elevatorCombo.getSelectionModel().select(0);
	}

	private void setupBuildingCanvas() {
		BuildingCanvas.widthProperty().bind(buildingCanvasPane.widthProperty());
		BuildingCanvas.heightProperty().bind(buildingCanvasPane.heightProperty());
		buildingCanvasPane.addEventFilter(MouseEvent.MOUSE_CLICKED, handler.getOnMouseEventHandler());

		doorCanvas.addEventFilter(MouseEvent.MOUSE_CLICKED, handler.getOnMouseEventHandlerDoors());

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				BuildingCanvas.drawCanvas(cabins);
			}
		}.start();

		buildingCanvasPane.getChildren().add(BuildingCanvas);
		buildingPane.setCenter(buildingCanvasPane);
	}

	private void setupCabinCanvas() {
		cabinCanvas.widthProperty().bind(cabinPane.widthProperty());
		cabinCanvas.heightProperty().bind(cabinPane.heightProperty());
		doorCanvas.widthProperty().bind(doorPane.widthProperty());
        doorCanvas.heightProperty().bind(doorPane.heightProperty());

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				int elevator = elevatorCombo.getSelectionModel().getSelectedIndex();
				cabinCanvas.drawCanvas(elevator);
				doorCanvas.drawDoors(elevator);
				ArrayList<ElButton> buttons = cabins.get(elevator).getButtons();

				for (int i = 0; i < 10; i++) {
					if (buttons.get(i).getPressed()) {
						buttonList.get(i).setStyle("-fx-body-color: #ffff00;"); //set to yellow
					} else {
						buttonList.get(i).setStyle("-fx-body-color: linear-gradient(to bottom,derive(#d0d0d0,34%) 0%, derive(#d0d0d0,-18%) 100%);"); //set back to default
					}
				}

				for (int i = 0; i < 9; i++) {
					if (handler.getUpButtonList().get(i).getPressed()) {
						floorUpButtonList.get(i).setStyle("-fx-body-color: #ffff00;"); //set to yellow
					} else {
						floorUpButtonList.get(i).setStyle("-fx-body-color: linear-gradient(to bottom,derive(#d0d0d0,34%) 0%, derive(#d0d0d0,-18%) 100%);"); //set back to default
					}
				}

				for (int i = 0; i < 10; i++) {
					if (handler.getDownButtonList().get(i).getPressed()) {
						floorDownButtonList.get(i).setStyle("-fx-body-color: #ffff00;"); //set to yellow
					} else {
						floorDownButtonList.get(i).setStyle("-fx-body-color: linear-gradient(to bottom,derive(#d0d0d0,34%) 0%, derive(#d0d0d0,-18%) 100%);"); //set back to default
					}
				}

				if (cabins.get(elevator).getFireAlarm()){
					fireImage1.setStyle("-fx-background-image: url('/fire/redalarm.jpg'); -fx-background-repeat: no-repeat;");
					fireImage2.setStyle("-fx-background-image: url('/fire/redalarm.jpg'); -fx-background-repeat: no-repeat;");
				} else {
					fireImage1.setStyle(null);
					fireImage2.setStyle(null);
				}

				if (cabins.get(elevator).getMaintenance()) {
					maintenanceKey.setStyle("-fx-background-image: url('/maintenance/1.png'); -fx-background-repeat: no-repeat;");
				} else {
					maintenanceKey.setStyle("-fx-background-image: url('/maintenance/0.png'); -fx-background-repeat: no-repeat;");
				}

			}
		}.start();

		cabinCanvasPane.getChildren().add(cabinCanvas);
		cabinPane.getChildren().add(cabinCanvasPane);
		doorPane.getChildren().add(doorCanvas);
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

    public Button getUpbutton10() {
        return upbutton10;
    }

}
