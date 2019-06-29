package net.javainthebox.scatterplotter;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        AnchorPane root = FXMLLoader.load(getClass().getResource("ScatterPlotView.fxml"));
        
        Scene scene = new Scene(root);
//        scene.getStylesheets().add(getClass().getResource("chart.css").toExternalForm());
        
        stage.setScene(scene);
        stage.setTitle("Scatter Plot");
        stage.show();
    }
    
    public static void main(String... args) {
        launch(args);
    }
}
