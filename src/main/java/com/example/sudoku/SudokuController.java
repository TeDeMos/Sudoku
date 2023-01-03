package com.example.sudoku;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class SudokuController {
    public GridPane mainGrid;
    public HBox numbers;
    public HBox control;

    @FXML
    public void initialize() {
        Node[] nodes = new Node[9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++)
                nodes[j] = new Label(String.format("%s%s", j, i));
            mainGrid.addRow(i, nodes);
        }
        for (int i = 0; i < 9; i++)
            numbers.getChildren().add(new Button(String.valueOf(i + 1)));
    }

    public void setBinding(Stage stage) {
        ReadOnlyDoubleProperty width = stage.widthProperty();
        ReadOnlyDoubleProperty height = stage.heightProperty();
        NumberBinding gridSide = Bindings.min(width.subtract(20), height.subtract(40).multiply(9).divide(11));
        for (Node n : control.getChildren())
            ((Button)n).prefWidthProperty().bind(gridSide.subtract(30).divide(4));
        for (Node n : numbers.getChildren()) {
            Button b = (Button)n;
            b.prefWidthProperty().bind(gridSide.subtract(80).divide(9));
            b.prefHeightProperty().bind(gridSide.subtract(80).divide(9));
        }
        for (int i = 0; i < 9; i++) {
            ColumnConstraints c = new ColumnConstraints();
            RowConstraints r = new RowConstraints();
            c.prefWidthProperty().bind(gridSide.divide(9));
            r.prefHeightProperty().bind(gridSide.divide(9));
            mainGrid.getColumnConstraints().add(c);
            mainGrid.getRowConstraints().add(r);
        }
        mainGrid.minWidthProperty().bind(gridSide);
    }
}