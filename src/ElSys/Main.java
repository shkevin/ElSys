package ElSys;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

		public static final int WIDTH = 800;
		public static final int HEIGHT = 900;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("ElSys");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("elsys.fxml"));
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(925);
        primaryStage.show();

        /* The program will close all threads when the window is closed */
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
