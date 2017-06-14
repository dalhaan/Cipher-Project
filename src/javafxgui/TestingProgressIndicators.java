package javafxgui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
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
        ScrollPane scrollPane = new ScrollPane();
        GridPane layout = new GridPane();
        Label fileTitle1 = new Label("File.txt");
        ProgressIndicator progressIndicator1 = new ProgressIndicator(0.3);
        Label fileTitle2 = new Label("File.txt");
        ProgressIndicator progressIndicator2 = new ProgressIndicator(0.3);
        Label fileTitle3 = new Label("File.txt");
        ProgressIndicator progressIndicator3 = new ProgressIndicator(0.3);
        Label fileTitle4 = new Label("File.txt");
        ProgressIndicator progressIndicator4 = new ProgressIndicator(0.3);
        layout.addRow(0, progressIndicator1, fileTitle1);
        layout.addRow(1, progressIndicator2, fileTitle2);
        layout.addRow(2, progressIndicator3, fileTitle3);
        layout.addRow(3, progressIndicator4, fileTitle4);
        scrollPane.setContent(layout);

        // Setup window
        Scene scene = new Scene(scrollPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
