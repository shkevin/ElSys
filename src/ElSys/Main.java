package ElSys;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

		public static final int WIDTH = 800;
		public static final int HEIGHT = 900;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("ElSys");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("elsys.fxml"));
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
