package net.javainthebox.scatterplotter;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class ScatterPlotViewController implements Initializable {

    @FXML
    private AnchorPane pane;

    @FXML
    private LineChart<Number, Number> chart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML
    void open(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("CSV Files", "*.csv"),
                new ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(pane.getScene().getWindow());

        // Load CSV file by OpenCSV
        try ( CSVReader reader = new CSVReader(new FileReader(selectedFile))) {
            List<String[]> lines = reader.readAll();

            xAxis.setLabel(lines.get(0)[0]);
            yAxis.setLabel(lines.get(0)[1]);

            List<double[]> plots = lines.stream()
                    .skip(1)
                    .map(l -> Arrays.stream(l).mapToDouble(Double::parseDouble).toArray())
                    .collect(Collectors.toList());

            drawChart(plots);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void exit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void help(ActionEvent event) {
        ButtonType buttonType = new ButtonType("OK", ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Scatter Plotter");
        dialog.setContentText("Scatter Plotter v1.0");
        dialog.getDialogPane().getButtonTypes().add(buttonType);
        dialog.showAndWait();
    }

    private void drawChart(List<double[]> plots) {
        // Plots scatter chart using LineChart
        // w/o lines
        XYChart.Series<Number, Number> series
                = new XYChart.Series<>(FXCollections.observableArrayList(
                        plots.stream()
                                .map(p -> new XYChart.Data<Number, Number>(p[0], p[1]))
                                .collect(Collectors.toList())));

        chart.getData().add(series);

        // Detect linear regreession by Apache Commons Math
        SimpleRegression regression = new SimpleRegression();
        double[][] data = new double[plots.size()][2];
        IntStream.range(0, plots.size())
                .forEach(i -> {
                    data[i][0] = plots.get(i)[0];
                    data[i][1] = plots.get(i)[1];
                });
        regression.addData(data);
        double slope = regression.getSlope();
        double intercept = regression.getIntercept();

        // Plot regression line
        DoubleSummaryStatistics stats
                = plots.stream()
                        .collect(Collectors.summarizingDouble(p -> p[0]));

        XYChart.Series<Number, Number> regresionSeries
                = new XYChart.Series<>();
        regresionSeries.getData().add(new XYChart.Data<>(stats.getMin(), stats.getMin() * slope + intercept));
        regresionSeries.getData().add(new XYChart.Data<>(stats.getMax(), stats.getMax() * slope + intercept));
        chart.getData().add(regresionSeries);
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
    }
}
