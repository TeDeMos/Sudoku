package com.example.sudoku;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SudokuController {
    private static final Background unselected =
            new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background selectedMain =
            new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY));
    private static final Background selectedSecondary =
            new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    public GridPane mainGrid;
    public HBox numbers;
    public HBox control;
    private Label[][] labels;
    private int yPrevClicked;
    private int xPrevClicked;
    private int[][] gameState;

    @FXML
    public void initialize() {
        gameState = new int[9][9];
        Node[] nodes = new Node[9];
        labels = new Label[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Label l = new Label();
                double top = j % 3 == 0 ? 2 : 1;
                double bottom = j % 3 == 2 ? 2 : 1;
                double left = i % 3 == 0 ? 2 : 1;
                double right = i % 3 == 2 ? 2 : 1;
                l.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                        new BorderWidths(top, right, bottom, left))));
                l.alignmentProperty().setValue(Pos.CENTER);
                l.onMouseClickedProperty().setValue(this::gridMouseClick);
                l.idProperty().setValue(String.format("L%s%s", i, j));
                nodes[j] = l;
                labels[i][j] = l;
            }
            mainGrid.addColumn(i, nodes);
        }
        for (int i = 0; i < 10; i++) {
            Button b = new Button(i > 0 ? String.valueOf(i) : "\uD83D\uDD19");
            b.idProperty().setValue(String.format("B%s", i));
            b.setOnAction(this::numberButtonClick);
            numbers.getChildren().add(b);
        }
    }

    public void gridMouseClick(MouseEvent e) {
        int pos = Integer.parseInt(((Label)e.getSource()).getId().substring(1));
        int x = pos / 10;
        int y = pos % 10;
        selectCell(x, y);
    }

    public void numberButtonClick(ActionEvent e) {
        int number = Integer.parseInt(((Button)e.getSource()).getId().substring(1));
        setNumber(xPrevClicked, yPrevClicked, number);
    }

    public void keyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        if (code.isDigitKey()) {
            setNumber(xPrevClicked, yPrevClicked, Integer.parseInt(code.getChar()));
            return;
        }
        switch (e.getCode()) {
            case A -> selectCell(Math.max(xPrevClicked - 1, 0), yPrevClicked);
            case D -> selectCell(Math.min(xPrevClicked + 1, 8), yPrevClicked);
            case W -> selectCell(xPrevClicked, Math.max(yPrevClicked - 1, 0));
            case S -> selectCell(xPrevClicked, Math.min(yPrevClicked + 1, 8));
            case BACK_SPACE -> setNumber(xPrevClicked, yPrevClicked, 0);
        }
    }

    private void setNumber(int x, int y, int number) {
        gameState[x][y] = number;
        labels[x][y].setText(number > 0 ? String.valueOf(number) : "");
    }

    private void selectCell(int x, int y) {
        setBackgrounds(xPrevClicked, yPrevClicked, unselected, unselected);
        setBackgrounds(x, y, selectedMain, selectedSecondary);
        yPrevClicked = y;
        xPrevClicked = x;
    }

    private void setBackgrounds(int x, int y, Background main, Background secondary) {
        for (int i = 0; i < 9; i++) {
            labels[x][i].setBackground(secondary);
            labels[i][y].setBackground(secondary);
        }
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                labels[x / 3 * 3 + i][y / 3 * 3 + j].setBackground(secondary);
        labels[x][y].setBackground(main);
    }

    public void setBinding(Stage stage) {
        stage.addEventHandler(KeyEvent.KEY_PRESSED, this::keyPressed);
        ReadOnlyDoubleProperty width = stage.widthProperty();
        ReadOnlyDoubleProperty height = stage.heightProperty();
        NumberBinding gridSide = Bindings.min(width.subtract(20), height.subtract(50).multiply(9).divide(11));
        NumberBinding labelSide = gridSide.divide(9).add(1);
        NumberBinding buttonHeight = gridSide.subtract(110).divide(10);
        NumberBinding bottomButtonWidth = gridSide.subtract(40).divide(4);
        StringExpression buttonFontSize = Bindings.concat("-fx-font-size: ", buttonHeight.multiply(2).divide(5), ";");
        StringExpression labelFontSize = Bindings.concat("-fx-font-size: ", labelSide.multiply(2).divide(3), ";");
        for (Node n : control.getChildren()) {
            Button b = (Button)n;
            b.prefWidthProperty().bind(bottomButtonWidth);
            b.prefHeightProperty().bind(buttonHeight);
            b.styleProperty().bind(buttonFontSize);
        }
        for (Node n : numbers.getChildren()) {
            Button b = (Button)n;
            b.prefWidthProperty().bind(buttonHeight);
            b.prefHeightProperty().bind(buttonHeight);
            b.styleProperty().bind(buttonFontSize);
        }
        for (Node n : mainGrid.getChildren()) {
            Label l = (Label)n;
            l.prefWidthProperty().bind(labelSide);
            l.prefHeightProperty().bind(labelSide);
            l.styleProperty().bind(labelFontSize);
        }
    }
}