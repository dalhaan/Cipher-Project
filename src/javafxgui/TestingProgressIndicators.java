package javafxgui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URISyntaxException;

/**
 * Created by Sirius on 14/06/2017.
 */
public class TestingProgressIndicators extends Application {
    public static void main(String[] args) throws URISyntaxException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Setup window
        Scene scene = new Scene(null);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
